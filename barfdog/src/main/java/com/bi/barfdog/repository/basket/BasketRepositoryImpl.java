package com.bi.barfdog.repository.basket;

import com.bi.barfdog.api.basketDto.QueryBasketsDto;
import com.bi.barfdog.domain.basket.QBasketOption;
import com.bi.barfdog.domain.item.QItemOption;
import com.bi.barfdog.domain.member.Member;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.domain.basket.QBasket.*;
import static com.bi.barfdog.domain.basket.QBasketOption.*;
import static com.bi.barfdog.domain.item.QItem.*;
import static com.bi.barfdog.domain.item.QItemImage.*;
import static com.bi.barfdog.domain.item.QItemOption.*;

@RequiredArgsConstructor
@Repository
public class BasketRepositoryImpl implements BasketRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QueryBasketsDto> findBasketsDto(Member member) {

        List<QueryBasketsDto.ItemDto> itemDtoList = queryFactory
                .select(Projections.constructor(QueryBasketsDto.ItemDto.class,
                        basket.id,
                        item.id,
                        itemImage.filename,
                        item.name,
                        item.originalPrice,
                        item.salePrice,
                        basket.amount,
                        item.deliveryFree
                ))
                .from(basket)
                .join(basket.item, item)
                .join(itemImage).on(itemImage.item.eq(item))
                .where(basket.member.eq(member).and(itemImage.leakOrder.eq(1)))
                .orderBy(basket.createdDate.asc())
                .fetch();
        List<QueryBasketsDto> result = getQueryBasketsDtos(itemDtoList);

        return result;
    }

    private List<QueryBasketsDto> getQueryBasketsDtos(List<QueryBasketsDto.ItemDto> itemDtoList) {
        List<QueryBasketsDto> result = new ArrayList<>();

        for (QueryBasketsDto.ItemDto itemDto : itemDtoList) {
            itemDto.changeUrl(itemDto.getThumbnailUrl());

            List<QueryBasketsDto.ItemOptionDto> itemOptionDtoList = getItemOptionDtoList(itemDto);

            int totalPrice = getTotalPrice(itemDto, itemOptionDtoList);

            QueryBasketsDto queryBasketsDto = QueryBasketsDto.builder()
                    .itemDto(itemDto)
                    .itemOptionDtoList(itemOptionDtoList)
                    .totalPrice(totalPrice)
                    .build();
            result.add(queryBasketsDto);
        }
        return result;
    }

    private List<QueryBasketsDto.ItemOptionDto> getItemOptionDtoList(QueryBasketsDto.ItemDto itemDto) {
        Long basketId = itemDto.getBasketId();

        List<QueryBasketsDto.ItemOptionDto> itemOptionDtoList = queryFactory
                .select(Projections.constructor(QueryBasketsDto.ItemOptionDto.class,
                        itemOption.id,
                        itemOption.name,
                        itemOption.optionPrice,
                        basketOption.amount
                ))
                .from(basketOption)
                .join(basketOption.itemOption, itemOption)
                .where(basketOption.basket.id.eq(basketId))
                .fetch();
        return itemOptionDtoList;
    }

    private int getTotalPrice(QueryBasketsDto.ItemDto itemDto, List<QueryBasketsDto.ItemOptionDto> itemOptionDtoList) {
        int totalPrice = itemDto.getSalePrice() * itemDto.getAmount();
        for (QueryBasketsDto.ItemOptionDto itemOptionDto : itemOptionDtoList) {
            totalPrice += itemOptionDto.getOptionPrice() * itemOptionDto.getAmount();
        }
        return totalPrice;
    }
}
