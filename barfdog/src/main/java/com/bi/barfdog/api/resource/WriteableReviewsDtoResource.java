package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.ReviewApiController;
import com.bi.barfdog.api.reviewDto.QueryWriteableReviewsDto;
import com.bi.barfdog.api.reviewDto.ReviewType;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class WriteableReviewsDtoResource extends EntityModel<QueryWriteableReviewsDto> {
    public WriteableReviewsDtoResource(QueryWriteableReviewsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(ReviewApiController.class).withRel("write_review"));
    }
}
