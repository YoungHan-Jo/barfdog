package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.ReviewAdminController;
import com.bi.barfdog.api.reviewDto.QueryAdminReviewsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ReviewsAdminDtoResource extends EntityModel<QueryAdminReviewsDto> {
    public ReviewsAdminDtoResource(QueryAdminReviewsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(ReviewAdminController.class).slash(dto.getId()).withRel("query_review"));
        add(linkTo(ReviewAdminController.class).slash(dto.getId()).withRel("delete_review"));
    }
}
