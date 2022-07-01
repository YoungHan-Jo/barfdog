package com.bi.barfdog.repository.order;

import com.bi.barfdog.api.orderDto.OrderAdminCond;
import com.bi.barfdog.api.orderDto.OrderType;
import com.bi.barfdog.api.orderDto.QueryAdminOrdersDto;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.QGeneralOrder;
import com.bi.barfdog.domain.order.QOrder;
import com.bi.barfdog.domain.order.QSubscribeOrder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.order.QGeneralOrder.*;
import static com.bi.barfdog.domain.order.QOrder.*;
import static com.bi.barfdog.domain.order.QSubscribeOrder.*;

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
        }else {
            queryAdminOrdersDtos = getAllOrdersDtos(pageable, cond);
        }

        return queryAdminOrdersDtos;
    }

    private PageImpl<QueryAdminOrdersDto> getSubscribeOrdersDtos(Pageable pageable, OrderAdminCond cond) {
        List<QueryAdminOrdersDto> result = queryFactory
                .select(Projections.constructor(QueryAdminOrdersDto.class,
                        order.id,
                        order.merchantUid,
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
        List<QueryAdminOrdersDto> result = queryFactory
                .select(Projections.constructor(QueryAdminOrdersDto.class,
                        order.id,
                        order.merchantUid,
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
                .join(generalOrder).on(generalOrder.eq(order))
                .where(
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
                .join(generalOrder).on(generalOrder.eq(order))
                .where(
                        merchantUidEq(cond.getMerchantUid()),
                        memberNameEq(cond.getMemberName()),
                        memberEmailEq(cond.getMemberEmail()),
                        recipientNameEq(cond.getRecipientName()),
                        orderStatusEq(cond.getStatus())
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private PageImpl<QueryAdminOrdersDto> getAllOrdersDtos(Pageable pageable, OrderAdminCond cond) {
        List<QueryAdminOrdersDto> result = queryFactory
                .select(Projections.constructor(QueryAdminOrdersDto.class,
                        order.id,
                        order.merchantUid,
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
                .leftJoin(generalOrder).on(generalOrder.eq(order))
                .leftJoin(subscribeOrder).on(subscribeOrder.eq(order))
                .where(
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
                .where(
                        merchantUidEq(cond.getMerchantUid()),
                        memberNameEq(cond.getMemberName()),
                        memberEmailEq(cond.getMemberEmail()),
                        recipientNameEq(cond.getRecipientName()),
                        orderStatusEq(cond.getStatus())
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        if (orderStatus == OrderStatus.ALL) {
            return null;
        } else {
            return order.orderStatus.eq(orderStatus);
        }
    }

    private BooleanExpression recipientNameEq(String name) {
        return name != null ? order.delivery.recipient.name.eq(name) : null;
    }

    private BooleanExpression memberEmailEq(String email) {
        return email != null ? order.member.email.eq(email) : null;
    }

    private BooleanExpression memberNameEq(String name) {
        return name != null ? order.member.name.eq(name) : null;
    }

    private BooleanExpression merchantUidEq(String merchantUid) {
        return merchantUid != null ?order.merchantUid.eq(merchantUid) : null;
    }



}
