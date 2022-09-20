package com.bi.barfdog.repository.subscribe;

import com.bi.barfdog.api.memberDto.MemberSubscribeAdminDto;
import com.bi.barfdog.api.memberDto.QuerySubscribeAdminDto;
import com.bi.barfdog.api.orderDto.OrderSheetSubscribeResponseDto;
import com.bi.barfdog.api.subscribeDto.QuerySubscribeDto;
import com.bi.barfdog.api.subscribeDto.QuerySubscribesDto;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.coupon.CouponTarget;
import com.bi.barfdog.domain.coupon.QCoupon;
import com.bi.barfdog.domain.dog.QDogPicture;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.QMemberCoupon;
import com.bi.barfdog.domain.recipe.QRecipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.querydsl.core.types.Projections;
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

import static com.bi.barfdog.domain.coupon.QCoupon.*;
import static com.bi.barfdog.domain.dog.QDog.dog;
import static com.bi.barfdog.domain.dog.QDogPicture.*;
import static com.bi.barfdog.domain.member.QMember.member;
import static com.bi.barfdog.domain.memberCoupon.QMemberCoupon.*;
import static com.bi.barfdog.domain.recipe.QRecipe.*;
import static com.bi.barfdog.domain.subscribe.QSubscribe.subscribe;
import static com.bi.barfdog.domain.subscribeRecipe.QSubscribeRecipe.*;
import static com.bi.barfdog.domain.surveyReport.QSurveyReport.surveyReport;

