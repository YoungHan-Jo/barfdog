package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.ReviewApiController;
import com.bi.barfdog.api.reviewDto.QueryReviewsDto;
import com.bi.barfdog.domain.review.ReviewStatus;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ReviewsDtoResource extends EntityModel<QueryReviewsDto> {
    public ReviewsDtoResource(QueryReviewsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        ReviewStatus status = dto.getStatus();
        if (status == ReviewStatus.REQUEST || status == ReviewStatus.RETURN) {
            add(linkTo(ReviewApiController.class).slash(dto.getId()).slash("images").withRel("query_review_images"));
            add(linkTo(ReviewApiController.class).slash(dto.getId()).withRel("query_review"));
            add(linkTo(ReviewApiController.class).slash(dto.getId()).withRel("delete_review"));
            add(linkTo(ReviewApiController.class).slash(dto.getId()).withRel("update_review"));
        }
    }
}
