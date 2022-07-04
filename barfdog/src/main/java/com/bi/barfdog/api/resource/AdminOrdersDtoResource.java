package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.OrderAdminController;
import com.bi.barfdog.api.orderDto.OrderAdminCond;
import com.bi.barfdog.api.orderDto.QueryAdminOrdersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminOrdersDtoResource extends EntityModel<QueryAdminOrdersDto> {
    public AdminOrdersDtoResource(QueryAdminOrdersDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        if (dto.getOrderType().equals("general")) {
            add(linkTo(OrderAdminController.class).slash(dto.getId()).slash("general").withRel("query_order"));
        } else {
            add(linkTo(OrderAdminController.class).slash(dto.getId()).slash("subscribe").withRel("query_order"));
        }
    }
}
