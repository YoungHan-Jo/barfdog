package com.bi.barfdog.api;

import com.bi.barfdog.api.resource.ReviewsAdminDtoResource;
import com.bi.barfdog.api.reviewDto.AdminReviewsCond;
import com.bi.barfdog.api.reviewDto.ApprovalReviewsRequestDto;
import com.bi.barfdog.api.reviewDto.QueryAdminReviewsDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/reviews", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ReviewAdminController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping
    public ResponseEntity queryReviewsByCategory(Pageable pageable,
                                                 PagedResourcesAssembler<QueryAdminReviewsDto> assembler,
                                                 @ModelAttribute @Valid AdminReviewsCond cond,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return badRequest(bindingResult);

        Page<QueryAdminReviewsDto> page = reviewRepository.findAdminReviewsDto(pageable, cond);

        PagedModel<EntityModel<QueryAdminReviewsDto>> pagedModel = assembler.toModel(page, e -> new ReviewsAdminDtoResource(e));
        pagedModel.add(linkTo(ReviewAdminController.class).slash("approval").withRel("approval_reviews"));
        pagedModel.add(linkTo(ReviewAdminController.class).slash("best").withRel("create_best_reviews"));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-reviews").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @PutMapping("/approval")
    public ResponseEntity approvalReviews(@RequestBody @Valid ApprovalReviewsRequestDto requestDto,
                                          Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);

        reviewService.approvalReviews(requestDto);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryReview(@PathVariable Long id) {

        return null;
    }




    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
