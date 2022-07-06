package com.bi.barfdog.service;

import com.bi.barfdog.api.orderDto.OrderSheetSubsCouponDto;
import com.bi.barfdog.api.orderDto.OrderSheetSubscribeResponseDto;
import com.bi.barfdog.api.orderDto.SubscribeOrderRequestDto;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardName;
import com.bi.barfdog.domain.reward.RewardStatus;
import com.bi.barfdog.domain.reward.RewardType;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final MemberCouponRepository memberCouponRepository;
    private final DeliveryRepository deliveryRepository;
    private final SubscribeRepository subscribeRepository;
    private final OrderRepository orderRepository;
    private final RewardRepository rewardRepository;

    public OrderSheetSubscribeResponseDto getOrderSheetSubsDto(Member member, Long subscribeId) {

        OrderSheetSubscribeResponseDto responseDto = getOrderSheetSubscribeResponseDto(member, subscribeId);

        return responseDto;
    }

    private OrderSheetSubscribeResponseDto getOrderSheetSubscribeResponseDto(Member member, Long subscribeId) {
        List<OrderSheetSubsCouponDto> subscribeCouponDtos = memberCouponRepository.findSubscribeCouponDtos(member);

        OrderSheetSubscribeResponseDto.SubscribeDto subscribeDto = subscribeRepository.findOrderSheetSubscribeDtoById(subscribeId);

        List<String> recipeNameList = subscribeRepository.findRecipeNamesById(subscribeId);

        LocalDate nextDeliveryDate = getNextDeliveryDate();

        Grade grade = member.getGrade();
        int gradeDiscountPercent = getGradeDiscountPercent(grade);

        OrderSheetSubscribeResponseDto responseDto = OrderSheetSubscribeResponseDto.builder()
                .subscribeDto(subscribeDto)
                .recipeNameList(recipeNameList)
                .name(member.getName())
                .grade(grade)
                .gradeDiscountPercent(gradeDiscountPercent)
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .nextDeliveryDate(nextDeliveryDate)
                .coupons(subscribeCouponDtos)
                .reward(member.getReward())
                .brochure(member.isBrochure())
                .build();

        return responseDto;
    }

    private int getGradeDiscountPercent(Grade grade) {
        int gradeDiscountPercent = 0;
        if (grade == Grade.골드) {
            gradeDiscountPercent = 1;
        } else if (grade == Grade.플래티넘) {
            gradeDiscountPercent = 3;
        } else if (grade == Grade.다이아몬드) {
            gradeDiscountPercent = 5;
        } else if (grade == Grade.더바프) {
            gradeDiscountPercent = 7;
        }
        return gradeDiscountPercent;
    }


    @Transactional
    public void orderSubscribeOrder(Member member, Long subscribeId, SubscribeOrderRequestDto requestDto) {

        Delivery delivery = saveDelivery(requestDto);

        Subscribe subscribe = subscribeRepository.findById(subscribeId).get();
        subscribe.order(requestDto);
        member.order(requestDto);
        saveReward(member, requestDto);

        Optional<MemberCoupon> optionalMemberCoupon = memberCouponRepository.findById(requestDto.getMemberCouponId());
        saveSubscribeOrder(member, requestDto, delivery, subscribe, optionalMemberCoupon);
        useCoupon(optionalMemberCoupon);

    }

    private void useCoupon(Optional<MemberCoupon> optionalMemberCoupon) {
        if (optionalMemberCoupon.isPresent()) {
            MemberCoupon memberCoupon = optionalMemberCoupon.get();
            memberCoupon.useCoupon();
        }
    }

    private void saveSubscribeOrder(Member member, SubscribeOrderRequestDto requestDto, Delivery delivery, Subscribe subscribe, Optional<MemberCoupon> optionalMemberCoupon) {
        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .impUid(requestDto.getImpUid())
                .merchantUid(requestDto.getMerchantUid())
                .orderStatus(OrderStatus.PAYMENT_DONE)
                .member(member)
                .orderPrice(requestDto.getOrderPrice())
                .deliveryPrice(requestDto.getDeliveryPrice())
                .discountTotal(requestDto.getDiscountTotal())
                .discountReward(requestDto.getDiscountReward())
                .discountCoupon(requestDto.getDiscountCoupon())
                .paymentPrice(requestDto.getPaymentPrice())
                .paymentMethod(requestDto.getPaymentMethod())
                .isPackage(false)
                .isAgreePrivacy(requestDto.isAgreePrivacy())
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(optionalMemberCoupon.isPresent() ? optionalMemberCoupon.get() : null)
                .build();
        orderRepository.save(subscribeOrder);
    }

    private void saveReward(Member member, SubscribeOrderRequestDto requestDto) {
        Reward reward = Reward.builder()
                .member(member)
                .name(RewardName.USE_ORDER)
                .rewardType(RewardType.ORDER)
                .rewardStatus(RewardStatus.USED)
                .tradeReward(requestDto.getDiscountReward())
                .build();
        rewardRepository.save(reward);
    }

    private Delivery saveDelivery(SubscribeOrderRequestDto requestDto) {
        Recipient recipient = Recipient.builder()
                .name(requestDto.getDeliveryDto().getName())
                .phone(requestDto.getDeliveryDto().getPhone())
                .zipcode(requestDto.getDeliveryDto().getZipcode())
                .street(requestDto.getDeliveryDto().getStreet())
                .detailAddress(requestDto.getDeliveryDto().getDetailAddress())
                .build();

        Delivery delivery = Delivery.builder()
                .recipient(recipient)
                .status(DeliveryStatus.PAYMENT_DONE)
                .request(requestDto.getDeliveryDto().getRequest())
                .build();
        return deliveryRepository.save(delivery);
    }


    private LocalDate getNextDeliveryDate() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();
        int i = dayOfWeekNumber - 3;
        LocalDate nextDeliveryDate = null;
        if (dayOfWeekNumber <= 5) {
            nextDeliveryDate = today.minusDays(i+7);
        } else {
            nextDeliveryDate = today.minusDays(i+14);
        }
        return nextDeliveryDate;
    }


}
