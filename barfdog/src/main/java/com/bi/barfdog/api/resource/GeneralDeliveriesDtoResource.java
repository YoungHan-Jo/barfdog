package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.deliveryDto.QueryGeneralDeliveriesDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

public class GeneralDeliveriesDtoResource extends EntityModel<QueryGeneralDeliveriesDto> {
    public GeneralDeliveriesDtoResource(QueryGeneralDeliveriesDto dto, Link... links) {
        super(dto, Arrays.asList(links));
    }
}
