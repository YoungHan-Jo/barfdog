package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.AdminApiController;
import com.bi.barfdog.api.memberDto.MemberSubscribeAdminDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminMemberSubscribesDtoRessource extends EntityModel<MemberSubscribeAdminDto> {
    public AdminMemberSubscribesDtoRessource(MemberSubscribeAdminDto dto, Link... links) {
        super(dto, Arrays.asList(links));
    }
}
