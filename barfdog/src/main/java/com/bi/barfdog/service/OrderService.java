package com.bi.barfdog.service;

import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemOption;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardName;
import com.bi.barfdog.domain.reward.RewardStatus;
import com.bi.barfdog.domain.reward.RewardType;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.card.CardRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final MemberCouponRepository memberCouponRepository;
    private final DeliveryRepository deliveryRepository;
    private final SubscribeRepository subscribeRepository;
    private final OrderRepository orderRepository;
    private final RewardRepository rewardRepository;
    private final CardRepository cardRepository;
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final SettingRepository settingRepository;

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

        Card card = saveCard(member, requestDto);

        Subscribe subscribe = subscribeRepository.findById(subscribeId).get();
        subscribe.order(requestDto, card);
        member.order(requestDto);
        saveReward(member, requestDto);


        Long memberCouponId = requestDto.getMemberCouponId();
        MemberCoupon memberCoupon = null;
        if (memberCouponId != null) {
            memberCoupon = memberCouponRepository.findById(memberCouponId).get();
        }
        saveSubscribeOrder(member, requestDto, delivery, subscribe, memberCoupon);
        useCoupon(memberCoupon);

    }

    private Card saveCard(Member member, SubscribeOrderRequestDto requestDto) {
        Card card = Card.builder()
                .member(member)
                .customerUid(requestDto.getCustomerUid())
                .cardName(requestDto.getCardName())
                .cardNumber(requestDto.getCardNumber())
                .build();
        cardRepository.save(card);
        return card;
    }

    private void useCoupon(MemberCoupon memberCoupon) {
        if (memberCoupon != null) {
            memberCoupon.useCoupon();
        }
    }

    private void saveSubscribeOrder(Member member, SubscribeOrderRequestDto requestDto, Delivery delivery, Subscribe subscribe, MemberCoupon memberCoupon) {
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
                .memberCoupon(memberCoupon != null ? memberCoupon : null)
                .subscribeCount(subscribe.getSubscribeCount())
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
                .nextDeliveryDate(requestDto.getNextDeliveryDate())
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

    public OrderSheetGeneralResponseDto getOrderSheetGeneralDto(Member member, OrderSheetGeneralRequestDto requestDto) {

        List<OrderSheetGeneralResponseDto.OrderItemDto> orderItemDtoList = new ArrayList<>();

        List<OrderSheetGeneralRequestDto.OrderItemDto> requestOrderItemDtos = requestDto.getOrderItemDtoList();
        for (OrderSheetGeneralRequestDto.OrderItemDto requestOrderItemDto : requestOrderItemDtos) {
            OrderSheetGeneralRequestDto.ItemDto requestItemDto = requestOrderItemDto.getItemDto();
            Long itemId = requestItemDto.getItemId();
            int itemAmount = requestItemDto.getAmount();

            Item item = itemRepository.findById(itemId).get();

            List<OrderSheetGeneralResponseDto.OptionDto> optionDtoList = new ArrayList<>();

            List<OrderSheetGeneralRequestDto.ItemOptionDto> requestOptionDtos = requestOrderItemDto.getItemOptionDtoList();
            for (OrderSheetGeneralRequestDto.ItemOptionDto requestOptionDto : requestOptionDtos) {
                Long itemOptionId = requestOptionDto.getItemOptionId();
                ItemOption itemOption = itemOptionRepository.findById(itemOptionId).get();

                OrderSheetGeneralResponseDto.OptionDto optionDto = OrderSheetGeneralResponseDto.OptionDto.builder()
                        .optionId(itemOptionId)
                        .name(itemOption.getName())
                        .price(itemOption.getOptionPrice())
                        .amount(requestOptionDto.getAmount())
                        .build();
                optionDtoList.add(optionDto);
            }

            int originalOrderLinePrice = getOriginalOrderLinePrice(itemAmount, item, optionDtoList);
            int orderLinePrice = getOrderLinePrice(itemAmount, item, optionDtoList);

            OrderSheetGeneralResponseDto.OrderItemDto orderItemDto = OrderSheetGeneralResponseDto.OrderItemDto.builder()
                    .itemId(itemId)
                    .name(item.getName())
                    .optionDtoList(optionDtoList)
                    .amount(itemAmount)
                    .originalOrderLinePrice(originalOrderLinePrice)
                    .orderLinePrice(orderLinePrice)
                    .build();

            orderItemDtoList.add(orderItemDto);
        }

        List<Subscribe> subscribeList = subscribeRepository.findAllByMember(member);

        if (subscribeList.size() > 0) {
            subscribeList.get(0);
        }

        List<OrderSheetGeneralCouponDto> coupons = memberCouponRepository.findGeneralCouponsDto(member);

        int totalOrderPrice = getTotalOrderPrice(orderItemDtoList);

        Setting setting = settingRepository.findAll().get(0);

        List<Delivery> deliveryList = deliveryRepository.findPackageDeliveryDto(member);


        OrderSheetGeneralResponseDto responseDto = OrderSheetGeneralResponseDto.builder()
                .orderItemDtoList(orderItemDtoList)
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .deliveryId(deliveryList.size() > 0 ? deliveryList.get(0).getId() : null)
                .nextSubscribeDeliveryDate(deliveryList.size() > 0 ? deliveryList.get(0).getNextDeliveryDate() : null)
                .coupons(coupons)
                .orderPrice(totalOrderPrice)
                .reward(member.getReward())
                .deliveryPrice(setting.getDeliveryConstant().getPrice())
                .freeCondition(setting.getDeliveryConstant().getFreeCondition())
                .brochure(member.isBrochure())
                .build();

        return responseDto;
    }

    private int getTotalOrderPrice(List<OrderSheetGeneralResponseDto.OrderItemDto> orderItemDtoList) {
        int totalOrderPrice = 0;

        for (OrderSheetGeneralResponseDto.OrderItemDto orderItemDto : orderItemDtoList) {
            totalOrderPrice += orderItemDto.getOrderLinePrice();
        }
        return totalOrderPrice;
    }

    private int getOrderLinePrice(int itemAmount, Item item, List<OrderSheetGeneralResponseDto.OptionDto> optionDtoList) {
        int salePrice = item.getSalePrice();
        int orderLinePrice = salePrice * itemAmount;
        for (OrderSheetGeneralResponseDto.OptionDto optionDto : optionDtoList) {
            orderLinePrice += optionDto.getPrice() * optionDto.getAmount();
        }
        return orderLinePrice;
    }

    private int getOriginalOrderLinePrice(int itemAmount, Item item, List<OrderSheetGeneralResponseDto.OptionDto> optionDtoList) {
        int originalPrice = item.getOriginalPrice();
        int originalOrderLinePrice = originalPrice * itemAmount;
        for (OrderSheetGeneralResponseDto.OptionDto optionDto : optionDtoList) {
            originalOrderLinePrice += optionDto.getPrice() * optionDto.getAmount();
        }
        return originalOrderLinePrice;
    }
}
