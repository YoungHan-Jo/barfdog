package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.SubscribeApiController;
import com.bi.barfdog.api.subscribeDto.QuerySubscribesDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class SubscribesDtoResource extends EntityModel<QuerySubscribesDto> {
    public SubscribesDtoResource(QuerySubscribesDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        Long id = dto.getSubscribeDto().getSubscribeId();
        add(linkTo(SubscribeApiController.class).slash(id).withRel("query_subscribe"));
        add(linkTo(SubscribeApiController.class).slash(id).slash("skip").withRel("skip_subscribe"));
    }
}
