package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.OrderApiController;
import com.bi.barfdog.api.orderDto.QueryGeneralOrdersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class GeneralOrdersDtoResource extends EntityModel<QueryGeneralOrdersDto> {
    public GeneralOrdersDtoResource(QueryGeneralOrdersDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(OrderApiController.class).slash(dto.getOrderDto().getId()).slash("general").withRel("query_order"));
    }
}
