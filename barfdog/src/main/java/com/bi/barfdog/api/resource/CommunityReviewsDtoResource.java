package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.reviewDto.QueryCommunityReviewsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

public class CommunityReviewsDtoResource extends EntityModel<QueryCommunityReviewsDto> {
    public CommunityReviewsDtoResource(QueryCommunityReviewsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
    }
}
