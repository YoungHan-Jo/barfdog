package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.ItemApiController;
import com.bi.barfdog.api.itemDto.QueryItemsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ItemsDtoResource extends EntityModel<QueryItemsDto> {
    public ItemsDtoResource(QueryItemsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(ItemApiController.class).slash(dto.getId()).withRel("query_item"));
    }
}
