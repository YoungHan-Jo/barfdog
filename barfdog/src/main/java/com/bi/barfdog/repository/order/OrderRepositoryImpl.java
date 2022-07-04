package com.bi.barfdog.repository.order;

import com.bi.barfdog.api.orderDto.OrderAdminCond;
import com.bi.barfdog.api.orderDto.OrderType;
import com.bi.barfdog.api.orderDto.QueryAdminOrdersDto;
import com.bi.barfdog.domain.order.OrderStatus;
import com.querydsl.core.types.ExpressionUtils;
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
import java.util.List;

import static com.bi.barfdog.domain.order.QGeneralOrder.generalOrder;
import static com.bi.barfdog.domain.order.QOrder.order;
import static com.bi.barfdog.domain.order.QSubscribeOrder.subscribeOrder;
import static com.bi.barfdog.domain.orderItem.QOrderItem.orderItem;

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
