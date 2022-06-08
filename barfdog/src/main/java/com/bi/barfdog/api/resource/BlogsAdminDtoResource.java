package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.AdminApiController;
import com.bi.barfdog.api.BlogAdminController;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import com.bi.barfdog.api.memberDto.QueryMembersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BlogsAdminDtoResource extends EntityModel<QueryBlogsAdminDto> {
    public BlogsAdminDtoResource(QueryBlogsAdminDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(BlogAdminController.class).slash(dto.getId()).withRel("query_blog"));
        add(linkTo(BlogAdminController.class).slash(dto.getId()).withRel("update_blog"));
        add(linkTo(BlogAdminController.class).slash(dto.getId()).withRel("delete_blog"));

    }
}
