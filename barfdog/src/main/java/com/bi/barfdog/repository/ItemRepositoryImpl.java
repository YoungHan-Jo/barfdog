package com.bi.barfdog.repository;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;
import com.bi.barfdog.api.itemDto.QueryItemsAdminDto;
import com.bi.barfdog.api.itemDto.QueryItemsAdminRequestDto;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.ItemType;
import com.bi.barfdog.domain.item.QItem;
import com.bi.barfdog.domain.item.QItemOption;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.item.QItem.*;
import static com.bi.barfdog.domain.item.QItemOption.*;
import static com.querydsl.jpa.JPAExpressions.*;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public QueryItemAdminDto.ItemAdminDto findAdminDtoById(Long id) {
        return queryFactory
                .select(Projections.constructor(QueryItemAdminDto.ItemAdminDto.class,
                        item.id,
                        item.itemType,
                        item.name,
                        item.description,
                        item.originalPrice,
                        item.discountType,
                        item.discountDegree,
                        item.salePrice,
                        item.inStock,
                        item.remaining,
                        item.contents,
                        item.deliveryFree,
                        item.status
                        ))
                .from(item)
                .where(item.id.eq(id))
                .fetchOne()
                ;
    }

    @Override
    public Page<QueryItemsAdminDto> findAdminDtoList(Pageable pageable, ItemType itemType) {
        List<QueryItemsAdminDto> result = queryFactory
                .select(Projections.constructor(QueryItemsAdminDto.class,
                        item.id,
                        item.name,
                        new CaseBuilder()
                                .when(getOptionCount().gt(0L)).then(true)
                                .otherwise(false),
                        item.originalPrice,
                        item.discountDegree.stringValue().concat(new CaseBuilder()
                                .when(item.discountType.eq(DiscountType.FIXED_RATE)).then("%")
                                .otherwise("원")),
                        item.salePrice,
                        item.status,
                        item.remaining,
                        item.createdDate
                ))
                .from(item)
                .where(itemTypeEq(itemType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.createdDate.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(item.count())
                .from(item)
                .where(itemTypeEq(itemType))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private JPQLQuery<Long> getOptionCount() {
        return select(itemOption.count())
                .from(itemOption)
                .where(itemOption.item.id.eq(item.id));
    }

    private BooleanExpression itemTypeEq(ItemType itemType) {
        return item.itemType.eq(itemType);
    }
}
