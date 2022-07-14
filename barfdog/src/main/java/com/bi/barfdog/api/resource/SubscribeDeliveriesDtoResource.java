package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.deliveryDto.QuerySubscribeDeliveriesDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

public class SubscribeDeliveriesDtoResource extends EntityModel<QuerySubscribeDeliveriesDto> {
    public SubscribeDeliveriesDtoResource(QuerySubscribeDeliveriesDto dto, Link... links) {
        super(dto, Arrays.asList(links));
    }
}
