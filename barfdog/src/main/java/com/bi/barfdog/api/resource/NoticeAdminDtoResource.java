package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.AdminApiController;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class NoticeAdminDtoResource extends EntityModel<QueryBlogsAdminDto> {
    public NoticeAdminDtoResource(QueryBlogsAdminDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(AdminApiController.class).slash("notices").slash(dto.getId()).withRel("query_notice"));
        add(linkTo(AdminApiController.class).slash("notices").slash(dto.getId()).withRel("update_notice"));
        add(linkTo(AdminApiController.class).slash("notices").slash(dto.getId()).withRel("delete_notice"));

    }
}
