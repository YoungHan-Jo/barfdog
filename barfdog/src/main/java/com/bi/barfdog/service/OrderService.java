package com.bi.barfdog.service;

import com.bi.barfdog.api.orderDto.OrderSheetSubscribeDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final CouponRepository couponRepository;

    public OrderSheetSubscribeDto getOrderSheetSubsDto(Member member) {




        OrderSheetSubscribeDto responseDto = OrderSheetSubscribeDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .coupons()
                .reward(member.getReward())
                .brochure(member.isBrochure())
                .build();

        return responseDto;
    }
}
