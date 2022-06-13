package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.UploadedImageAdminDto;
import com.bi.barfdog.api.resource.WriteableReviewsDtoResource;
import com.bi.barfdog.api.reviewDto.QueryReviewsDto;
import com.bi.barfdog.api.reviewDto.QueryWriteableReviewsDto;
import com.bi.barfdog.api.reviewDto.WriteReviewDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.service.ReviewService;
import com.bi.barfdog.validator.ReviewValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/reviews", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ReviewApiController {

    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final ReviewValidator reviewValidator;

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

        UploadedImageAdminDto responseDto = reviewService.uploadImage(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ReviewApiController.class).slash("upload");

        EntityModel<UploadedImageAdminDto> entityModel = EntityModel.of(responseDto,
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
        reviewRepository.findReviewsDtoByMember(pageable,member);

        return ResponseEntity.ok(null);
    }











    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
