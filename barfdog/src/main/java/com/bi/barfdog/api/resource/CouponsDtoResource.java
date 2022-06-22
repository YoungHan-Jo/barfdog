package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.couponDto.QueryCouponsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

public class CouponsDtoResource extends EntityModel<QueryCouponsDto> {
    public CouponsDtoResource(QueryCouponsDto dto, Link... links) {
        super(dto, Arrays.asList(links));
    }
}
