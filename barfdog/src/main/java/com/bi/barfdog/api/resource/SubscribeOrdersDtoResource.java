package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.OrderApiController;
import com.bi.barfdog.api.SubscribeApiController;
import com.bi.barfdog.api.orderDto.QuerySubscribeOrdersDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class SubscribeOrdersDtoResource extends EntityModel<QuerySubscribeOrdersDto> {
    public SubscribeOrdersDtoResource(QuerySubscribeOrdersDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        add(linkTo(OrderApiController.class).slash(dto.getSubscribeOrderDto().getOrderId()).slash("subscribe").withRel("query_subscribeOrder"));
        add(linkTo(SubscribeApiController.class).slash(dto.getSubscribeOrderDto().getSubscribeId()).withRel("query_subscribe"));
    }
}
