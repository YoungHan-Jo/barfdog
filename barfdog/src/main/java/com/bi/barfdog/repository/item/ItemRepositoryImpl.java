package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.itemDto.*;
import com.bi.barfdog.api.reviewDto.ReviewItemsDto;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
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
import static com.bi.barfdog.domain.setting.QSetting.*;
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
                        item.itemIcons,
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
                        item.itemType,
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
                .where(adminItemTypesEq(itemType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.createdDate.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(item.count())
                .from(item)
                .where(adminItemTypesEq(itemType))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression adminItemTypesEq(ItemType itemType) {
        switch (itemType) {
            case GOODS:
                return item.itemType.eq(ItemType.GOODS).and(itemIsDeletedFalse());
            case RAW:
                return item.itemType.eq(ItemType.RAW).and(itemIsDeletedFalse());
            case TOPPING:
                return item.itemType.eq(ItemType.TOPPING).and(itemIsDeletedFalse());
            default:
                return itemIsDeletedFalse();
        }
    }

    private BooleanExpression itemIsDeletedFalse() {
        return itemIsDeleteFalse();
    }

    @Override
    public Page<QueryItemsDto> findItemsDto(Pageable pageable, ItemsCond cond) {

        List<QueryItemsDto> result = queryFactory
                .select(Projections.constructor(QueryItemsDto.class,
                        item.id,
                        itemImage.filename,
                        item.itemIcons,
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
                .where(itemTypeEq(cond.getItemType()).and(itemImage.leakOrder.eq(1)))
                .groupBy(item)
                .orderBy(orderByCond(cond.getSortBy()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        for (QueryItemsDto dto : result) {
            dto.changeUrl(dto.getThumbnailUrl());
        }

        Long totalCount = queryFactory
                .select(item.count())
                .from(item)
                .where(itemTypeEq(cond.getItemType()))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public QueryItemDto findItemDtoById(Long id) {

        QueryItemDto.ItemDto itemDto = queryFactory
                .select(Projections.constructor(QueryItemDto.ItemDto.class,
                        item.id,
                        item.name,
                        item.description,
                        item.originalPrice,
                        item.discountType,
                        item.discountDegree,
                        item.salePrice,
                        item.inStock,
                        item.remaining,
                        item.totalSalesAmount,
                        item.contents,
                        item.itemIcons,
                        item.deliveryFree
                ))
                .from(item)
                .where(item.id.eq(id))
                .fetchOne();

        QueryItemDto.DeliveryCondDto deliveryCondDto = queryFactory
                .select(Projections.constructor(QueryItemDto.DeliveryCondDto.class,
                        setting.deliveryConstant.price,
                        setting.deliveryConstant.freeCondition
                ))
                .from(setting)
                .fetchOne();

        List<QueryItemDto.ItemOptionDto> itemOptionDtoList = queryFactory
                .select(Projections.constructor(QueryItemDto.ItemOptionDto.class,
                        itemOption.id,
                        itemOption.name,
                        itemOption.optionPrice,
                        itemOption.remaining
                ))
                .from(itemOption)
                .where(itemOption.item.id.eq(id))
                .fetch();

        List<QueryItemDto.ItemImageDto> itemImageDtoList = queryFactory
                .select(Projections.constructor(QueryItemDto.ItemImageDto.class,
                        itemImage.id,
                        itemImage.leakOrder,
                        itemImage.filename,
                        itemImage.filename
                ))
                .from(itemImage)
                .where(itemImage.item.id.eq(id))
                .fetch();
        for (QueryItemDto.ItemImageDto dto : itemImageDtoList) {
            dto.changeUrl(dto.getFilename());
        }

        QueryItemDto.ReviewDto reviewDto = queryFactory
                .select(Projections.constructor(QueryItemDto.ReviewDto.class,
                        MathExpressions.round(itemReview.star.avg(), 1),
                        itemReview.count()
                ))
                .from(itemReview)
                .where(itemReview.item.id.eq(id))
                .fetchOne();


        QueryItemDto result = QueryItemDto.builder()
                .itemDto(itemDto)
                .deliveryCondDto(deliveryCondDto)
                .itemOptionDtoList(itemOptionDtoList)
                .itemImageDtoList(itemImageDtoList)
                .reviewDto(reviewDto)
                .build();
        return result;
    }

    @Override
    public List<ReviewItemsDto> findReviewItemsDtoByItemType(ItemType itemType) {
        return queryFactory
                .select(Projections.constructor(ReviewItemsDto.class,
                        item.id,
                        item.name
                ))
                .from(item)
                .where(itemTypeEq(itemType))
                .orderBy(item.createdDate.desc())
                .fetch();
    }

    private BooleanExpression itemTypeEq(ItemType itemType) {
        switch (itemType) {
            case GOODS:
                return item.itemType.eq(ItemType.GOODS).and(leakedItem().and(itemIsDeleteFalse()));
            case RAW:
                return item.itemType.eq(ItemType.RAW).and(leakedItem().and(itemIsDeleteFalse()));
            case TOPPING:
                return item.itemType.eq(ItemType.TOPPING).and(leakedItem().and(itemIsDeleteFalse()));
            default:
                return leakedItem().and(itemIsDeleteFalse());
        }
    }

    private BooleanExpression leakedItem() {
        return item.status.eq(ItemStatus.LEAKED);
    }

    private BooleanExpression itemIsDeleteFalse() {
        return item.isDeleted.eq(false);
    }


    private OrderSpecifier<? extends Comparable<? extends Comparable<?>>> orderByCond(String cond) { // recent, registration, saleAmount
        switch (cond) {
            case "registration":
                return item.createdDate.asc();
            case "saleAmount":
                return item.totalSalesAmount.desc();
            default:
                return item.createdDate.desc();
        }
    }

    private BooleanExpression itemTypeEqForCount(ItemsCond cond) {
        switch (cond.getItemType()) {
            case GOODS:
                return item.itemType.eq(ItemType.GOODS).and(leakedItem());
            case RAW:
                return item.itemType.eq(ItemType.RAW).and(leakedItem());
            case TOPPING:
                return item.itemType.eq(ItemType.TOPPING).and(leakedItem());
            default:
                return leakedItem();
        }
    }


    private JPQLQuery<Long> getOptionCount() {
        return select(itemOption.count())
                .from(itemOption)
                .where(itemOption.item.id.eq(item.id));
    }

    private BooleanExpression itemImageLeakOrderEq1AndItemTypeEq(ItemType itemType) {
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
        return itemImage.leakOrder.eq(1).and(leakedItem());
    }
}