@RequiredArgsConstructor
@Repository
public class SubscribeRepositoryImpl implements SubscribeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    private final SubscribeRecipeRepository subscribeRecipeRepository;


    @Override
    public Page<MemberSubscribeAdminDto> findSubscribeAdminDtoByMemberId(Long id, Pageable pageable) {
        List<MemberSubscribeAdminDto> result = new ArrayList<>();

        List<QuerySubscribeAdminDto> querySubscribeAdminDtos = queryFactory
                .select(Projections.constructor(QuerySubscribeAdminDto.class,
                        subscribe.id,
                        dog.name,
                        subscribe.createdDate,
                        subscribe.subscribeCount,
                        subscribe.plan,
                        surveyReport.foodAnalysis.oneMealRecommendGram,
                        subscribe.nextPaymentPrice,
                        subscribe.nextDeliveryDate,
                        dog.inedibleFood,
                        dog.inedibleFoodEtc,
                        dog.caution
                ))
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .join(dog.surveyReport, surveyReport)
                .where(member.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(subscribe.nextDeliveryDate.desc())
                .fetch();

        for (QuerySubscribeAdminDto querySubscribeAdminDto : querySubscribeAdminDtos) {

            List<String> recipeNames = subscribeRecipeRepository.findRecipeNamesBySubscribeId(querySubscribeAdminDto.getId());

            MemberSubscribeAdminDto memberSubscribeAdminDto = MemberSubscribeAdminDto.builder()
                    .querySubscribeAdminDto(querySubscribeAdminDto)
                    .recipeNames(recipeNames)
                    .build();

            result.add(memberSubscribeAdminDto);
        }

        Long totalCount = queryFactory
                .select(subscribe.count())
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .join(dog.surveyReport, surveyReport)
                .where(member.id.eq(id))
                .fetchOne();

        return new PageImpl<>(result,pageable,totalCount);
    }

    @Override
    public List<Subscribe> findWriteableByMember(Member member) {
        return queryFactory
                .select(subscribe)
                .from(subscribe)
                .where(subscribe.dog.member.eq(member).and(subscribe.writeableReview.isTrue()))
                .fetch()
                ;
    }

    @Override
    public OrderSheetSubscribeResponseDto.SubscribeDto findOrderSheetSubscribeDtoById(Long subscribeId) {
        return queryFactory
                .select(Projections.constructor(OrderSheetSubscribeResponseDto.SubscribeDto.class,
                        subscribe.id,
                        subscribe.plan,
                        subscribe.nextPaymentPrice,
                        subscribe.discountGrade
                ))
                .from(subscribe)
                .where(subscribe.id.eq(subscribeId))
                .fetchOne()
                ;
    }

    @Override
    public List<String> findRecipeNamesById(Long subscribeId) {
        return queryFactory
                .select(recipe.name)
                .from(subscribeRecipe)
                .join(subscribeRecipe.recipe, recipe)
                .where(subscribeRecipe.subscribe.id.eq(subscribeId))
                .fetch()
                ;

    }

    @Override
    public List<Subscribe> findAllByMember(Member user) {
        return queryFactory
                .select(subscribe)
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .where(member.eq(user))
                .orderBy(subscribe.nextDeliveryDate.asc())
                .fetch();
    }

    @Override
    public Page<QuerySubscribesDto> findSubscribesDto(Member member, Pageable pageable) {

        List<QuerySubscribesDto> result = new ArrayList<>();

        List<QuerySubscribesDto.SubscribeDto> subscribeDtoList = queryFactory
                .select(Projections.constructor(QuerySubscribesDto.SubscribeDto.class,
                        subscribe.id,
                        dogPicture.filename,
                        subscribe.status,
                        subscribe.plan,
                        dog.name,
                        subscribe.countSkipOneTime,
                        subscribe.countSkipOneWeek,
                        subscribe.nextPaymentDate,
                        subscribe.nextPaymentPrice,
                        subscribe.discountCoupon,
                        subscribe.discountGrade
                ))
                .from(subscribe)
                .join(subscribe.dog, dog)
                .leftJoin(dogPicture).on(dogPicture.dog.eq(dog))
                .where(dog.member.eq(member))
                .fetch();
        for (QuerySubscribesDto.SubscribeDto subscribeDto : subscribeDtoList) {
            subscribeDto.changeUrl(subscribeDto.getPictureUrl());

            String recipeNames = getRecipeNames(subscribeDto);

            QuerySubscribesDto dto = QuerySubscribesDto.builder()
                    .subscribeDto(subscribeDto)
                    .recipeNames(recipeNames)
                    .build();
            result.add(dto);
        }

        Long totalCount = queryFactory
                .select(subscribe.count())
                .from(subscribe)
                .join(subscribe.dog, dog)
                .where(dog.member.eq(member))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public QuerySubscribeDto findSubscribeDto(Member user, Long id) {

        QuerySubscribeDto.SubscribeDto subscribeDto = getSubscribeDto(id);
        List<QuerySubscribeDto.SubscribeRecipeDto> subscribeRecipeDtoList = getSubscribeRecipeDtoList(id);
        List<QuerySubscribeDto.MemberCouponDto> memberCouponDtoList = getMemberCouponDtos(user);
        List<QuerySubscribeDto.RecipeDto> recipeDtoList = getRecipeDtoList();

        QuerySubscribeDto responseEntity = QuerySubscribeDto.builder()
                .subscribeDto(subscribeDto)
                .subscribeRecipeDtoList(subscribeRecipeDtoList)
                .memberCouponDtoList(memberCouponDtoList)
                .recipeDtoList(recipeDtoList)
                .build();

        return responseEntity;
    }

    @Override
    public List<Subscribe> findTomorrowPayment() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime from = tomorrow.atStartOfDay();
        LocalDateTime to = tomorrow.atTime(23, 59, 59);
        return queryFactory
                .selectFrom(subscribe)
                .where(subscribe.nextPaymentDate.between(from,to))
                .fetch()
                ;
    }

    @Override
    public Long findSubscribingCountByMember(Member user) {
        return queryFactory
                .select(subscribe.count())
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .where(member.eq(user).and(subscribe.status.eq(SubscribeStatus.SUBSCRIBING)))
                .fetchOne()
                ;
    }

    private QuerySubscribeDto.SubscribeDto getSubscribeDto(Long id) {
        QuerySubscribeDto.SubscribeDto subscribeDto = queryFactory
                .select(Projections.constructor(QuerySubscribeDto.SubscribeDto.class,
                        subscribe.id,
                        dog.id,
                        dog.name,
                        subscribe.subscribeCount,
                        subscribe.plan,
                        subscribe.oneMealRecommendGram,
                        subscribe.nextPaymentDate,
                        subscribe.countSkipOneTime,
                        subscribe.countSkipOneWeek,
                        subscribe.nextPaymentPrice,
                        subscribe.discountCoupon,
                        subscribe.discountGrade,
                        subscribe.nextDeliveryDate,
                        memberCoupon.id,
                        coupon.name
                ))
                .from(subscribe)
                .join(subscribe.dog, dog)
                .leftJoin(subscribe.memberCoupon, memberCoupon)
                .leftJoin(memberCoupon.coupon, coupon)
                .where(subscribe.id.eq(id))
                .fetchOne();
        return subscribeDto;
    }

    private List<QuerySubscribeDto.SubscribeRecipeDto> getSubscribeRecipeDtoList(Long id) {
        List<QuerySubscribeDto.SubscribeRecipeDto> subscribeRecipeDtoList = queryFactory
                .select(Projections.constructor(QuerySubscribeDto.SubscribeRecipeDto.class,
                        recipe.id,
                        recipe.name
                ))
                .from(subscribeRecipe)
                .join(subscribeRecipe.subscribe, subscribe)
                .join(subscribeRecipe.recipe, recipe)
                .where(subscribe.id.eq(id))
                .fetch();
        return subscribeRecipeDtoList;
    }

    private List<QuerySubscribeDto.MemberCouponDto> getMemberCouponDtos(Member user) {
        List<QuerySubscribeDto.MemberCouponDto> memberCouponDtoList = queryFactory
                .select(Projections.constructor(QuerySubscribeDto.MemberCouponDto.class,
                        memberCoupon.id,
                        coupon.name,
                        coupon.discountType,
                        coupon.discountDegree,
                        coupon.availableMaxDiscount,
                        coupon.availableMinPrice,
                        memberCoupon.remaining,
                        memberCoupon.expiredDate
                ))
                .from(memberCoupon)
                .join(memberCoupon.coupon, coupon)
                .join(memberCoupon.member, member)
                .where(member.eq(user)
                        .and(memberCoupon.memberCouponStatus.eq(CouponStatus.ACTIVE)
                                .and(coupon.couponTarget.in(CouponTarget.ALL, CouponTarget.SUBSCRIBE)
                                        .and(memberCoupon.expiredDate.after(LocalDateTime.now())))))
                .fetch();
        return memberCouponDtoList;
    }

    private List<QuerySubscribeDto.RecipeDto> getRecipeDtoList() {
        List<QuerySubscribeDto.RecipeDto> recipeDtoList = queryFactory
                .select(Projections.constructor(QuerySubscribeDto.RecipeDto.class,
                        recipe.id,
                        recipe.name,
                        recipe.description,
                        recipe.pricePerGram,
                        recipe.gramPerKcal,
                        recipe.inStock,
                        recipe.thumbnailImage.filename2
                ))
                .from(recipe)
                .where(recipe.status.eq(RecipeStatus.ACTIVE))
                .fetch();

        for (QuerySubscribeDto.RecipeDto recipeDto : recipeDtoList) {
            recipeDto.changeUrl(recipeDto.getImgUrl());
        }
        return recipeDtoList;
    }

    private String getRecipeNames(QuerySubscribesDto.SubscribeDto subscribeDto) {
        String recipeNames = "";
        List<String> nameList = queryFactory
                .select(recipe.name)
                .from(subscribeRecipe)
                .join(subscribeRecipe.subscribe, subscribe)
                .join(subscribeRecipe.recipe, recipe)
                .where(subscribe.id.eq(subscribeDto.getSubscribeId()))
                .fetch();
        if (nameList.size() > 0) {
            recipeNames = nameList.get(0);
        }
        if (nameList.size() > 1) {
            recipeNames += "," + nameList.get(1);
        }
        return recipeNames;
    }
}
