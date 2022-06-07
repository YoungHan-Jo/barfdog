package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.NoticeApiController;
import com.bi.barfdog.api.noticeDto.QueryNoticesDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class NoticeDtoResource extends EntityModel<QueryNoticesDto> {
    public NoticeDtoResource(QueryNoticesDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(NoticeApiController.class).slash(dto.getId()).withRel("query_notice"));
    }
}
