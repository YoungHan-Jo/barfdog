package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.BlogApiController;
import com.bi.barfdog.api.blogDto.QueryBlogsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BlogsDtoResource extends EntityModel<QueryBlogsDto> {
    public BlogsDtoResource(QueryBlogsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(BlogApiController.class).slash(dto.getId()).withRel("query_blog"));
    }
}
