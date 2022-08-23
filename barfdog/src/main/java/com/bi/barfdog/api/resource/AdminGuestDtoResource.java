package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.MemberAdminController;
import com.bi.barfdog.api.OrderAdminController;
import com.bi.barfdog.api.guestDto.QueryAdminGuestDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminGuestDtoResource extends EntityModel<QueryAdminGuestDto> {
    public AdminGuestDtoResource(QueryAdminGuestDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        if (dto.getMemberId() != null) {
            add(linkTo(MemberAdminController.class).slash(dto.getMemberId()).withRel("query_member"));
        }
    }
}
