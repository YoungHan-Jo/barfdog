package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.EventAdminController;
import com.bi.barfdog.api.eventDto.QueryEventsAdminDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventAdminDtoResource extends EntityModel<QueryEventsAdminDto> {
    public EventAdminDtoResource(QueryEventsAdminDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(EventAdminController.class).slash(dto.getEventsAdminDto().getId()).withRel("query_event"));
        add(linkTo(EventAdminController.class).withRel("update_event"));
        add(linkTo(EventAdminController.class).withRel("delete_event"));
    }
}
