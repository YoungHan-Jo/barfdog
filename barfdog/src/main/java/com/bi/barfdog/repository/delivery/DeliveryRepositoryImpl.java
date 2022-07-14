package com.bi.barfdog.repository.delivery;

import com.bi.barfdog.api.deliveryDto.QueryGeneralDeliveriesDto;
import com.bi.barfdog.api.deliveryDto.QuerySubscribeDeliveriesDto;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.item.QItem;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.QGeneralOrder;
import com.bi.barfdog.domain.orderItem.QOrderItem;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.domain.delivery.QDelivery.*;
import static com.bi.barfdog.domain.dog.QDog.*;
import static com.bi.barfdog.domain.item.QItem.*;
import static com.bi.barfdog.domain.member.QMember.*;
import static com.bi.barfdog.domain.order.QGeneralOrder.*;
import static com.bi.barfdog.domain.order.QSubscribeOrder.*;
import static com.bi.barfdog.domain.orderItem.QOrderItem.*;
import static com.bi.barfdog.domain.recipe.QRecipe.*;
import static com.bi.barfdog.domain.subscribe.QSubscribe.*;
import static com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe.*;

@RequiredArgsConstructor
@Repository
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<QuerySubscribeDeliveriesDto> findSubscribeDeliveriesDto(Member member, Pageable pageable) {
        List<QuerySubscribeDeliveriesDto.DeliveryDto> deliveryDtoList = queryFactory
                .select(Projections.constructor(QuerySubscribeDeliveriesDto.DeliveryDto.class,
                        subscribeOrder.id,
                        dog.name,
                        subscribeOrder.createdDate,
                        subscribeOrder.subscribeCount,
                        dog.name,
                        subscribe.nextDeliveryDate,
                        subscribe.nextDeliveryDate,
                        delivery.status,
                        delivery.deliveryNumber
                ))
                .from(subscribeOrder)
                .join(subscribeOrder.delivery, delivery)
                .join(subscribeOrder.subscribe, subscribe)
                .join(subscribe.dog, dog)
                .where(subscribeOrder.member.eq(member)
                        .and(subscribeOrder.orderStatus.notIn(OrderStatus.CONFIRM, OrderStatus.CANCEL_DONE, OrderStatus.RETURN_DONE,
                                OrderStatus.EXCHANGE_DONE, OrderStatus.FAILED, OrderStatus.BEFORE_PAYMENT, OrderStatus.HOLD, OrderStatus.SELLING_CANCEL))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(subscribeOrder.createdDate.desc())
                .fetch();

        List<QuerySubscribeDeliveriesDto> result = new ArrayList<>();

        for (QuerySubscribeDeliveriesDto.DeliveryDto deliveryDto : deliveryDtoList) {
            deliveryDto.changeUrlAndDate(deliveryDto.getOrderId(), deliveryDto.getProduceDate());

            List<String> nameList = queryFactory
                    .select(recipe.name)
                    .from(subscribeRecipe)
                    .join(subscribeRecipe.recipe, recipe)
                    .join(subscribeRecipe.subscribe, subscribe)
                    .join(subscribeOrder).on(subscribeOrder.subscribe.eq(subscribe))
                    .where(subscribeOrder.id.eq(deliveryDto.getOrderId()))
                    .fetch();
            String recipeName = nameList.get(0);

            QuerySubscribeDeliveriesDto querySubscribeDeliveriesDto = QuerySubscribeDeliveriesDto.builder()
                    .deliveryDto(deliveryDto)
                    .recipeName(recipeName)
                    .build();

            result.add(querySubscribeDeliveriesDto);
        }

        Long totalCount = queryFactory
                .select(subscribeOrder.count())
                .from(subscribeOrder)
                .where(subscribeOrder.member.eq(member)
                        .and(subscribeOrder.orderStatus.notIn(OrderStatus.CONFIRM, OrderStatus.CANCEL_DONE, OrderStatus.RETURN_DONE,
                                OrderStatus.EXCHANGE_DONE, OrderStatus.FAILED, OrderStatus.BEFORE_PAYMENT, OrderStatus.HOLD, OrderStatus.SELLING_CANCEL))
                )
                .fetchOne();


        return new PageImpl<>(result, pageable, totalCount);
    }


    @Override
    public Page<QueryGeneralDeliveriesDto> findGeneralDeliveriesDto(Member member, Pageable pageable) {

        List<QueryGeneralDeliveriesDto.OrderDeliveryDto> orderDeliveryDtos = queryFactory
                .select(Projections.constructor(QueryGeneralDeliveriesDto.OrderDeliveryDto.class,
                        generalOrder.id,
                        Expressions.constant("url"),
                        generalOrder.createdDate,
                        delivery.status,
                        delivery.deliveryNumber
                ))
                .from(generalOrder)
                .join(generalOrder.delivery, delivery)
                .where(generalOrder.member.eq(member)
                        .and(generalOrder.orderStatus.notIn(OrderStatus.CONFIRM, OrderStatus.CANCEL_DONE, OrderStatus.RETURN_DONE,
                                OrderStatus.EXCHANGE_DONE, OrderStatus.FAILED, OrderStatus.BEFORE_PAYMENT, OrderStatus.HOLD, OrderStatus.SELLING_CANCEL))
                )
                .orderBy(generalOrder.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<QueryGeneralDeliveriesDto> result = getQueryGeneralDeliveriesDtos(orderDeliveryDtos);

        Long totalCount = queryFactory
                .select(generalOrder.count())
                .from(generalOrder)
                .join(generalOrder.delivery, delivery)
                .where(generalOrder.member.eq(member)
                        .and(generalOrder.orderStatus.notIn(OrderStatus.CONFIRM, OrderStatus.CANCEL_DONE, OrderStatus.RETURN_DONE,
                                OrderStatus.EXCHANGE_DONE, OrderStatus.FAILED, OrderStatus.BEFORE_PAYMENT, OrderStatus.HOLD, OrderStatus.SELLING_CANCEL))
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private List<QueryGeneralDeliveriesDto> getQueryGeneralDeliveriesDtos(List<QueryGeneralDeliveriesDto.OrderDeliveryDto> orderDeliveryDtos) {
        List<QueryGeneralDeliveriesDto> result = new ArrayList<>();

        for (QueryGeneralDeliveriesDto.OrderDeliveryDto orderDeliveryDto : orderDeliveryDtos) {
            orderDeliveryDto.changeUrl(orderDeliveryDto.getOrderId());

            List<String> itemNameList = getItemNameList(orderDeliveryDto);

            QueryGeneralDeliveriesDto queryGeneralDeliveriesDto = QueryGeneralDeliveriesDto.builder()
                    .orderDeliveryDto(orderDeliveryDto)
                    .itemNameList(itemNameList)
                    .build();
            result.add(queryGeneralDeliveriesDto);
        }
        return result;
    }

    private List<String> getItemNameList(QueryGeneralDeliveriesDto.OrderDeliveryDto orderDeliveryDto) {
        List<String> itemNameList = queryFactory
                .select(item.name)
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.generalOrder.id.eq(orderDeliveryDto.getOrderId()))
                .fetch();
        return itemNameList;
    }

    @Override
    public List<Delivery> findPackageDeliveryDto(Member user) {
        return queryFactory
                .select(delivery)
                .from(subscribeOrder)
                .join(subscribeOrder.delivery, delivery)
                .join(subscribeOrder.subscribe, subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .where(member.eq(user)
                        .and(orderStatusBeforeDelivery()
                                .and(delivery.nextDeliveryDate.after(LocalDate.now().plusDays(1)))
                        ))
                .orderBy(delivery.nextDeliveryDate.asc())
                .fetch()
                ;
    }



    private BooleanExpression orderStatusBeforeDelivery() {
        return subscribeOrder.orderStatus.in(OrderStatus.PAYMENT_DONE,
                OrderStatus.PRODUCING, OrderStatus.DELIVERY_READY);
    }

}
