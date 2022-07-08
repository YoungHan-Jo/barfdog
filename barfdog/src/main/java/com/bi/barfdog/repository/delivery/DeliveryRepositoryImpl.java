package com.bi.barfdog.repository.delivery;

import com.bi.barfdog.api.deliveryDto.QueryDeliveriesDto;
import com.bi.barfdog.domain.delivery.QDelivery;
import com.bi.barfdog.domain.dog.QDog;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.QSubscribeOrder;
import com.bi.barfdog.domain.recipe.QRecipe;
import com.bi.barfdog.domain.subscribe.QSubscribe;
import com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.domain.delivery.QDelivery.*;
import static com.bi.barfdog.domain.dog.QDog.*;
import static com.bi.barfdog.domain.order.QSubscribeOrder.*;
import static com.bi.barfdog.domain.recipe.QRecipe.*;
import static com.bi.barfdog.domain.subscribe.QSubscribe.*;
import static com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe.*;

@RequiredArgsConstructor
@Repository
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<QueryDeliveriesDto> findDeliveriesDto(Member member, Pageable pageable) {
        List<QueryDeliveriesDto.DeliveryDto> deliveryDtoList = queryFactory
                .select(Projections.constructor(QueryDeliveriesDto.DeliveryDto.class,
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
                        .and(subscribeOrder.orderStatus.notIn(OrderStatus.CONFIRM,OrderStatus.CANCEL_DONE,OrderStatus.RETURN_DONE,OrderStatus.EXCHANGE_DONE))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(subscribeOrder.createdDate.desc())
                .fetch();

        List<QueryDeliveriesDto> result = new ArrayList<>();

        for (QueryDeliveriesDto.DeliveryDto deliveryDto : deliveryDtoList) {
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

            QueryDeliveriesDto queryDeliveriesDto = QueryDeliveriesDto.builder()
                    .deliveryDto(deliveryDto)
                    .recipeName(recipeName)
                    .build();

            result.add(queryDeliveriesDto);
        }

        Long totalCount = queryFactory
                .select(subscribeOrder.count())
                .from(subscribeOrder)
                .where(subscribeOrder.member.eq(member)
                        .and(subscribeOrder.orderStatus.notIn(OrderStatus.CONFIRM, OrderStatus.CANCEL_DONE, OrderStatus.RETURN_DONE, OrderStatus.EXCHANGE_DONE))
                )
                .fetchOne();


        return new PageImpl<>(result, pageable, totalCount);
    }
}
