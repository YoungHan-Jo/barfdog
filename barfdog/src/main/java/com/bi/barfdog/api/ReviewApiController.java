package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.UploadedImageDto;
import com.bi.barfdog.api.resource.CommunityReviewsDtoResource;
import com.bi.barfdog.api.resource.ReviewsDtoResource;
import com.bi.barfdog.api.resource.WriteableReviewsDtoResource;
import com.bi.barfdog.api.reviewDto.*;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.review.Review;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.review.BestReviewRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.service.ReviewService;
import com.bi.barfdog.validator.ReviewValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/reviews", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ReviewApiController {

    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final ReviewValidator reviewValidator;
    private final ReviewImageRepository reviewImageRepository;
    private final BestReviewRepository bestReviewRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping("/writeable")
    public ResponseEntity queryWriteableReviews(@CurrentUser Member member,
                                                Pageable pageable,
                                                PagedResourcesAssembler<QueryWriteableReviewsDto> assembler) {

        Page<QueryWriteableReviewsDto> page = reviewRepository.findWriteableReviewDto(member, pageable);
        PagedModel<EntityModel<QueryWriteableReviewsDto>> pagedModel = assembler.toModel(page, e -> new WriteableReviewsDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-writeable-reviews").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/upload")
    public ResponseEntity uploadImage(@RequestPart MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();

        UploadedImageDto responseDto = reviewService.uploadImage(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewApiController.class).slash("upload");

        EntityModel<UploadedImageDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-reviewImage").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping
    public ResponseEntity writeReview(@CurrentUser Member member,
                                      @RequestBody @Valid WriteReviewDto requestDto,
                                      Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        reviewValidator.validatorId(requestDto, errors);
        if(errors.hasErrors()) return badRequest(errors);

        reviewService.writeReview(member, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewApiController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewApiController.class).withRel("query_reviews"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-write-review").withRel("profile"));

        return ResponseEntity.created(linkTo(ReviewApiController.class).toUri()).body(representationModel);
    }

    @GetMapping
    public ResponseEntity queryReviews(@CurrentUser Member member,
                                       Pageable pageable,
                                       PagedResourcesAssembler<QueryReviewsDto> assembler) {
        Page<QueryReviewsDto> page = reviewRepository.findReviewsDtoByMember(pageable, member);

        PagedModel<EntityModel<QueryReviewsDto>> pagedModel = assembler.toModel(page, e -> new ReviewsDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-reviews").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}/images")
    public ResponseEntity queryReviewImages(@PathVariable Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if(!optionalReview.isPresent()) return notFound();

        List<QueryReviewImagesDto> responseDto = reviewImageRepository.findImagesDtoByReview(optionalReview.get());

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewApiController.class).slash(id).slash("images");

        CollectionModel<QueryReviewImagesDto> collectionModel = CollectionModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-review-images").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryReview(@PathVariable Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if(!optionalReview.isPresent()) return notFound();

        QueryReviewDto responseDto = reviewRepository.findReviewDtoById(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewApiController.class).slash(id);

        EntityModel<QueryReviewDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update_review"),
                profileRootUrlBuilder.slash("index.html#resources-query-review").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteReview(@CurrentUser Member member,
                                       @PathVariable Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (!optionalReview.isPresent()) return notFound();
        Review review = optionalReview.get();
        if (review.getMember() != member) return forbidden();

        reviewService.deleteReview(review);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewApiController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewApiController.class).withRel("query_reviews"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-review").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateReview(@CurrentUser Member member,
                                       @PathVariable Long id,
                                       @RequestBody @Valid UpdateReviewDto requestDto,
                                       Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if(!optionalReview.isPresent()) return notFound();
        Review review = optionalReview.get();
        if (review.getMember() != member) return forbidden();

        reviewService.updateReview(id, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewApiController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewApiController.class).withRel("query_reviews"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-review").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/best")
    public ResponseEntity queryBestReviews() {
        List<QueryBestReviewsDto> responseDto = bestReviewRepository.findBestReviewsDto();

        List<EntityModel<QueryBestReviewsDto>> entityModelList = new ArrayList<>();

        for (QueryBestReviewsDto dto : responseDto) {
            EntityModel<QueryBestReviewsDto> entityModel = EntityModel.of(dto,
                    linkTo(ReviewApiController.class).slash(dto.getId()).slash("community").withRel("query_review_community")
            );
            entityModelList.add(entityModel);
        }

        CollectionModel<EntityModel<QueryBestReviewsDto>> collectionModel = CollectionModel.of(entityModelList,
                linkTo(ReviewApiController.class).slash("best").withSelfRel(),
                linkTo(ReviewApiController.class).slash("community").withRel("query_reviews_community"),
                profileRootUrlBuilder.slash("index.html#resources-query-best-reviews").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/community")
    public ResponseEntity queryReviewsCommunity(Pageable pageable,
                                                PagedResourcesAssembler<QueryCommunityReviewsDto> assembler) {

        Page<QueryCommunityReviewsDto> page = reviewRepository.findCommunityReviewsDto(pageable);

        PagedModel<CommunityReviewsDtoResource> pagedModel = assembler.toModel(page, e -> new CommunityReviewsDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-reviews-community").withRel("profile"));


        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}/community")
    public ResponseEntity queryReviewCommunity(@PathVariable Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (!optionalReview.isPresent()) return notFound();

        QueryCommunityReviewDto responseDto = reviewRepository.findCommunityReviewDtoById(id);

        EntityModel<QueryCommunityReviewDto> entityModel = EntityModel.of(responseDto,
                linkTo(ReviewApiController.class).slash(id).slash("community").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-review-community").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }




    private ResponseEntity forbidden() {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }


    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
