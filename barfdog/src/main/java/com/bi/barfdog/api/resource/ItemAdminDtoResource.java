package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.ItemAdminController;
import com.bi.barfdog.api.itemDto.QueryItemsAdminDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ItemAdminDtoResource extends EntityModel<QueryItemsAdminDto> {
    public ItemAdminDtoResource(QueryItemsAdminDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(ItemAdminController.class).slash(dto.getId()).withRel("query_item"));
        add(linkTo(ItemAdminController.class).slash(dto.getId()).withRel("update_item"));
        add(linkTo(ItemAdminController.class).slash(dto.getId()).withRel("delete_item"));

    }
}
