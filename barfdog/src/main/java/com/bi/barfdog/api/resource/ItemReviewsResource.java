package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.itemDto.ItemReviewsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

public class ItemReviewsResource extends EntityModel<ItemReviewsDto> {
    public ItemReviewsResource(ItemReviewsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
    }
}
