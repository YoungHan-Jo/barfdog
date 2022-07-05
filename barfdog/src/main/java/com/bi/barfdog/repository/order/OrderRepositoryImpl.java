package com.bi.barfdog.repository.order;

import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.domain.dog.QDog;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.QSubscribeOrder;
import com.bi.barfdog.domain.recipe.QRecipe;
import com.bi.barfdog.domain.subscribe.QBeforeSubscribe;
import com.bi.barfdog.domain.subscribe.QSubscribe;
import com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe;
import com.bi.barfdog.domain.surveyReport.QSurveyReport;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.api.orderDto.QueryAdminGeneralOrderDto.*;
import static com.bi.barfdog.api.orderDto.QueryAdminSubscribeOrderDto.*;
import static com.bi.barfdog.domain.coupon.QCoupon.*;
import static com.bi.barfdog.domain.delivery.QDelivery.*;
import static com.bi.barfdog.domain.dog.QDog.*;
import static com.bi.barfdog.domain.item.QItem.*;
import static com.bi.barfdog.domain.member.QMember.*;
import static com.bi.barfdog.domain.memberCoupon.QMemberCoupon.*;
import static com.bi.barfdog.domain.order.QGeneralOrder.generalOrder;
import static com.bi.barfdog.domain.order.QOrder.order;
import static com.bi.barfdog.domain.order.QSubscribeOrder.subscribeOrder;
import static com.bi.barfdog.domain.orderItem.QOrderItem.orderItem;
import static com.bi.barfdog.domain.orderItem.QSelectOption.*;
import static com.bi.barfdog.domain.recipe.QRecipe.*;
import static com.bi.barfdog.domain.subscribe.QBeforeSubscribe.*;
import static com.bi.barfdog.domain.subscribe.QSubscribe.*;
import static com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe.*;
import static com.bi.barfdog.domain.surveyReport.QSurveyReport.*;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<QueryAdminOrdersDto> findAdminOrdersDto(Pageable pageable, OrderAdminCond cond) {


        PageImpl<QueryAdminOrdersDto> queryAdminOrdersDtos = null;

        OrderType orderType = cond.getOrderType();
        if (orderType == OrderType.SUBSCRIBE) {
            queryAdminOrdersDtos = getSubscribeOrdersDtos(pageable, cond);
        } else if(orderType == OrderType.GENERAL){
            queryAdminOrdersDtos = getGeneralOrdersDtos(pageable, cond);
        }

        return queryAdminOrdersDtos;
    }

    @Override
    public QueryAdminGeneralOrderDto findAdminGeneralOrderDto(Long id) {

        OrderInfoDto orderInfoDto = getOrderInfoDto(id);
        List<OrderItemAndOptionDto> orderItemAndOptionDtoList = getOrderItemAndOptionDtoList(id);
        PaymentDto paymentDto = getPaymentDto(id);
        DeliveryDto deliveryDto = getDeliveryDto(id);


        QueryAdminGeneralOrderDto result = QueryAdminGeneralOrderDto.builder()
                .orderInfoDto(orderInfoDto)
                .orderItemAndOptionDtoList(orderItemAndOptionDtoList)
                .paymentDto(paymentDto)
                .deliveryDto(deliveryDto)
                .build();
        return result;
    }

    @Override
    public QueryAdminSubscribeOrderDto findAdminSubscribeOrderDto(Long id) {

        SubscribeOrderInfoDto subscribeOrderInfoDto = getSubscribeOrderInfoDto(id);
        DogDto dogDto = getDogDto(id);
        SubscribeDto subscribeDto = getSubscribeDto(id);
        SubscribeDto beforeSubscribeDto = getSubscribeDto(subscribeDto);
        SubscribePaymentDto subscribePaymentDto = getSubscribePaymentDto(id);
        SubscribeDeliveryDto subscribeDeliveryDto = getSubscribeDeliveryDto(id);

        QueryAdminSubscribeOrderDto result = QueryAdminSubscribeOrderDto.builder()
                .subscribeOrderInfoDto(subscribeOrderInfoDto)
                .dogDto(dogDto)
                .subscribeDto(subscribeDto)
                .beforeSubscribeDto(beforeSubscribeDto)
                .subscribePaymentDto(subscribePaymentDto)
                .subscribeDeliveryDto(subscribeDeliveryDto)
                .build();

        return result;
    }

    private SubscribeDeliveryDto getSubscribeDeliveryDto(Long id) {
        SubscribeDeliveryDto subscribeDeliveryDto = queryFactory
                .select(Projections.constructor(SubscribeDeliveryDto.class,
                        delivery.recipient.name,
                        delivery.recipient.phone,
                        delivery.recipient.zipcode,
                        delivery.recipient.street,
                        delivery.recipient.detailAddress,
                        delivery.departureDate,
                        delivery.arrivalDate,
                        delivery.deliveryNumber
                ))
                .from(order)
                .join(order.delivery, delivery)
                .where(order.id.eq(id))
                .fetchOne();
        return subscribeDeliveryDto;
    }

    private SubscribePaymentDto getSubscribePaymentDto(Long id) {
        SubscribePaymentDto subscribePaymentDto = queryFactory
                .select(Projections.constructor(SubscribePaymentDto.class,
                        subscribeOrder.orderPrice,
                        subscribeOrder.deliveryPrice,
                        subscribeOrder.discountReward,
                        coupon.name,
                        subscribeOrder.discountAmount,
                        subscribeOrder.paymentPrice,
                        subscribeOrder.orderStatus,
                        subscribeOrder.orderConfirmDate
                ))
                .from(subscribeOrder)
                .leftJoin(subscribeOrder.memberCoupon, memberCoupon)
                .leftJoin(memberCoupon.coupon, coupon)
                .where(subscribeOrder.id.eq(id))
                .fetchOne();
        return subscribePaymentDto;
    }

    private SubscribeDto getSubscribeDto(SubscribeDto subscribeDto) {
        SubscribeDto beforeSubscribeDto = queryFactory
                .select(Projections.constructor(SubscribeDto.class,
                        subscribe.id,
                        beforeSubscribe.subscribeCount,
                        beforeSubscribe.plan,
                        beforeSubscribe.oneMealRecommendGram,
                        beforeSubscribe.recipeName
                ))
                .from(subscribe)
                .join(subscribe.beforeSubscribe, beforeSubscribe)
                .where(subscribe.id.eq(subscribeDto.getId()))
                .fetchOne();
        return beforeSubscribeDto;
    }

    private SubscribeDto getSubscribeDto(Long id) {
        SubscribeDto subscribeDto = queryFactory
                .select(Projections.constructor(SubscribeDto.class,
                        subscribe.id,
                        subscribe.subscribeCount,
                        subscribe.plan,
                        surveyReport.foodAnalysis.oneMealRecommendGram,
                        dog.name
                ))
                .from(subscribeOrder)
                .join(subscribeOrder.subscribe, subscribe)
                .join(subscribe.dog, dog)
                .join(dog.surveyReport, surveyReport)
                .where(subscribeOrder.id.eq(id))
                .fetchOne();

        List<String> recipeNames = queryFactory
                .select(recipe.name)
                .from(subscribeRecipe)
                .join(subscribeRecipe.subscribe, subscribe)
                .join(subscribeRecipe.recipe, recipe)
                .where(subscribe.id.eq(subscribeDto.getId()))
                .fetch();

        subscribeDto.changeRecipeName(recipeNames);
        return subscribeDto;
    }

    private DogDto getDogDto(Long id) {
        DogDto dogDto = queryFactory
                .select(Projections.constructor(DogDto.class,
                        dog.name,
                        dog.inedibleFood,
                        dog.inedibleFoodEtc,
                        dog.caution
                ))
                .from(subscribeOrder)
                .join(subscribeOrder.subscribe, subscribe)
                .join(subscribe.dog, dog)
                .where(subscribeOrder.id.eq(id))
                .fetchOne();
        return dogDto;
    }

    private DeliveryDto getDeliveryDto(Long id) {
        DeliveryDto deliveryDto = queryFactory
                .select(Projections.constructor(DeliveryDto.class,
                        delivery.recipient.name,
                        delivery.recipient.phone,
                        delivery.recipient.zipcode,
                        delivery.recipient.street,
                        delivery.recipient.detailAddress,
                        delivery.departureDate,
                        delivery.arrivalDate,
                        delivery.deliveryNumber
                ))
                .from(order)
                .join(order.delivery, delivery)
                .where(order.id.eq(id))
                .fetchOne();
        return deliveryDto;
    }



    private PaymentDto getPaymentDto(Long id) {
        PaymentDto paymentDto = queryFactory
                .select(Projections.constructor(PaymentDto.class,
                        order.orderPrice,
                        order.deliveryPrice,
                        order.discountReward,
                        order.paymentPrice,
                        order.orderStatus,
                        order.orderConfirmDate
                ))
                .from(order)
                .where(order.id.eq(id))
                .fetchOne();
        return paymentDto;
    }

    private List<OrderItemAndOptionDto> getOrderItemAndOptionDtoList(Long id) {

        List<OrderItemDto> orderItemDtoList = queryFactory
                .select(Projections.constructor(OrderItemDto.class,
                        orderItem.id,
                        item.name,
                        orderItem.amount,
                        orderItem.finalPrice,
                        coupon.name,
                        orderItem.discountAmount,
                        orderItem.status
                ))
                .from(orderItem)
                .join(orderItem.item, item)
                .leftJoin(orderItem.memberCoupon, memberCoupon)
                .leftJoin(memberCoupon.coupon, coupon)
                .where(orderItem.generalOrder.id.eq(id))
                .fetch();

        List<OrderItemAndOptionDto> orderItemAndOptionDtoList = new ArrayList<>();

        for (OrderItemDto orderItemDto : orderItemDtoList) {
            List<SelectOptionDto> selectOptionDtoList = queryFactory
                    .select(Projections.constructor(SelectOptionDto.class,
                            selectOption.name,
                            selectOption.price,
                            selectOption.amount
                    ))
                    .from(selectOption)
                    .where(selectOption.orderItem.id.eq(orderItemDto.getOrderItemId()))
                    .fetch();

            OrderItemAndOptionDto orderItemAndOptionDto = OrderItemAndOptionDto.builder()
                    .orderItemDto(orderItemDto)
                    .selectOptionDtoList(selectOptionDtoList)
                    .build();

            orderItemAndOptionDtoList.add(orderItemAndOptionDto);
        }
        return orderItemAndOptionDtoList;
    }

    private OrderInfoDto getOrderInfoDto(Long id) {
        OrderInfoDto orderInfoDto = queryFactory
                .select(Projections.constructor(OrderInfoDto.class,
                        generalOrder.id,
                        generalOrder.merchantUid,
                        generalOrder.createdDate,
                        Expressions.constant("general"),
                        generalOrder.isPackage,
                        member.name,
                        member.phoneNumber,
                        member.email,
                        member.isSubscribe
                ))
                .from(generalOrder)
                .join(generalOrder.member, member)
                .where(generalOrder.id.eq(id))
                .fetchOne();
        return orderInfoDto;
    }

    private SubscribeOrderInfoDto getSubscribeOrderInfoDto(Long id) {
        SubscribeOrderInfoDto subscribeOrderInfoDto = queryFactory
                .select(Projections.constructor(SubscribeOrderInfoDto.class,
                        generalOrder.id,
                        generalOrder.merchantUid,
                        generalOrder.createdDate,
                        Expressions.constant("subscribe"),
                        generalOrder.isPackage,
                        member.name,
                        member.phoneNumber,
                        member.email,
                        member.isSubscribe
                ))
                .from(generalOrder)
                .join(generalOrder.member, member)
                .where(generalOrder.id.eq(id))
                .fetchOne();
        return subscribeOrderInfoDto;
    }

    private PageImpl<QueryAdminOrdersDto> getSubscribeOrdersDtos(Pageable pageable, OrderAdminCond cond) {
        LocalDateTime from = cond.getFrom().atStartOfDay();
        LocalDateTime to = cond.getTo().atTime(23, 59, 59);

        List<QueryAdminOrdersDto> result = queryFactory
                .select(Projections.constructor(QueryAdminOrdersDto.class,
                        order.id,
                        Expressions.constant("subscribe"),
                        order.merchantUid,
                        subscribeOrder.subscribe.id,
                        order.orderStatus,
                        order.createdDate,
                        order.member.email,
                        order.member.name,
                        order.member.phoneNumber,
                        order.delivery.recipient.name,
                        order.delivery.recipient.phone,
                        order.isPackage
                ))
                .from(order)
                .join(subscribeOrder).on(subscribeOrder.eq(order))
                .where(
                        order.createdDate.between(from, to),
                        merchantUidEq(cond.getMerchantUid()),
                        memberNameEq(cond.getMemberName()),
                        memberEmailEq(cond.getMemberEmail()),
                        recipientNameEq(cond.getRecipientName()),
                        orderStatusEq(cond.getStatus())
                )
                .orderBy(order.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(order.count())
                .from(order)
                .join(subscribeOrder).on(subscribeOrder.eq(order))
                .where(
                        order.createdDate.between(from, to),
                        merchantUidEq(cond.getMerchantUid()),
                        memberNameEq(cond.getMemberName()),
                        memberEmailEq(cond.getMemberEmail()),
                        recipientNameEq(cond.getRecipientName()),
                        orderStatusEq(cond.getStatus())
                )
                .fetchOne();
        return new PageImpl<>(result, pageable, totalCount);
    }



    private PageImpl<QueryAdminOrdersDto> getGeneralOrdersDtos(Pageable pageable, OrderAdminCond cond) {
        LocalDateTime from = cond.getFrom().atStartOfDay();
        LocalDateTime to = cond.getTo().atTime(23, 59, 59);

        List<QueryAdminOrdersDto> result = queryFactory
                .select(Projections.constructor(QueryAdminOrdersDto.class,
                        generalOrder.id,
                        Expressions.constant("general"),
                        generalOrder.merchantUid,
                        orderItem.id,
                        generalOrder.orderStatus,
                        generalOrder.createdDate,
                        generalOrder.member.email,
                        generalOrder.member.name,
                        generalOrder.member.phoneNumber,
                        generalOrder.delivery.recipient.name,
                        generalOrder.delivery.recipient.phone,
                        generalOrder.isPackage
                ))
                .from(orderItem)
                .join(orderItem.generalOrder, generalOrder)
                .where(
                        generalOrder.createdDate.between(from, to),
                        orderItemMerchantUidEq(cond.getMerchantUid()),
                        orderItemMemberNameEq(cond.getMemberName()),
                        orderItemMemberEmail(cond.getMemberEmail()),
                        orderItemRecipientNameEq(cond.getRecipientName()),
                        orderItemStatusEq(cond.getStatus())
                )
                .orderBy(generalOrder.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(orderItem.count())
                .from(orderItem)
                .join(orderItem.generalOrder, generalOrder)
                .where(
                        generalOrder.createdDate.between(from, to),
                        orderItemMerchantUidEq(cond.getMerchantUid()),
                        orderItemMemberNameEq(cond.getMemberName()),
                        orderItemMemberEmail(cond.getMemberEmail()),
                        orderItemRecipientNameEq(cond.getRecipientName()),
                        orderItemStatusEq(cond.getStatus())
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression orderItemStatusEq(OrderStatus orderstatus) {
        if (orderstatus == com.bi.barfdog.domain.order.OrderStatus.ALL) {
            return null;
        }
        return orderItem.status.eq(orderstatus);
    }

    private BooleanExpression orderItemRecipientNameEq(String name) {
        return isNotEmpty(name) ? generalOrder.delivery.recipient.name.eq(name) : null;
    }

    private BooleanExpression orderItemMemberEmail(String email) {
        return isNotEmpty(email) ? orderItem.generalOrder.member.email.eq(email) : null;
    }

    private BooleanExpression orderItemMemberNameEq(String name) {
        return isNotEmpty(name) ? orderItem.generalOrder.member.name.eq(name) : null;
    }

    private BooleanExpression orderItemMerchantUidEq(String merchantUid) {
        return isNotEmpty(merchantUid) ? orderItem.generalOrder.merchantUid.eq(merchantUid) : null;
    }

    private BooleanExpression orderStatusEq(com.bi.barfdog.domain.order.OrderStatus orderStatus) {
        if (orderStatus == com.bi.barfdog.domain.order.OrderStatus.ALL) {
            return null;
        } else {
            return order.orderStatus.eq(orderStatus);
        }
    }

    private BooleanExpression recipientNameEq(String name) {
        return isNotEmpty(name) ? order.delivery.recipient.name.eq(name) : null;
    }

    private BooleanExpression memberEmailEq(String email) {
        return isNotEmpty(email) ? order.member.email.eq(email) : null;
    }

    private BooleanExpression memberNameEq(String name) {
        return isNotEmpty(name) ? order.member.name.eq(name) : null;
    }

    private BooleanExpression merchantUidEq(String merchantUid) {
        return isNotEmpty(merchantUid) ? order.merchantUid.eq(merchantUid) : null;
    }

    private boolean isNotEmpty(String merchantUid) {
        return merchantUid != null && merchantUid.trim().length() > 0;
    }


}
