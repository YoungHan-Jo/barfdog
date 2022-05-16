package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.AdminApiController;
import com.bi.barfdog.api.memberDto.QueryMembersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class MembersDtoResource extends EntityModel<QueryMembersDto> {
    public MembersDtoResource(QueryMembersDto membersDto, Link... links) {
        super(membersDto, Arrays.asList(links));
        add(linkTo(AdminApiController.class).slash("members").slash(membersDto.getId()).withRel("query_member"));
    }
}
