package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.EventApiController;
import com.bi.barfdog.api.eventDto.QueryEventsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventDtoResource extends EntityModel<QueryEventsDto> {
    public EventDtoResource(QueryEventsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(EventApiController.class).slash(dto.getId()).withRel("query_event"));
    }
}
