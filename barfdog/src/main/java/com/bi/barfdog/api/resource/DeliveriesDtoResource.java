package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.deliveryDto.QueryDeliveriesDto;
import lombok.Data;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

public class DeliveriesDtoResource extends EntityModel<QueryDeliveriesDto> {
    public DeliveriesDtoResource(QueryDeliveriesDto dto, Link... links) {
        super(dto, Arrays.asList(links));
    }
}
