package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.itemDto.ItemsCond;
import com.bi.barfdog.api.itemDto.QueryItemAdminDto;
import com.bi.barfdog.api.itemDto.QueryItemsAdminDto;
import com.bi.barfdog.api.itemDto.QueryItemsDto;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import com.bi.barfdog.domain.item.QItemImage;
import com.bi.barfdog.domain.review.QItemReview;
import com.bi.barfdog.domain.review.QReview;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.item.QItem.*;
import static com.bi.barfdog.domain.item.QItemImage.*;
import static com.bi.barfdog.domain.item.QItemOption.*;
import static com.bi.barfdog.domain.review.QItemReview.*;
import static com.bi.barfdog.domain.review.QReview.*;
import static com.querydsl.jpa.JPAExpressions.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
                        item.itemIcons,
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

    @Override
    public Page<QueryItemsDto> findItemsDto(Pageable pageable, ItemsCond cond) {

        List<QueryItemsDto> result = queryFactory
                .select(Projections.constructor(QueryItemsDto.class,
                        item.id,
                        itemImage.filename,
                        item.name,
                        item.originalPrice,
                        item.salePrice,
                        item.inStock,
                        MathExpressions.round(itemReview.star.avg(),1),
                        itemReview.count()
                ))
                .from(item)
                .join(itemImage).on(itemImage.item.eq(item))
                .leftJoin(itemReview).on(itemReview.item.eq(item))
                .where(itemTypeEq(cond.getItemType()))
                .groupBy(itemReview.item)
                .orderBy(item.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        for (QueryItemsDto dto : result) {
            dto.changeUrl(dto.getThumbnailUrl());
        }

        Long totalCount = queryFactory
                .select(item.count())
                .from(item)
                .where(itemTypeEqForCount(cond))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression itemTypeEqForCount(ItemsCond cond) {
        switch (cond.getItemType()) {
            case GOODS:
                return item.itemType.eq(ItemType.GOODS).and(item.status.eq(ItemStatus.LEAKED));
            case RAW:
                return item.itemType.eq(ItemType.RAW).and(item.status.eq(ItemStatus.LEAKED));
            case TOPPING:
                return item.itemType.eq(ItemType.TOPPING).and(item.status.eq(ItemStatus.LEAKED));
            default:
                return item.status.eq(ItemStatus.LEAKED);
        }
    }

    private BooleanExpression itemTypeEq() {
        return item.itemType.eq(ItemType.RAW);
    }

    private JPQLQuery<Long> getOptionCount() {
        return select(itemOption.count())
                .from(itemOption)
                .where(itemOption.item.id.eq(item.id));
    }

    private BooleanExpression itemTypeEq(ItemType itemType) {
        switch (itemType) {
            case GOODS:
                return item.itemType.eq(ItemType.GOODS).and(leakedAndImageOrderFirst());
            case RAW:
                return item.itemType.eq(ItemType.RAW).and(leakedAndImageOrderFirst());
            case TOPPING:
                return item.itemType.eq(ItemType.TOPPING).and(leakedAndImageOrderFirst());
            default:
                return leakedAndImageOrderFirst();
        }
    }

    private BooleanExpression leakedAndImageOrderFirst() {
        return itemImage.leakOrder.eq(1).and(item.status.eq(ItemStatus.LEAKED));
    }
}
