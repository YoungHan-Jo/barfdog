package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.CouponAdminController;
import com.bi.barfdog.api.couponDto.CouponListResponseDto;
import com.bi.barfdog.domain.coupon.CouponType;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AdminCouponsDtoResource extends EntityModel<CouponListResponseDto> {
    public AdminCouponsDtoResource(CouponListResponseDto dto, Link... links) {
        super(dto, Arrays.asList(links));
        if (dto == null) return;
        if (dto.getCouponType() == CouponType.GENERAL_PUBLISHED || dto.getCouponType() == CouponType.CODE_PUBLISHED) {
            if (dto.getExpiredDate() == null) return;
            if (dto.getExpiredDate().isBefore(LocalDateTime.now())) {
                add(linkTo(CouponAdminController.class).slash(dto.getId()).slash("inactive").withRel("inactive_coupon"));
            }
        }
    }
}
