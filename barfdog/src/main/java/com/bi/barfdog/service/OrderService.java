package com.bi.barfdog.service;

import com.bi.barfdog.api.deliveryDto.OrderIdListDto;
import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.common.RandomString;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemOption;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.OrderExchange;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.orderItem.OrderReturn;
import com.bi.barfdog.domain.orderItem.SelectOption;
import com.bi.barfdog.domain.reward.*;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.iamport.Iamport_API;
import com.bi.barfdog.repository.basket.BasketOptionRepository;
import com.bi.barfdog.repository.basket.BasketRepository;
import com.bi.barfdog.repository.card.CardRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.orderItem.SelectOptionRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.request.UnscheduleData;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    private final CardRepository cardRepository;
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final SettingRepository settingRepository;
    private final OrderItemRepository orderItemRepository;
    private final SelectOptionRepository selectOptionRepository;
    private final DogRepository dogRepository;
    private final BasketOptionRepository basketOptionRepository;
    private final BasketRepository basketRepository;
    private final MemberRepository memberRepository;

    private IamportClient client = new IamportClient(Iamport_API.API_KEY, Iamport_API.API_SECRET);


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
    public OrderResponseDto orderGeneralOrder(Long id, GeneralOrderRequestDto requestDto) {
        Member member = memberRepository.findById(id).get();
        member.generalOrder(requestDto);
        saveUsedRewardHistory(member, requestDto.getDiscountReward());

        Delivery delivery = getDelivery(requestDto);

        GeneralOrder generalOrder = saveGeneralOrder(member, requestDto, delivery);

        List<GeneralOrderRequestDto.OrderItemDto> orderItemDtoList = requestDto.getOrderItemDtoList();
        for (GeneralOrderRequestDto.OrderItemDto orderItemDto : orderItemDtoList) {
            Item item = itemRepository.findById(orderItemDto.getItemId()).get();
            item.decreaseRemaining(orderItemDto.getAmount());


            Long memberCouponId = orderItemDto.getMemberCouponId();
            MemberCoupon memberCoupon = useCoupon(memberCouponId);

            int expectedSaveReward = getExpectedSaveReward(member, orderItemDto);

            OrderItem orderItem = OrderItem.builder()
                    .generalOrder(generalOrder)
                    .item(item)
                    .salePrice(item.getSalePrice())
                    .amount(orderItemDto.getAmount())
                    .memberCoupon(memberCoupon)
                    .discountAmount(orderItemDto.getDiscountAmount())
                    .finalPrice(orderItemDto.getFinalPrice())
                    .status(OrderStatus.BEFORE_PAYMENT)
                    .saveReward(expectedSaveReward)
                    .isSavedReward(false)
                    .writeableReview(false)
                    .build();
            orderItemRepository.save(orderItem);

            List<GeneralOrderRequestDto.SelectOptionDto> selectOptionDtoList = orderItemDto.getSelectOptionDtoList();
            for (GeneralOrderRequestDto.SelectOptionDto selectOptionDto : selectOptionDtoList) {
                saveSelectOption(orderItem, selectOptionDto);
            }
        }

        OrderResponseDto responseDto = OrderResponseDto.builder()
                .id(generalOrder.getId())
                .merchantUid(generalOrder.getMerchantUid())
                .status(generalOrder.getOrderStatus())
                .build();

        return responseDto;

    }

    private MemberCoupon useCoupon(Long memberCouponId) {
        MemberCoupon memberCoupon = null;
        if (memberCouponId != null && memberCouponId != 0L) {
            Optional<MemberCoupon> optionalMemberCoupon = memberCouponRepository.findById(memberCouponId);
            if (optionalMemberCoupon.isPresent()) {
                memberCoupon = optionalMemberCoupon.get();
                memberCoupon.useCoupon();
            }
        }
        return memberCoupon;
    }

    private int getExpectedSaveReward(Member member, GeneralOrderRequestDto.OrderItemDto orderItemDto) {
        double rewardPercent = 0.0;

        Grade grade = member.getGrade();
        switch (grade) {
            case 실버:
                rewardPercent = 0.5;
                break;
            case 골드:
                rewardPercent = 1.0;
                break;
            case 플래티넘:
                rewardPercent = 1.5;
                break;
            case 다이아몬드:
                rewardPercent = 2.0;
                break;
            case 더바프:
                rewardPercent = 3.0;
                break;
            default:
                rewardPercent = 0.0;
                break;
        }
        int savedReward = (int) Math.round(orderItemDto.getFinalPrice() * rewardPercent / 100.0);
        return savedReward;
    }

    private GeneralOrder saveGeneralOrder(Member member, GeneralOrderRequestDto requestDto, Delivery delivery) {
        RandomString rs = new RandomString(15);
        String randomString = rs.nextString();
        LocalDate today = LocalDate.now();
        String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        GeneralOrder generalOrder = GeneralOrder.builder()
                .merchantUid(dateString + "_" + randomString)
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .member(member)
                .orderPrice(requestDto.getOrderPrice())
                .deliveryPrice(requestDto.getDeliveryPrice())
                .discountTotal(requestDto.getDiscountTotal())
                .discountReward(requestDto.getDiscountReward())
                .discountCoupon(requestDto.getDiscountCoupon())
                .paymentPrice(requestDto.getPaymentPrice())
                .paymentMethod(requestDto.getPaymentMethod())
                .isPackage(requestDto.getDeliveryId() != null ? true : false)
                .isAgreePrivacy(requestDto.isAgreePrivacy())
                .isBrochure(!member.isBrochure() && requestDto.isBrochure() ? true : false)
                .delivery(delivery)
                .build();
        return orderRepository.save(generalOrder);
    }

    private void saveSelectOption(OrderItem orderItem, GeneralOrderRequestDto.SelectOptionDto selectOptionDto) {
        ItemOption itemOption = itemOptionRepository.findById(selectOptionDto.getItemOptionId()).get();
        itemOption.decreaseRemaining(selectOptionDto.getAmount());
        SelectOption selectOption = SelectOption.builder()
                .orderItem(orderItem)
                .itemOption(itemOption)
                .name(itemOption.getName())
                .price(itemOption.getOptionPrice())
                .amount(selectOptionDto.getAmount())
                .build();
        selectOptionRepository.save(selectOption);
    }



    private Delivery getDelivery(GeneralOrderRequestDto requestDto) {
        Delivery delivery = null;

        if (requestDto.getDeliveryId() != null) {
            delivery = deliveryRepository.findById(requestDto.getDeliveryId()).get();
        } else {
            delivery = Delivery.builder()
                    .recipient(Recipient.builder()
                            .name(requestDto.getDeliveryDto().getName())
                            .phone(requestDto.getDeliveryDto().getPhone())
                            .zipcode(requestDto.getDeliveryDto().getZipcode())
                            .street(requestDto.getDeliveryDto().getStreet())
                            .detailAddress(requestDto.getDeliveryDto().getDetailAddress())
                            .build())
                    .status(DeliveryStatus.BEFORE_PAYMENT)
                    .request(requestDto.getDeliveryDto().getRequest())
                    .build();
            deliveryRepository.save(delivery);
        }
        return delivery;
    }


    @Transactional
    public OrderResponseDto orderSubscribeOrder(Long memberId, Long subscribeId, SubscribeOrderRequestDto requestDto) {
        Member member = memberRepository.findById(memberId).get();
        member.subscribeOrder(requestDto);

        saveUsedRewardHistory(member, requestDto.getDiscountReward());

        Long memberCouponId = requestDto.getMemberCouponId();
        MemberCoupon memberCoupon = useCoupon(memberCouponId);

        Delivery delivery = saveDelivery(requestDto);

        Subscribe subscribe = subscribeRepository.findById(subscribeId).get();
        SubscribeOrder subscribeOrder = saveSubscribeOrder(member, requestDto, delivery, subscribe, memberCoupon);

        OrderResponseDto responseDto = OrderResponseDto.builder()
                .id(subscribeOrder.getId())
                .merchantUid(subscribeOrder.getMerchantUid())
                .status(subscribeOrder.getOrderStatus())
                .build();

        return responseDto;
    }



    private SubscribeOrder saveSubscribeOrder(Member member, SubscribeOrderRequestDto requestDto, Delivery delivery, Subscribe subscribe, MemberCoupon memberCoupon) {
        String merchantUid = generateMerchantUid();
        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .merchantUid(merchantUid)
                .orderStatus(OrderStatus.BEFORE_PAYMENT)
                .member(member)
                .orderPrice(requestDto.getOrderPrice())
                .deliveryPrice(requestDto.getDeliveryPrice())
                .discountTotal(requestDto.getDiscountTotal())
                .discountReward(requestDto.getDiscountReward())
                .discountCoupon(requestDto.getDiscountCoupon())
                .discountGrade(requestDto.getDiscountGrade())
                .paymentPrice(requestDto.getPaymentPrice())
                .paymentMethod(requestDto.getPaymentMethod())
                .isPackage(false)
                .isAgreePrivacy(requestDto.isAgreePrivacy())
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(memberCoupon != null ? memberCoupon : null)
                .subscribeCount(subscribe.getSubscribeCount() + 1)
                .isBrochure(requestDto.isBrochure())
                .build();
        return orderRepository.save(subscribeOrder);
    }

    private String generateMerchantUid() {
        RandomString rs = new RandomString(15);
        String randomString = rs.nextString();
        LocalDate orderDate = LocalDate.now();
        String dateString = orderDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String merchantUid = dateString + "_" + randomString;
        return merchantUid;
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
                .status(DeliveryStatus.BEFORE_PAYMENT)
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


    @Transactional
    public void successGeneralOrder(Long orderId, Long memberId, SuccessGeneralRequestDto requestDto) {
        GeneralOrder order = (GeneralOrder) orderRepository.findById(orderId).get();
        order.successGeneral(requestDto);
        Member member = memberRepository.findById(memberId).get();
        member.generalOrderSuccess(order);

        isFirstPayment(member);

        if (!order.isPackage()) {
            order.getDelivery().paymentDone();
        }

        List<Item> selectItemList = new ArrayList<>();

        List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(order);
        for (OrderItem orderItem : orderItems) {
            Item item = orderItem.getItem();
            selectItemList.add(item);
            orderItem.successPayment();
        }

        List<Basket> deleteBasketList = basketRepository.findByMemberAndItems(member, selectItemList);
        basketOptionRepository.deleteByBasketList(deleteBasketList);
        basketRepository.deleteAll(deleteBasketList);

        try {
            String dogName = getRepresentativeDogName(member);
            DirectSendUtils.sendGeneralOrderSuccessAlim(order, dogName, orderItems);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("알림톡 전송 중 에러");
        }

    }

    private void isFirstPayment(Member member) {
        if (!member.isPaid()) {
            member.firstPayment();
            String recommendCode = member.getRecommendCode();
            if (recommendCode != null) {
                Optional<Member> optionalMember = memberRepository.findByMyRecommendationCode(recommendCode);
                if (optionalMember.isPresent()) {
                    Member recommendedMember = optionalMember.get();
                    recommendedMember.chargeReward(RewardPoint.RECOMMEND);
                    saveInviteRewardHistory(member, recommendedMember);
                }
            }

            if (!member.getFirstReward().isReceiveAgree()) {
                if (member.getAgreement().isReceiveEmail() && member.getAgreement().isReceiveSms()) {
                    member.saveReceiveAgreeReward();

                    Reward reward = Reward.builder()
                            .member(member)
                            .name(RewardName.RECEIVE_AGREEMENT)
                            .rewardType(RewardType.RECEIVE)
                            .rewardStatus(RewardStatus.SAVED)
                            .tradeReward(RewardPoint.RECEIVE_AGREEMENT)
                            .build();
                    rewardRepository.save(reward);
                }
            }
        }
    }

    private void saveInviteRewardHistory(Member member, Member recommendedMember) {
        Reward reward = Reward.builder()
                .member(recommendedMember)
                .name(RewardName.INVITE + "(" + member.getName() + ")")
                .rewardType(RewardType.INVITE)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(RewardPoint.RECOMMEND)
                .build();
        rewardRepository.save(reward);
    }

    private String getRepresentativeDogName(Member member) {
        List<Dog> dogList = dogRepository.findRepresentativeDogByMember(member);
        String dogName = "강아지";
        if (dogList.size() > 0) {
            dogName = dogList.get(0).getName();
        }
        return dogName;
    }


    private void saveUsedRewardHistory(Member member, int tradeReward) {
        Reward reward = Reward.builder()
                .member(member)
                .name(RewardName.USE_ORDER)
                .rewardType(RewardType.ORDER)
                .rewardStatus(RewardStatus.USED)
                .tradeReward(tradeReward)
                .build();
        rewardRepository.save(reward);
    }

    @Transactional
    public void failGeneralOrder(Long orderId, Long memberId) {
        Member member = memberRepository.findById(memberId).get();
        GeneralOrder order = (GeneralOrder) orderRepository.findById(orderId).get();

        member.generalOrderFail(order.getDiscountReward());
        saveFailedOrderRewardHistory(member, order);

        order.failPayment();

        List<OrderItem> orderItemList = orderItemRepository.findAllByGeneralOrder(order);
        for (OrderItem orderItem : orderItemList) {
            orderItem.failPayment();

            List<SelectOption> selectOptionList = selectOptionRepository.findAllByOrderItem(orderItem);
            for (SelectOption selectOption : selectOptionList) {
                selectOption.failPayment();
            }
        }



    }

    private void saveFailedOrderRewardHistory(Member member, Order order) {
        Reward reward = Reward.builder()
                .member(member)
                .name(RewardName.FAILED_ORDER)
                .rewardType(RewardType.ORDER)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(order.getDiscountReward())
                .build();
        rewardRepository.save(reward);
    }





    @Transactional
    public void successSubscribeOrder(Long id, Long memberId, SuccessSubscribeRequestDto requestDto) {
        SubscribeOrder order = (SubscribeOrder) orderRepository.findById(id).get();
        order.successSubscribe(requestDto);

        Member member = memberRepository.findById(memberId).get();
        member.subscribeOrderSuccess(order);

        isFirstPayment(member);

        Subscribe subscribe = order.getSubscribe();
        Delivery delivery = order.getDelivery();
        delivery.paymentDone();

        String customerUid = requestDto.getCustomerUid();
        Card card = null;
        try {
            IamportResponse<BillingCustomer> billingCustomer = client.getBillingCustomer(customerUid);
            BillingCustomer response = billingCustomer.getResponse();
            card = saveCard(member, response.getCustomerUid(), response.getCardName(), response.getCardNumber());
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        subscribe.successPayment(card, order);

        SubscribeOrder reservedOrder = saveReservedOrderAndDelivery(member, order, subscribe);

        ScheduleData scheduleData = new ScheduleData(customerUid);
        Date nextPaymentDate = java.sql.Timestamp.valueOf(subscribe.getNextPaymentDate());
        int reservedPaymentPrice = reservedOrder.getOrderPrice() - reservedOrder.getDiscountTotal();
        scheduleData.addSchedule(new ScheduleEntry(reservedOrder.getMerchantUid(), nextPaymentDate, BigDecimal.valueOf(reservedPaymentPrice)));

        try {
            client.subscribeSchedule(scheduleData);
            try {
                DirectSendUtils.sendSubscribeOrderSuccessAlim(order);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("알림톡 전송 중 에러");
            }

        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private LocalDate calculateFirstDeliveryDate() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();
        int i = dayOfWeekNumber - 3;
        LocalDate nextDeliveryDate = null;
        if (dayOfWeekNumber <= 5) {
            nextDeliveryDate = today.plusDays(i+7);
        } else {
            nextDeliveryDate = today.plusDays(i+14);
        }
        return nextDeliveryDate;
    }

    private SubscribeOrder saveReservedOrderAndDelivery(Member member, SubscribeOrder order, Subscribe subscribe) {
        Delivery nextDelivery = saveNextDelivery(order, subscribe);

        String merchantUid = generateMerchantUid();

        SubscribeOrder nextSubscribeOrder = saveNextSubscribeOrder(merchantUid, member, subscribe, order, nextDelivery);
        subscribe.setNextOrderMerchantUid(merchantUid);

        return nextSubscribeOrder;
    }

    private SubscribeOrder saveNextSubscribeOrder(String merchantUid, Member member, Subscribe subscribe, SubscribeOrder order, Delivery nextDelivery) {
        int nextPaymentPrice = subscribe.getNextPaymentPrice();
        int discountGrade = order.getDiscountGrade();
        SubscribeOrder nextSubscribeOrder = SubscribeOrder.builder()
                .merchantUid(merchantUid)
                .orderStatus(OrderStatus.RESERVED_PAYMENT)
                .member(member)
                .orderPrice(nextPaymentPrice)
                .discountTotal(discountGrade)
                .discountGrade(discountGrade)
                .paymentPrice(nextPaymentPrice - discountGrade)
                .paymentMethod(order.getPaymentMethod())
                .isAgreePrivacy(true)
                .delivery(nextDelivery)
                .subscribe(subscribe)
                .subscribeCount(subscribe.getSubscribeCount() + 1)
                .build();
        return orderRepository.save(nextSubscribeOrder);
    }

    private int calculateNextPaymentPriceAfterGradeDiscount(Member member, Subscribe subscribe) {
        int gradeDiscountPercent = getGradeDiscountPercent(member.getGrade());

        int nextPaymentPrice = subscribe.getNextPaymentPrice();
        if (gradeDiscountPercent > 0) {
            nextPaymentPrice = (int) Math.round(nextPaymentPrice / 100.0 * (100 - gradeDiscountPercent));
        }
        return nextPaymentPrice;
    }

    private Delivery saveNextDelivery(SubscribeOrder order, Subscribe subscribe) {
        Delivery delivery = order.getDelivery();

        SubscribePlan plan = subscribe.getPlan();
        LocalDate nextDeliveryDate = calculateNextDeliveryDate(delivery, plan);

        Delivery nextDelivery = Delivery.builder()
                .recipient(delivery.getRecipient())
                .status(DeliveryStatus.BEFORE_PAYMENT)
                .request(delivery.getRequest())
                .nextDeliveryDate(nextDeliveryDate)
                .build();
        return deliveryRepository.save(nextDelivery);
    }

    private void saveRewardHistory(Member member, Order order) {
        Reward reward = Reward.builder()
                .member(member)
                .name(RewardName.USE_ORDER)
                .rewardType(RewardType.ORDER)
                .rewardStatus(RewardStatus.USED)
                .tradeReward(order.getDiscountReward())
                .build();
        rewardRepository.save(reward);
    }

    private Card saveCard(Member member, String customerUid, String cardName, String cardNumber) {
        Card card = Card.builder()
                .member(member)
                .customerUid(customerUid)
                .cardName(cardName)
                .cardNumber(cardNumber)
                .build();
        cardRepository.save(card);
        return card;
    }

    @Transactional
    public void failSubscribeOrder(Long id, Long memberId) {
        SubscribeOrder order = (SubscribeOrder) orderRepository.findById(id).get();
        Member member = memberRepository.findById(memberId).get();

        member.subscribeOrderFail(order);
        saveFailedOrderRewardHistory(member, order);

        order.getMemberCoupon();

        MemberCoupon memberCoupon = order.getMemberCoupon();
        if (memberCoupon != null) {
            memberCoupon.revivalCoupon();
        }

        order.failPayment();
    }

    @Transactional
    public void cancelRequestSubscribe(Long id) {
        SubscribeOrder order = (SubscribeOrder) orderRepository.findById(id).get();
        OrderStatus status = order.getOrderStatus();
        if (status == OrderStatus.PAYMENT_DONE) {
            // 즉시 카드 취소
            cancelSubscribeOrderNow(order, null, null,OrderStatus.CANCEL_DONE_BUYER);

        } else if (status == OrderStatus.PRODUCING || status == OrderStatus.DELIVERY_READY) {
            // 취소 요청
            order.cancelRequest();
            order.setCancelRequestDate();
        }
    }



    //    취소(요청) 주문 단위
    @Transactional
    public void cancelRequestGeneral(Long id) {
        GeneralOrder order = (GeneralOrder) orderRepository.findById(id).get();

        OrderStatus orderStatus = order.getOrderStatus();

        if (orderStatus == OrderStatus.PAYMENT_DONE) {
            // 즉시 카드 취소
            cancelGeneralOrderNow(OrderStatus.CANCEL_DONE_BUYER, null, null, order);
        } else if (orderStatus == OrderStatus.DELIVERY_READY || orderStatus == OrderStatus.PRODUCING) {
            // 취소 요청
            List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(order);
            order.cancelRequest();
            order.setCancelRequestDate();
            for (OrderItem orderItem : orderItems) {
                orderItem.cancelRequestDate();
            }
        }
    }

    private void cancelGeneralOrderNow(OrderStatus newOrderStatus, String reason, String detailReason, GeneralOrder order) {
        Member member = order.getMember();
        String impUid = order.getImpUid();
        CancelData cancelData = new CancelData(impUid, true);
        try {
            client.cancelPaymentByImpUid(cancelData);

            member.cancelGeneralPayment(order);
            saveCancelOrderRewardHistory(order, member);

            order.cancelOrderAndDelivery(newOrderStatus);
            order.setCancelOrderInfo(reason, detailReason);

            List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(order);
            for (OrderItem orderItem : orderItems) {
                orderItem.cancelOrderConfirmAndRevivalCoupon(newOrderStatus, null, null);
                Item item = orderItem.getItem();
                item.increaseRemaining(orderItem.getAmount());

                List<SelectOption> selectOptionList = selectOptionRepository.findAllByOrderItem(orderItem);
                for (SelectOption selectOption : selectOptionList) {
                    ItemOption itemOption = selectOption.getItemOption();
                    if (itemOption == null) continue;

                    itemOption.increaseRemaining(selectOption.getAmount());
                }
            }

            String dogName = getRepresentativeDogName(member);
            DirectSendUtils.sendGeneralOrderCancelAlim(order, dogName, orderItems);

        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCancelOrderRewardHistory(Order order, Member member) {
        int discountReward = order.getDiscountReward();
        if (discountReward <= 0) return;

        Reward reward = Reward.builder()
                .member(member)
                .name(RewardName.CANCEL_ORDER)
                .rewardType(RewardType.ORDER)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(discountReward)
                .build();
        rewardRepository.save(reward);
    }

    @Transactional
    public void confirmOrders(Long memberId, ConfirmOrderItemsDto requestDto) {
        Member member = memberRepository.findById(memberId).get();
        List<Long> orderItemIdList = requestDto.getOrderItemIdList();
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            confirmOrderItemAndOrder(member, orderItem);
        }
    }

    private void confirmOrderItemAndOrder(Member member, OrderItem orderItem) {
        if(alreadyConfirm(orderItem)) return;
        orderItem.confirm();
        member.chargeReward(orderItem.getSaveReward());
        saveExpectedRewardHistory(member, orderItem);

        generalOrderConfirm(orderItem);
    }

    private void generalOrderConfirm(OrderItem orderItem) {
        GeneralOrder order = orderItem.getGeneralOrder();
        List<OrderItem> innerOrderItems = orderItemRepository.findAllByGeneralOrder(order);
        int orderItemSize = innerOrderItems.size();
        int count = 0;
        for (OrderItem innerOrderItem : innerOrderItems) {
            if (innerOrderItem.getStatus() == OrderStatus.CONFIRM) count++;
        }
        if (orderItemSize == count) {
            order.generalConfirm();
        }
    }

    private boolean alreadyConfirm(OrderItem orderItem) {
        return orderItem.getStatus() == OrderStatus.CONFIRM;
    }

    private void saveExpectedRewardHistory(Member member, OrderItem orderItem) {
        Reward reward = Reward.builder()
                .member(member)
                .name(RewardName.CONFIRM_ORDER)
                .rewardType(RewardType.ORDER)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(orderItem.getSaveReward())
                .build();
        rewardRepository.save(reward);
    }

    @Transactional
    public void requestReturn(RequestReturnExchangeOrdersDto requestDto) {
        OrderReturn orderReturn = OrderReturn.builder()
                .returnReason(requestDto.getReason())
                .returnDetailReason(requestDto.getDetailReason())
                .returnRequestDate(LocalDateTime.now())
                .build();
        List<Long> orderItemIdList = requestDto.getOrderItemIdList();
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            orderItem.returnRequest(orderReturn);
        }
    }

    @Transactional
    public void requestExchange(RequestReturnExchangeOrdersDto requestDto) {
        OrderExchange orderExchange = OrderExchange.builder()
                .exchangeReason(requestDto.getReason())
                .exchangeDetailReason(requestDto.getDetailReason())
                .exchangeRequestDate(LocalDateTime.now())
                .build();
        List<Long> orderItemIdList = requestDto.getOrderItemIdList();
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            orderItem.exchangeRequest(orderExchange);
        }
    }

    @Transactional
    public void checkGeneralOrder(OrderConfirmGeneralDto requestDto) {
        List<Long> orderItemIdList = requestDto.getOrderItemIdList();
        for (Long orderItemId : orderItemIdList) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
            orderItem.checkGeneralOrder();

            GeneralOrder order = orderItem.getGeneralOrder();
            Member member = order.getMember();
            String dogName = getRepresentativeDogName(member);

            try {
                DirectSendUtils.sendGeneralOrderDeliveryReadyAlim(order, dogName, orderItem.getItem().getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void checkSubscribeOrder(OrderConfirmSubscribeDto requestDto) {
        List<Long> orderIdList = requestDto.getOrderIdList();
        for (Long orderId : orderIdList) {
            Order order = orderRepository.findById(orderId).get();
            order.checkSubscribeOrder();

            try {
                DirectSendUtils.sendSubscribeOrderProducingAlim(order);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Transactional
    public void cancelRequestConfirmGeneral(CancelConfirmGeneralDto requestDto) {
        List<Long> orderIdList = requestDto.getOrderIdList();
        for (Long orderId : orderIdList) {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (!optionalOrder.isPresent()) continue;
            GeneralOrder order = (GeneralOrder) optionalOrder.get();
            cancelGeneralOrderNow(OrderStatus.CANCEL_DONE_BUYER, null, null, order);
        }
    }

    private void revivalCoupon(OrderItem orderItem) {
        MemberCoupon memberCoupon = orderItem.getMemberCoupon();
        if (memberCoupon != null) {
            memberCoupon.revivalCoupon();
        }
    }

    private int getCancelReward(GeneralOrder order, int finalPrice) {
        int allRewards = order.getDiscountReward();
        int paymentPrice = order.getPaymentPrice();
        int allAmount = paymentPrice + allRewards;
        int percent = (int) Math.round(((double)finalPrice / allAmount) * 100);
        int cancelReward = (int) (allRewards / 100) * percent;
        return cancelReward;
    }



    private void revivalCancelOrderReward(OrderItem orderItem, GeneralOrder order, String name) {
        int cancelReward = orderItem.getCancelReward();

        if (cancelReward > 0) {
            Member member = order.getMember();
            member.chargeReward(cancelReward);
            saveRewardHistory(name, cancelReward, member);
        }
    }
    private void revivalCancelOrderReward(Order order, String rewardName) {
        int cancelReward = order.getDiscountReward();

        if (cancelReward > 0) {
            Member member = order.getMember();
            member.chargeReward(cancelReward);
            saveRewardHistory(rewardName, cancelReward, member);
        }
    }

    private void saveRewardHistory(String name, int cancelReward, Member member) {
        Reward reward = Reward.builder()
                .member(member)
                .name(name)
                .rewardType(RewardType.ORDER)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(cancelReward)
                .build();
        rewardRepository.save(reward);
    }

    @Transactional
    public void cancelRequestConfirmSubscribe(CancelConfirmSubscribeDto requestDto) {
        List<Long> orderIdList = requestDto.getOrderIdList();
        for (Long orderId : orderIdList) {

            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (!optionalOrder.isPresent()) continue;

            Order findOrder = optionalOrder.get();
            if (findOrder instanceof GeneralOrder) continue;

            SubscribeOrder order = (SubscribeOrder) findOrder;
            // 즉시 카드 취소
            cancelSubscribeOrderNow(order,null,null, OrderStatus.CANCEL_DONE_BUYER);
        }
    }

    private void cancelSubscribeOrderNow(SubscribeOrder order,String reason, String detailReason, OrderStatus orderStatus) {
        Member member = order.getMember();
        Subscribe subscribe = order.getSubscribe();

        String merchantUid = order.getMerchantUid();
        CancelData cancelData = new CancelData(merchantUid, false);


        try {
            client.cancelPaymentByImpUid(cancelData);

            member.cancelSubscribePayment(order);
            saveCancelOrderRewardHistory(order, member);

            order.cancelOrderAndDelivery(orderStatus);
            order.setCancelOrderInfo(reason,detailReason);

            revivalCoupon(order);

            // 예약 취소
            String nextOrderMerchantUid = subscribe.getNextOrderMerchantUid();
            Optional<SubscribeOrder> optionalSubscribeOrder = orderRepository.findByMerchantUid(nextOrderMerchantUid);
            Card card = subscribe.getCard();
            if (optionalSubscribeOrder.isPresent()) {
                SubscribeOrder reservedOrder = optionalSubscribeOrder.get();
                String customerUid = card.getCustomerUid();
                UnscheduleData unscheduleData = new UnscheduleData(customerUid);
                unscheduleData.addMerchantUid(reservedOrder.getMerchantUid());
                client.unsubscribeSchedule(unscheduleData);

                revivalCoupon(reservedOrder);

                Delivery reservedDelivery = reservedOrder.getDelivery();
                deliveryRepository.delete(reservedDelivery);

                orderRepository.delete(reservedOrder);
            }

            subscribe.cancelPayment();
            cardRepository.delete(card);

            if (isSubscriber(member)) {
                member.stopSubscriber();
            }

            DirectSendUtils.sendSubscribeOrderCancelAlim(order);

        }catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isSubscriber(Member member) {
        Long count = subscribeRepository.findSubscribingCountByMember(member);
        return !count.equals(0L);
    }

    private void revivalCoupon(SubscribeOrder order) {
        MemberCoupon memberCoupon = order.getMemberCoupon();
        if (memberCoupon != null) {
            memberCoupon.revivalCoupon();
        }
    }

    private void revivalCancelOrderReward(SubscribeOrder order, Member member) {
        int discountReward = order.getDiscountReward();
        if (discountReward > 0) {
            member.chargeReward(discountReward);
            saveRewardHistory(RewardName.CANCEL_ORDER, discountReward, member);
        }
    }

    @Transactional
    public void sellingCancelGeneral(OrderCancelGeneralDto requestDto) {
        List<Long> orderItemIdList = requestDto.getOrderItemIdList();
        String reason = requestDto.getReason();
        String detailReason = requestDto.getDetailReason();

        for (Long orderItemId : orderItemIdList) {
            Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(orderItemId);
            if (!optionalOrderItem.isPresent()) continue;

            OrderItem orderItem = optionalOrderItem.get();
            GeneralOrder order = orderItem.getGeneralOrder();

            if (orderItem.getStatus() == OrderStatus.SELLING_CANCEL) continue;
            cancelGeneralOrderNow(OrderStatus.SELLING_CANCEL, reason, detailReason, order);
        }

        setOrderItemSellingCancel(orderItemIdList, reason, detailReason);
    }

    private void setOrderItemSellingCancel(List<Long> orderItemIdList, String reason, String detailReason) {
        for (Long orderItemId : orderItemIdList) {
            Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(orderItemId);
            if (!optionalOrderItem.isPresent()) continue;

            OrderItem orderItem = optionalOrderItem.get();
            OrderStatus orderStatus = orderItem.getGeneralOrder().getOrderStatus();
            if (orderStatus == OrderStatus.SELLING_CANCEL) {
                orderItem.sellingCancel(OrderStatus.SELLING_CANCEL, reason, detailReason);
            }
        }
    }

    @Transactional
    public void sellingCancelSubscribe(OrderCancelSubscribeDto requestDto) {
        String reason = requestDto.getReason();
        String detailReason = requestDto.getDetailReason();
        List<Long> orderIdList = requestDto.getOrderIdList();

        for (Long orderId : orderIdList) {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (!optionalOrder.isPresent()) continue;

            Order order = optionalOrder.get();
            if (order instanceof GeneralOrder) continue;

            SubscribeOrder subscribeOrder = (SubscribeOrder) order;

            cancelSubscribeOrderNow(subscribeOrder, reason,detailReason,OrderStatus.SELLING_CANCEL);
        }
    }



    @Transactional
    public void denyReturn(OrderItemIdListDto requestDto) {
        denyRequest(requestDto);
    }

    private void denyRequest(OrderItemIdListDto requestDto) {
        List<Long> orderItemIdList = requestDto.getOrderItemIdList();
        for (Long orderItemId : orderItemIdList) {
            Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(orderItemId);
            if (!optionalOrderItem.isPresent()) continue;
            OrderItem orderItem = optionalOrderItem.get();
            orderItem.denyRequest();
        }
    }

    @Transactional
    public void denyExchange(OrderItemIdListDto requestDto) {
        denyRequest(requestDto);
    }


    @Transactional
    public void confirmExchange(OrderItemIdListDto requestDto, OrderStatus status) {
        List<Long> orderItemIdList = requestDto.getOrderItemIdList();
        for (Long orderItemId : orderItemIdList) {
            Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(orderItemId);
            if (!optionalOrderItem.isPresent()) continue;
            OrderItem orderItem = optionalOrderItem.get();
            orderItem.exchangeConfirm(status);
            GeneralOrder order = orderItem.getGeneralOrder();
            Member member = order.getMember();

            String dogName = getRepresentativeDogName(member);

            try {
                DirectSendUtils.sendExchangeAlim(member, dogName, orderItem.getItem().getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Transactional
    public void confirmReturn(OrderItemIdListDto requestDto, OrderStatus orderStatus) {
        List<Long> orderItemIdList = requestDto.getOrderItemIdList();

        for (Long orderItemId : orderItemIdList) {
            Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(orderItemId);
            if (!optionalOrderItem.isPresent()) continue;
            OrderItem orderItem = optionalOrderItem.get();
            orderItem.returnConfirm(orderStatus);

            GeneralOrder order = orderItem.getGeneralOrder();
            Member member = order.getMember();

            String dogName = getRepresentativeDogName(member);

            try {
                DirectSendUtils.sendReturnAlim(member, dogName, orderItem.getItem().getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Transactional
    public void successPaymentSchedule(String merchantUid, String impUid) {
        SubscribeOrder order = orderRepository.findByMerchantUid(merchantUid).get();
        // TODO: 2022-07-25 Beforesubscribe작업 ?
        Member member = order.getMember();
        Subscribe subscribe = order.getSubscribe();

        order.successPaymentSchedule(impUid);

        String newMerchantUid = generateMerchantUid();
        subscribe.successPaymentSchedule(order, newMerchantUid);

        SubscribeOrder nextOrder = saveNextOrderAndNextDelivery(order, member, subscribe, newMerchantUid);

        scheduleNextOrder(member, subscribe, nextOrder);
    }

    private void scheduleNextOrder(Member member, Subscribe subscribe, SubscribeOrder nextOrder) {
        Card card = subscribe.getCard();
        String customerUid = card.getCustomerUid();
        ScheduleData scheduleData = new ScheduleData(customerUid);
        Date nextPaymentDate = java.sql.Timestamp.valueOf(subscribe.getNextPaymentDate());
        int nextPaymentPrice = calculateNextPaymentPriceAfterGradeDiscount(member, subscribe);
        scheduleData.addSchedule(new ScheduleEntry(nextOrder.getMerchantUid(),nextPaymentDate, BigDecimal.valueOf(nextPaymentPrice)));

        try {
            client.subscribeSchedule(scheduleData);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SubscribeOrder saveNextOrderAndNextDelivery(SubscribeOrder order, Member member, Subscribe subscribe,String newMerchantUid) {

        Delivery newDelivery = saveNewDelivery(order, subscribe);

        SubscribeOrder nextOrder = saveNextSubscribeOrder(newMerchantUid, member, subscribe, order, newDelivery);
        return nextOrder;
    }

    private Delivery saveNewDelivery(SubscribeOrder order, Subscribe subscribe) {
        Delivery delivery = order.getDelivery();
        SubscribePlan plan = subscribe.getPlan();
        LocalDate nextDeliveryDate = calculateNextDeliveryDate(delivery, plan);
        Delivery newDelivery = Delivery.builder()
                .recipient(delivery.getRecipient())
                .status(DeliveryStatus.BEFORE_PAYMENT)
                .request(delivery.getRequest())
                .nextDeliveryDate(nextDeliveryDate)
                .build();
        deliveryRepository.save(newDelivery);
        return newDelivery;
    }

    private LocalDate calculateNextDeliveryDate(Delivery delivery, SubscribePlan plan) {
        return delivery.getNextDeliveryDate().plusDays(plan == SubscribePlan.FULL ? 14 : 28);
    }

    @Transactional
    public void failPaymentSchedule(String merchantUid) {
        SubscribeOrder order = orderRepository.findByMerchantUid(merchantUid).get();
        order.failPaymentSchedule();
        try {
            DirectSendUtils.sendSubscribePaymentScheduleFailAlim(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: 2022-07-25 결제 실패 시 재결제 어떻게 할 것인가?
    }


    @Transactional
    public void rejectCancelRequestOrders(OrderIdListDto requestDto) {
        List<Long> orderIdList = requestDto.getOrderIdList();

        for (Long orderId : orderIdList) {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (!optionalOrder.isPresent()) continue;

            Order order = optionalOrder.get();
            if (order instanceof GeneralOrder) {
                GeneralOrder generalOrder = (GeneralOrder) order;

                generalOrder.rejectCancelRequest(OrderStatus.DELIVERY_READY);

                List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(generalOrder);
                for (OrderItem orderItem : orderItems) {
                    orderItem.rejectCancelRequest();
                }
            }

            if (order instanceof SubscribeOrder) {
                order.rejectCancelRequest(OrderStatus.PRODUCING);
            }

        }


    }
}
