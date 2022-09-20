package com.bi.barfdog.repository.orderItem;

import com.bi.barfdog.api.orderDto.QueryAdminGeneralOrderDto;
import com.bi.barfdog.api.orderDto.QueryAdminOrderItemDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.QGeneralOrder;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.coupon.QCoupon.coupon;
import static com.bi.barfdog.domain.delivery.QDelivery.delivery;
import static com.bi.barfdog.domain.item.QItem.item;
import static com.bi.barfdog.domain.member.QMember.member;
import static com.bi.barfdog.domain.memberCoupon.QMemberCoupon.memberCoupon;
import static com.bi.barfdog.domain.order.QGeneralOrder.*;
import static com.bi.barfdog.domain.order.QOrder.order;
import static com.bi.barfdog.domain.orderItem.QOrderItem.*;
import static com.bi.barfdog.domain.orderItem.QSelectOption.selectOption;

@RequiredArgsConstructor
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<OrderItem> findWriteableByMember(Member member) {
        return queryFactory
                .select(orderItem)
                .from(orderItem)
                .where(orderItem.generalOrder.member.eq(member).and(orderItem.writeableReview.isTrue()))
                .fetch()
                ;
    }

    @Override
    public QueryAdminOrderItemDto findAdminOrderItemDto(Long id) {

        QueryAdminOrderItemDto.OrderInfoDto orderInfoDto = getOrderInfoDto(id);
        QueryAdminOrderItemDto.OrderItemAndOptionDto orderItemAndOptionDto = getOrderItemAndOptionDto(id);
        QueryAdminOrderItemDto.PaymentDto paymentDto = getPaymentDto(id);
        QueryAdminOrderItemDto.DeliveryDto deliveryDto = getDeliveryDto(id);

        QueryAdminOrderItemDto result = QueryAdminOrderItemDto.builder()
                .orderInfoDto(orderInfoDto)
                .orderItemAndOptionDto(orderItemAndOptionDto)
                .paymentDto(paymentDto)
                .deliveryDto(deliveryDto)
                .build();

        return result;
    }

    private QueryAdminOrderItemDto.DeliveryDto getDeliveryDto(Long id) {
        QueryAdminOrderItemDto.DeliveryDto deliveryDto = queryFactory
                .select(Projections.constructor(QueryAdminOrderItemDto.DeliveryDto.class,
                        delivery.recipient.name,
                        delivery.recipient.phone,
                        delivery.recipient.zipcode,
                        delivery.recipient.street,
                        delivery.recipient.detailAddress,
                        delivery.departureDate,
                        delivery.arrivalDate,
                        delivery.deliveryNumber,
                        delivery.request
                ))
                .from(orderItem)
                .join(orderItem.generalOrder, generalOrder)
                .join(generalOrder.delivery, delivery)
                .where(orderItem.id.eq(id))
                .fetchOne();
        return deliveryDto;
    }

    private QueryAdminOrderItemDto.PaymentDto getPaymentDto(Long id) {
        QueryAdminOrderItemDto.PaymentDto paymentDto = queryFactory
                .select(Projections.constructor(QueryAdminOrderItemDto.PaymentDto.class,
                        generalOrder.orderPrice,
                        generalOrder.deliveryPrice,
                        generalOrder.discountReward,
                        generalOrder.discountCoupon,
                        generalOrder.paymentPrice,
                        generalOrder.paymentMethod,
                        generalOrder.orderStatus,
                        generalOrder.orderConfirmDate
                ))
                .from(orderItem)
                .join(orderItem.generalOrder, generalOrder)
                .where(orderItem.id.eq(id))
                .fetchOne();
        return paymentDto;
    }

    private QueryAdminOrderItemDto.OrderItemAndOptionDto getOrderItemAndOptionDto(Long id) {
        QueryAdminOrderItemDto.OrderItemDto orderItemDto = getOrderItemDto(id);

        List<QueryAdminOrderItemDto.SelectOptionDto> selectOptionDtoList = getSelectOptionDtos(id);

        QueryAdminOrderItemDto.OrderItemAndOptionDto orderItemAndOptionDto = QueryAdminOrderItemDto.OrderItemAndOptionDto.builder()
                .orderItemDto(orderItemDto)
                .selectOptionDtoList(selectOptionDtoList)
                .build();
        return orderItemAndOptionDto;
    }

    private List<QueryAdminOrderItemDto.SelectOptionDto> getSelectOptionDtos(Long id) {
        List<QueryAdminOrderItemDto.SelectOptionDto> selectOptionDtoList = queryFactory
                .select(Projections.constructor(QueryAdminOrderItemDto.SelectOptionDto.class,
                        selectOption.name,
                        selectOption.price,
                        selectOption.amount
                ))
                .from(selectOption)
                .where(selectOption.orderItem.id.eq(id))
                .fetch();
        return selectOptionDtoList;
    }

    private QueryAdminOrderItemDto.OrderItemDto getOrderItemDto(Long id) {
        QueryAdminOrderItemDto.OrderItemDto orderItemDto = queryFactory
                .select(Projections.constructor(QueryAdminOrderItemDto.OrderItemDto.class,
                        orderItem.id,
                        item.name,
                        orderItem.amount,
                        orderItem.finalPrice,
                        coupon.name,
                        orderItem.discountAmount,
                        orderItem.status,
                        orderItem.orderCancel.cancelReason,
                        orderItem.orderCancel.cancelDetailReason,
                        orderItem.orderCancel.cancelRequestDate,
                        orderItem.orderCancel.cancelConfirmDate,
                        orderItem.orderReturn.returnReason,
                        orderItem.orderReturn.returnDetailReason,
                        orderItem.orderReturn.returnRequestDate,
                        orderItem.orderReturn.returnConfirmDate,
                        orderItem.orderExchange.exchangeReason,
                        orderItem.orderExchange.exchangeDetailReason,
                        orderItem.orderExchange.exchangeRequestDate,
                        orderItem.orderExchange.exchangeConfirmDate
                ))
                .from(orderItem)
                .join(orderItem.item, item)
                .leftJoin(orderItem.memberCoupon, memberCoupon)
                .leftJoin(memberCoupon.coupon, coupon)
                .where(orderItem.id.eq(id))
                .fetchOne();
        return orderItemDto;
    }

    private QueryAdminOrderItemDto.OrderInfoDto getOrderInfoDto(Long id) {
        QueryAdminOrderItemDto.OrderInfoDto orderInfoDto = queryFactory
                .select(Projections.constructor(QueryAdminOrderItemDto.OrderInfoDto.class,
                        generalOrder.id,
                        generalOrder.merchantUid,
                        generalOrder.createdDate,
                        Expressions.constant("general"),
                        generalOrder.isPackage,
                        member.name,
                        member.phoneNumber,
                        member.email,
                        member.isSubscribe,
                        generalOrder.orderStatus,
                        generalOrder.orderCancel.cancelRequestDate,
                        generalOrder.orderCancel.cancelConfirmDate,
                        generalOrder.orderCancel.cancelReason,
                        generalOrder.orderCancel.cancelDetailReason
                ))
                .from(orderItem)
                .join(orderItem.generalOrder, generalOrder)
                .join(generalOrder.member, member)
                .where(orderItem.id.eq(id))
                .fetchOne();
        return orderInfoDto;
    }
}
