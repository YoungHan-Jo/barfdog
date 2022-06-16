package com.bi.barfdog.api;

import com.bi.barfdog.api.resource.ReviewsAdminDtoResource;
import com.bi.barfdog.api.reviewDto.*;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.review.BestReview;
import com.bi.barfdog.domain.review.Review;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/reviews", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ReviewAdminController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final ReviewValidator reviewValidator;
    private final BestReviewRepository bestReviewRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping
    public ResponseEntity queryReviewsByCategory(Pageable pageable,
                                                 PagedResourcesAssembler<QueryAdminReviewsDto> assembler,
                                                 @ModelAttribute @Valid AdminReviewsCond cond,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return badRequest(bindingResult);

        Page<QueryAdminReviewsDto> page = reviewRepository.findAdminReviewsDto(pageable, cond);

        PagedModel<EntityModel<QueryAdminReviewsDto>> pagedModel = assembler.toModel(page, e -> new ReviewsAdminDtoResource(e));
        pagedModel.add(linkTo(ReviewAdminController.class).slash("approval").withRel("approve_reviews"));
        pagedModel.add(linkTo(ReviewAdminController.class).slash("best").withRel("create_best_reviews"));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-reviews").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @PutMapping("/approval")
    public ResponseEntity approvalReviews(@RequestBody @Valid ReviewIdListDto requestDto,
                                          Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        reviewValidator.validateExists(requestDto.getReviewIdList(), errors);
        if(errors.hasErrors()) return badRequest(errors);

        reviewService.approvalReviews(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewAdminController.class).slash("approval");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewAdminController.class).withRel("query_reviews"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-approve-reviews").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteReview(@PathVariable Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (!optionalReview.isPresent()) return notFound();

        reviewService.deleteReview(optionalReview.get());

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewAdminController.class).withRel("query_reviews"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-delete-review").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/best")
    public ResponseEntity createBestReview(@RequestBody @Valid ReviewIdListDto requestDto,
                                           Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        reviewValidator.validateExists(requestDto.getReviewIdList(), errors);
        if(errors.hasErrors()) return badRequest(errors);

        reviewService.createBestReviews(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewAdminController.class).slash("best");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewAdminController.class).withRel("query_reviews"));
        representationModel.add(linkTo(ReviewAdminController.class).slash("best").withRel("query_best_reviews"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-best-review").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }


    @GetMapping("/{id}")
    public ResponseEntity queryReview(@PathVariable Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (!optionalReview.isPresent()) return notFound();

        QueryAdminReviewDto responseDto = reviewRepository.findAdminReviewDto(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewAdminController.class).slash(id);

        EntityModel<QueryAdminReviewDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(ReviewAdminController.class).slash("best").withRel("create_best_reviews"),
                linkTo(ReviewAdminController.class).slash("approval").withRel("approve_reviews"),
                linkTo(ReviewAdminController.class).slash(id).slash("return").withRel("return_review"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-review").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity returnReview(@PathVariable Long id,
                                       @RequestBody @Valid ReturnReviewDto requestDto,
                                       Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (!optionalReview.isPresent()) return notFound();
        reviewValidator.validateReturn(optionalReview.get(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        reviewService.returnReview(id, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewAdminController.class).slash(id).slash("return");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewAdminController.class).slash(id).withRel("query_review"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-return-review").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/best")
    public ResponseEntity queryBestReviews() {

        List<QueryAdminBestReviewsDto> responseDto = bestReviewRepository.findAdminBestReviewsDto();

        List<EntityModel<QueryAdminBestReviewsDto>> entityModels = new ArrayList<>();

        for (QueryAdminBestReviewsDto dto : responseDto) {
            EntityModel<QueryAdminBestReviewsDto> entityModel = EntityModel.of(dto,
                    linkTo(ReviewAdminController.class).slash(dto.getReviewId()).withRel("query_review"),
                    linkTo(ReviewAdminController.class).slash(dto.getId()).slash("best").withRel("delete_best_review")
            );
            entityModels.add(entityModel);
        }

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewAdminController.class).slash("best");

        CollectionModel<EntityModel<QueryAdminBestReviewsDto>> collectionModel = CollectionModel.of(entityModels,
                selfLinkBuilder.withSelfRel(),
                linkTo(ReviewAdminController.class).slash("leakedOrder").withRel("update_leakedOrder"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-best-reviews").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @DeleteMapping("/{id}/best")
    public ResponseEntity deleteBestReview(@PathVariable Long id) {
        Optional<BestReview> optionalBestReview = bestReviewRepository.findById(id);
        if(!optionalBestReview.isPresent()) return notFound();

        reviewService.deleteBestReview(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewAdminController.class).slash(id).slash("best");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewAdminController.class).slash("best").withRel("query_best_reviews"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-best-reviews").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PutMapping("/best/leakedOrder")
    public ResponseEntity updateBestReviewLeakedOrder(@RequestBody @Valid UpdateBestReviewLeakedOrderDto requestDto,
                                                      Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        reviewValidator.validateExistsBestReviews(requestDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        reviewService.updateBestReviewLeakedOrder(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewAdminController.class).slash("best").slash("leakedOrder");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ReviewAdminController.class).slash("best").withRel("query_best_reviews"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-best-reviews-leakedOrder").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }



    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
