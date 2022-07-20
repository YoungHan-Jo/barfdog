package com.bi.barfdog.repository.order;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.barfDto.AdminDashBoardRequestDto;
import com.bi.barfdog.api.barfDto.AdminDashBoardResponseDto;
import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.subscribe.QSubscribe;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.api.orderDto.QueryAdminGeneralOrderDto.*;
import static com.bi.barfdog.api.orderDto.QueryAdminSubscribeOrderDto.*;
import static com.bi.barfdog.domain.coupon.QCoupon.*;
import static com.bi.barfdog.domain.delivery.QDelivery.*;
import static com.bi.barfdog.domain.dog.QDog.*;
import static com.bi.barfdog.domain.item.QItem.*;
import static com.bi.barfdog.domain.item.QItemImage.*;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

    @Override
    public Page<QuerySubscribeOrdersDto> findSubscribeOrdersDto(Member member, Pageable pageable) {
        List<QuerySubscribeOrdersDto> result = getQuerySubscribeOrdersDtosByPaging(member, pageable);

        Long totalCount = queryFactory
                .select(subscribeOrder.count())
                .from(subscribeOrder)
                .where(subscribeOrder.member.eq(member))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public QuerySubscribeOrderDto findSubscribeOrderDto(Long id) {

        QuerySubscribeOrderDto.RecipeDto recipeDto = getRecipeDto(id);
        String recipeNames = getRecipeNames(id);
        QuerySubscribeOrderDto.OrderDto orderDto = getOrderDto(id);

        QuerySubscribeOrderDto result = QuerySubscribeOrderDto.builder()
                .recipeDto(recipeDto)
                .recipeNames(recipeNames)
                .orderDto(orderDto)
                .build();

        return result;
    }

    @Override
    public Page<QueryGeneralOrdersDto> findGeneralOrdersDto(Member member, Pageable pageable) {

        List<QueryGeneralOrdersDto.OrderDto> orderDtoList = queryFactory
                .select(Projections.constructor(QueryGeneralOrdersDto.OrderDto.class,
                        generalOrder.id,
                        generalOrder.merchantUid,
                        generalOrder.paymentPrice,
                        generalOrder.orderStatus
                ))
                .from(generalOrder)
                .where(generalOrder.member.eq(member))
                .orderBy(generalOrder.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<QueryGeneralOrdersDto> result = new ArrayList<>();

        for (QueryGeneralOrdersDto.OrderDto orderDto : orderDtoList) {
            String thumbnailUrl = getThumbnailUrl(orderDto);

            List<String> itemNameList = queryFactory
                    .select(item.name)
                    .from(orderItem)
                    .join(orderItem.item, item)
                    .join(orderItem.generalOrder, generalOrder)
                    .where(generalOrder.id.eq(orderDto.getId()))
                    .fetch();

            QueryGeneralOrdersDto queryGeneralOrdersDto = QueryGeneralOrdersDto.builder()
                    .thumbnailUrl(thumbnailUrl)
                    .orderDto(orderDto)
                    .itemNameList(itemNameList)
                    .build();

            result.add(queryGeneralOrdersDto);
        }

        Long totalCount = queryFactory
                .select(generalOrder.count())
                .from(generalOrder)
                .where(generalOrder.member.eq(member))
                .fetchOne();


        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public QueryGeneralOrderDto findGeneralOrderDto(Long id) {

        int savedRewardTotal = 0;

        List<OrderItem> orderItems = queryFactory
                .select(orderItem)
                .from(orderItem)
                .where(orderItem.generalOrder.id.eq(id))
                .fetch();

        List<QueryGeneralOrderDto.OrderItemDto> orderItemDtoList = new ArrayList<>();

        for (OrderItem orderItemSample : orderItems) {

            List<QueryGeneralOrderDto.SelectOptionDto> selectOptionDtoList = getSelectOptionDtoList(orderItemSample);

            String itemName = getItemName(orderItemSample);

            String thumbnailUrl = getThumbnailUrl(orderItemSample);

            savedRewardTotal += orderItemSample.getSaveReward();

            QueryGeneralOrderDto.OrderItemDto orderItemDto = QueryGeneralOrderDto.OrderItemDto.builder()
                    .orderItemId(orderItemSample.getId())
                    .thumbnailUrl(thumbnailUrl)
                    .selectOptionDtoList(selectOptionDtoList)
                    .itemName(itemName)
                    .amount(orderItemSample.getAmount())
                    .finalPrice(orderItemSample.getFinalPrice())
                    .discountAmount(orderItemSample.getDiscountAmount())
                    .status(orderItemSample.getStatus())
                    .saveReward(orderItemSample.getSaveReward())
                    .orderCancel(orderItemSample.getOrderCancel())
                    .orderReturn(orderItemSample.getOrderReturn())
                    .orderExchange(orderItemSample.getOrderExchange())
                    .build();
            orderItemDtoList.add(orderItemDto);
        }


        QueryGeneralOrderDto.OrderDto orderDto = queryFactory
                .select(Projections.constructor(QueryGeneralOrderDto.OrderDto.class,
                        generalOrder.id,
                        generalOrder.merchantUid,
                        generalOrder.paymentDate,
                        generalOrder.isPackage,
                        delivery.deliveryNumber,
                        generalOrder.orderPrice,
                        generalOrder.deliveryPrice,
                        generalOrder.discountTotal,
                        generalOrder.discountReward,
                        generalOrder.discountCoupon,
                        generalOrder.paymentPrice,
                        generalOrder.paymentMethod,
                        delivery.recipient.name,
                        delivery.recipient.phone,
                        delivery.recipient.zipcode,
                        delivery.recipient.street,
                        delivery.recipient.detailAddress,
                        delivery.request
                ))
                .from(generalOrder)
                .join(generalOrder.delivery, delivery)
                .where(generalOrder.id.eq(id))
                .fetchOne();

        QueryGeneralOrderDto result = QueryGeneralOrderDto.builder()
                .orderItemDtoList(orderItemDtoList)
                .orderDto(orderDto)
                .savedRewardTotal(savedRewardTotal)
                .build();

        return result;
    }

    @Override
    public AdminDashBoardResponseDto findAdminDashBoard(AdminDashBoardRequestDto requestDto) {
        LocalDateTime from = requestDto.getFrom().atStartOfDay();
        LocalDateTime to = requestDto.getTo().atTime(23, 59, 59);

        Long newOrderCount = getNewOrderCount(from, to);
        Long newMemberCount = getNewMemberCount(from, to);
        Long subscribePendingCount = getSubscribePendingCount();

        List<AdminDashBoardResponseDto.OrderStatusCountDto> orderStatusCountDtoList = getOrderStatusCountDtoList();


//        StringTemplate formattedDate = Expressions.stringTemplate(
//                "DATE_FORMAT({0}, {1})"
//                , order.createdDate
//                , ConstantImpl.create("%Y-%m"));
//
//        int year = LocalDate.now().getYear();
//        int month = LocalDate.now().getMonthValue();
//        LocalDateTime oneYearAgo = LocalDate.of(year, month, 1).atStartOfDay().minusYears(1);
//
//
//        List<AdminDashBoardResponseDto.OrderCountByMonth> orderCountByMonthList = queryFactory
//                .select(Projections.constructor(AdminDashBoardResponseDto.OrderCountByMonth.class,
//                        formattedDate.as("month"),
//                        generalOrder.count(),
//                        subscribeOrder.count()
//                ))
//                .from(order)
//                .leftJoin(generalOrder).on(generalOrder.eq(order))
//                .leftJoin(subscribeOrder).on(subscribeOrder.eq(order))
//                .where(order.createdDate.after(oneYearAgo))
//                .groupBy(formattedDate)
//                .orderBy(formattedDate.asc())
//                .fetch();


        AdminDashBoardResponseDto result = AdminDashBoardResponseDto.builder()
                .newOrderCount(newOrderCount)
                .newMemberCount(newMemberCount)
                .subscribePendingCount(subscribePendingCount)
                .orderStatusCountDtoList(orderStatusCountDtoList)
                .orderCountByMonthList(null)
                .build();

        return result;
    }

    private Long getSubscribePendingCount() {
        Long subscribePendingCount = queryFactory
                .select(subscribe.count())
                .from(subscribe)
                .where(subscribe.status.eq(SubscribeStatus.SUBSCRIBE_PENDING))
                .fetchOne();
        return subscribePendingCount;
    }

    private List<AdminDashBoardResponseDto.OrderStatusCountDto> getOrderStatusCountDtoList() {
        List<AdminDashBoardResponseDto.OrderStatusCountDto> orderStatusCountDtoList = queryFactory
                .select(Projections.constructor(AdminDashBoardResponseDto.OrderStatusCountDto.class,
                        order.orderStatus,
                        order.count()
                ))
                .from(order)
                .where(order.orderStatus.in(OrderStatus.FAILED, OrderStatus.PAYMENT_DONE, OrderStatus.DELIVERY_START,
                                OrderStatus.CANCEL_REQUEST, OrderStatus.RETURN_REQUEST, OrderStatus.EXCHANGE_REQUEST)
                        .and(order.createdDate.after(LocalDateTime.now().minusDays(30))))
                .groupBy(order.orderStatus)
                .fetch();
        return orderStatusCountDtoList;
    }

    private Long getNewMemberCount(LocalDateTime from, LocalDateTime to) {
        Long newMemberCount = queryFactory
                .select(member.count())
                .from(member)
                .where(member.createdDate.between(from, to))
                .fetchOne();
        return newMemberCount;
    }

    private Long getNewOrderCount(LocalDateTime from, LocalDateTime to) {
        Long newOrderCount = queryFactory
                .select(order.count())
                .from(order)
                .where(order.createdDate.between(from, to))
                .fetchOne();
        return newOrderCount;
    }

    private String getThumbnailUrl(OrderItem orderItemDto) {
        List<String> filenameList = queryFactory
                .select(itemImage.filename)
                .from(orderItem)
                .join(orderItem.item, item)
                .join(itemImage).on(itemImage.item.eq(item))
                .where(orderItem.eq(orderItemDto))
                .fetch();
        String filename = filenameList.get(0);
        String thumbnailUrl = linkTo(InfoController.class).slash("display/items?filename=" + filename).toString();
        return thumbnailUrl;
    }

    private String getItemName(OrderItem orderItemDto) {
        String itemName = queryFactory
                .select(item.name)
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.eq(orderItemDto))
                .fetchOne();
        return itemName;
    }

    private List<QueryGeneralOrderDto.SelectOptionDto> getSelectOptionDtoList(OrderItem orderItem) {
        List<QueryGeneralOrderDto.SelectOptionDto> selectOptionDtoList = queryFactory
                .select(Projections.constructor(QueryGeneralOrderDto.SelectOptionDto.class,
                        selectOption.name,
                        selectOption.amount
                ))
                .from(selectOption)
                .where(selectOption.orderItem.eq(orderItem))
                .fetch();
        return selectOptionDtoList;
    }

    private String getThumbnailUrl(QueryGeneralOrdersDto.OrderDto orderDto) {
        List<String> filenameList = queryFactory
                .select(itemImage.filename)
                .from(orderItem)
                .join(orderItem.item, item)
                .join(itemImage).on(itemImage.item.eq(item))
                .join(orderItem.generalOrder, generalOrder)
                .where(generalOrder.id.eq(orderDto.getId()))
                .fetch();

        String filename = filenameList.get(0);

        String thumbnailUrl = linkTo(InfoController.class).slash("display/items?filename=" + filename).toString();
        return thumbnailUrl;
    }

    private QuerySubscribeOrderDto.OrderDto getOrderDto(Long id) {
        QuerySubscribeOrderDto.OrderDto orderDto = queryFactory
                .select(Projections.constructor(QuerySubscribeOrderDto.OrderDto.class,
                        subscribe.subscribeCount,
                        dog.name,
                        surveyReport.foodAnalysis.oneMealRecommendGram,
                        QSubscribe.subscribe.plan,
                        subscribeOrder.orderPrice,
                        beforeSubscribe.subscribeCount,
                        beforeSubscribe.plan,
                        beforeSubscribe.oneMealRecommendGram,
                        beforeSubscribe.recipeName,
                        beforeSubscribe.paymentPrice,
                        subscribeOrder.merchantUid,
                        Expressions.constant("subscribe"),
                        subscribeOrder.createdDate,
                        delivery.deliveryNumber,
                        delivery.status,
                        subscribeOrder.deliveryPrice,
                        subscribeOrder.discountTotal,
                        subscribeOrder.discountReward,
                        subscribeOrder.discountCoupon,
                        subscribeOrder.paymentPrice,
                        subscribeOrder.paymentMethod,
                        delivery.recipient.name,
                        delivery.recipient.phone,
                        delivery.recipient.zipcode,
                        delivery.recipient.street,
                        delivery.recipient.detailAddress,
                        delivery.request
                ))
                .from(subscribeOrder)
                .join(subscribeOrder.subscribe, QSubscribe.subscribe)
                .join(QSubscribe.subscribe.dog, dog)
                .join(dog.surveyReport, surveyReport)
                .join(subscribeOrder.delivery, delivery)
                .leftJoin(subscribe.beforeSubscribe, beforeSubscribe)
                .where(subscribeOrder.id.eq(id))
                .fetchOne();
        return orderDto;
    }

    private QuerySubscribeOrderDto.RecipeDto getRecipeDto(Long id) {
        List<QuerySubscribeOrderDto.RecipeDto> recipeDtoList = queryFactory
                .select(Projections.constructor(QuerySubscribeOrderDto.RecipeDto.class,
                        recipe.thumbnailImage.filename1,
                        recipe.name
                ))
                .from(subscribeRecipe)
                .join(subscribeRecipe.recipe, recipe)
                .join(subscribeRecipe.subscribe, subscribe)
                .join(subscribeOrder).on(subscribeOrder.subscribe.eq(subscribe))
                .where(subscribeOrder.id.eq(id))
                .fetch();

        QuerySubscribeOrderDto.RecipeDto recipeDto = recipeDtoList.get(0);
        recipeDto.changUrl(recipeDto.getThumbnailUrl());
        return recipeDto;
    }

    private String getRecipeNames(Long id) {
        List<String> recipeNameList = queryFactory
                .select(recipe.name)
                .from(subscribeRecipe)
                .join(subscribeRecipe.recipe, recipe)
                .join(subscribeRecipe.subscribe, subscribe)
                .join(subscribeOrder).on(subscribeOrder.subscribe.eq(subscribe))
                .where(subscribeOrder.id.eq(id))
                .fetch();

        String recipeName = recipeNameList.get(0);
        if (recipeNameList.size() > 1) {
            for (int i = 1; i < recipeNameList.size(); ++i) {
                recipeName += "," + recipeNameList.get(i);
            }
        }
        return recipeName;
    }

    private List<QuerySubscribeOrdersDto> getQuerySubscribeOrdersDtosByPaging(Member member, Pageable pageable) {
        List<QuerySubscribeOrdersDto.SubscribeOrderDto> subscribeOrderDtos = queryFactory
                .select(Projections.constructor(QuerySubscribeOrdersDto.SubscribeOrderDto.class,
                        order.id,
                        subscribe.id,
                        order.createdDate,
                        dog.name,
                        subscribeOrder.subscribeCount,
                        order.merchantUid,
                        order.paymentPrice,
                        order.orderStatus
                ))
                .from(order)
                .join(subscribeOrder).on(subscribeOrder.eq(order))
                .join(subscribeOrder.subscribe, subscribe)
                .join(subscribe.dog, dog)
                .where(order.member.eq(member))
                .orderBy(order.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<QuerySubscribeOrdersDto> result = new ArrayList<>();

        for (QuerySubscribeOrdersDto.SubscribeOrderDto subscribeOrderDto : subscribeOrderDtos) {
            Long subscribeId = subscribeOrderDto.getSubscribeId();

            List<QuerySubscribeOrdersDto.RecipeDto> recipeDtos = queryFactory
                    .select(Projections.constructor(QuerySubscribeOrdersDto.RecipeDto.class,
                            recipe.thumbnailImage.filename1,
                            recipe.name
                    ))
                    .from(subscribeRecipe)
                    .join(subscribeRecipe.recipe, recipe)
                    .join(subscribeRecipe.subscribe, subscribe)
                    .where(subscribe.id.eq(subscribeId))
                    .fetch();
            for (QuerySubscribeOrdersDto.RecipeDto recipeDto : recipeDtos) {
                recipeDto.changeUrl(recipeDto.getThumbnailUrl());
            }

            QuerySubscribeOrdersDto querySubscribeOrdersDto = QuerySubscribeOrdersDto.builder()
                    .recipeDto(recipeDtos.get(0))
                    .subscribeOrderDto(subscribeOrderDto)
                    .build();

            result.add(querySubscribeOrdersDto);
        }
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
                        subscribeOrder.discountCoupon,
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
                        beforeSubscribe.id,
                        beforeSubscribe.subscribeCount,
                        beforeSubscribe.plan,
                        beforeSubscribe.oneMealRecommendGram,
                        beforeSubscribe.recipeName
                ))
                .from(subscribe)
                .leftJoin(subscribe.beforeSubscribe, beforeSubscribe)
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
                        subscribeOrder.id,
                        subscribeOrder.merchantUid,
                        subscribeOrder.createdDate,
                        Expressions.constant("subscribe"),
                        subscribeOrder.isPackage,
                        member.name,
                        member.phoneNumber,
                        member.email,
                        member.isSubscribe,
                        subscribeOrder.orderCancel.cancelReason,
                        subscribeOrder.orderCancel.cancelDetailReason,
                        subscribeOrder.orderCancel.cancelRequestDate,
                        subscribeOrder.orderCancel.cancelConfirmDate
                ))
                .from(subscribeOrder)
                .join(subscribeOrder.member, member)
                .where(subscribeOrder.id.eq(id))
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
                        delivery.deliveryNumber,
                        order.member.email,
                        order.member.name,
                        order.member.phoneNumber,
                        order.delivery.recipient.name,
                        order.delivery.recipient.phone,
                        order.isPackage
                ))
                .from(order)
                .join(subscribeOrder).on(subscribeOrder.eq(order))
                .join(order.delivery, delivery)
                .where(
                        order.createdDate.between(from, to),
                        merchantUidEq(cond.getMerchantUid()),
                        memberNameEq(cond.getMemberName()),
                        memberEmailEq(cond.getMemberEmail()),
                        recipientNameEq(cond.getRecipientName()),
                        orderStatusIn(cond.getStatusList())
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
                        orderStatusIn(cond.getStatusList())
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
                        delivery.deliveryNumber,
                        generalOrder.member.email,
                        generalOrder.member.name,
                        generalOrder.member.phoneNumber,
                        generalOrder.delivery.recipient.name,
                        generalOrder.delivery.recipient.phone,
                        generalOrder.isPackage
                ))
                .from(orderItem)
                .join(orderItem.generalOrder, generalOrder)
                .join(generalOrder.delivery, delivery)
                .where(
                        generalOrder.createdDate.between(from, to),
                        orderItemMerchantUidEq(cond.getMerchantUid()),
                        orderItemMemberNameEq(cond.getMemberName()),
                        orderItemMemberEmail(cond.getMemberEmail()),
                        orderItemRecipientNameEq(cond.getRecipientName()),
                        orderItemStatusIn(cond.getStatusList())
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
                        orderItemStatusIn(cond.getStatusList())
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression orderItemStatusIn(List<OrderStatus> orderStatusList) {
        if (orderStatusList == null || orderStatusList.size() == 0) {
            return null;
        } else {
            return orderItem.status.in(orderStatusList);
        }
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

    private BooleanExpression orderStatusIn(List<OrderStatus> orderStatusList) {
        if (orderStatusList == null || orderStatusList.size() == 0) {
            return null;
        } else {
            return order.orderStatus.in(orderStatusList);
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
