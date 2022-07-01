package com.bi.barfdog.service;

import com.bi.barfdog.api.orderDto.OrderSheetSubsCouponDto;
import com.bi.barfdog.api.orderDto.OrderSheetSubscribeResponseDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final MemberCouponRepository memberCouponRepository;
    private final SubscribeRepository subscribeRepository;

    public OrderSheetSubscribeResponseDto getOrderSheetSubsDto(Member member, Long subscribeId) {

        OrderSheetSubscribeResponseDto responseDto = getOrderSheetSubscribeResponseDto(member, subscribeId);

        return responseDto;
    }

    private OrderSheetSubscribeResponseDto getOrderSheetSubscribeResponseDto(Member member, Long subscribeId) {
        List<OrderSheetSubsCouponDto> subscribeCouponDtos = memberCouponRepository.findSubscribeCouponDtos(member);

        OrderSheetSubscribeResponseDto.SubscribeDto subscribeDto = subscribeRepository.findOrderSheetSubscribeDtoById(subscribeId);

        List<String> recipeNameList = subscribeRepository.findRecipeNamesById(subscribeId);

        OrderSheetSubscribeResponseDto responseDto = OrderSheetSubscribeResponseDto.builder()
                .subscribeDto(subscribeDto)
                .recipeNameList(recipeNameList)
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .coupons(subscribeCouponDtos)
                .reward(member.getReward())
                .brochure(member.isBrochure())
                .build();
        return responseDto;
    }
}
