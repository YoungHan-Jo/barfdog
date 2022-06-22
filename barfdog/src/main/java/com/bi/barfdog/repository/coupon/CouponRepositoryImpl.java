package com.bi.barfdog.repository.coupon;

import com.bi.barfdog.api.couponDto.*;
import com.bi.barfdog.api.resource.CouponsDtoResource;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.member.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.coupon.QCoupon.*;
import static com.bi.barfdog.domain.memberCoupon.QMemberCoupon.*;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryImpl implements CouponRepositoryCustom{

    private final JPAQueryFactory queryFactory;



    @Override
    public List<CouponListResponseDto> findRedirectCouponsByKeyword(String keyword) {

        List<CouponListResponseDto> result = queryFactory
                .select(Projections.constructor(CouponListResponseDto.class,
                        coupon.id,
                        coupon.name,
                        coupon.code,
                        coupon.description,
                        coupon.discountDegree.stringValue().concat(
                                coupon.discountType
                                        .when(DiscountType.FLAT_RATE).then("원")
                                        .otherwise("%")
                        ),
                        coupon.couponTarget,
                        coupon.amount,
                        coupon.lastExpiredDate
                ))
                .from(coupon)
                .where(isDirectCoupon(keyword))
                .orderBy(coupon.id.desc())
                .fetch();

        return result;
    }

    @Override
    public List<CouponListResponseDto> findAutoCouponsByKeyword(String keyword) {
        List<CouponListResponseDto> result = queryFactory
                .select(Projections.constructor(CouponListResponseDto.class,
                        coupon.id,
                        coupon.name,
                        coupon.code,
                        coupon.description,
                        coupon.discountDegree.stringValue().concat(
                                coupon.discountType
                                        .when(DiscountType.FLAT_RATE).then("원")
                                        .otherwise("%")
                        ),
                        coupon.couponTarget,
                        coupon.amount,
                        coupon.lastExpiredDate
                ))
                .from(coupon)
                .where(isAutoCoupon(keyword))
                .orderBy(coupon.id.desc())
                .fetch();

        return result;
    }

    @Override
    public List<PublicationCouponDto> findPublicationCouponDtosByCouponType(CouponType couponType) {

        List<PublicationCouponDto> result = queryFactory
                .select(Projections.constructor(PublicationCouponDto.class,
                        coupon.id,
                        coupon.name,
                        coupon.discountDegree.stringValue().concat(
                                coupon.discountType
                                        .when(DiscountType.FLAT_RATE).then("원")
                                        .otherwise("%")
                        )
                ))
                .from(coupon)
                .where(statusEqActive().and(couponTypeEq(couponType)))
                .orderBy(coupon.id.desc())
                .fetch();

        return result;
    }

    @Override
    public List<AutoCouponsForUpdateDto> findAutoCouponDtosForUpdate() {
        return queryFactory
                .select(Projections.constructor(AutoCouponsForUpdateDto.class,
                        coupon.id,
                        coupon.name,
                        coupon.discountType,
                        coupon.discountDegree,
                        coupon.availableMinPrice
                ))
                .from(coupon)
                .orderBy(coupon.id.asc())
                .fetch();
    }

    @Override
    public Page<QueryCouponsDto> findCouponsDtoByMember(Member member, Pageable pageable) {
        List<QueryCouponsDto> result = queryFactory
                .select(Projections.constructor(QueryCouponsDto.class,
                        memberCoupon.id,
                        memberCoupon.memberCouponStatus,
                        coupon.name,
                        coupon.description,
                        memberCoupon.expiredDate,
                        coupon.discountDegree.stringValue().concat(
                                coupon.discountType
                                        .when(DiscountType.FLAT_RATE).then("원")
                                        .otherwise("%")
                        ),
                        memberCoupon.remaining
                ))
                .from(memberCoupon)
                .join(memberCoupon.coupon, coupon)
                .where(activeCouponByMember(member))
                .orderBy(memberCoupon.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(memberCoupon.count())
                .from(memberCoupon)
                .join(memberCoupon.coupon, coupon)
                .where(activeCouponByMember(member))
                .fetchOne();

        return new PageImpl<>(result,pageable,totalCount);
    }

    @Override
    public QueryCouponsPageDto findCouponsPage(Member member, Pageable pageable, PagedResourcesAssembler<QueryCouponsDto> assembler) {

        Page<QueryCouponsDto> page = findCouponsDtoByMember(member, pageable);
        PagedModel<CouponsDtoResource> pagedModel = assembler.toModel(page, e -> new CouponsDtoResource(e));

        Long totalCount = queryFactory
                .select(memberCoupon.count())
                .from(memberCoupon)
                .where(activeCouponByMember(member))
                .fetchOne();

        Long availableCount = queryFactory
                .select(memberCoupon.count())
                .from(memberCoupon)
                .join(memberCoupon.coupon, coupon)
                .where(availableCouponsByMember(member))
                .fetchOne();

        QueryCouponsPageDto result = QueryCouponsPageDto.builder()
                .totalCount(totalCount)
                .availableCount(availableCount)
                .couponsPageDto(pagedModel)
                .build();

        return result;
    }

    private BooleanExpression availableCouponsByMember(Member member) {
        return activeCouponByMember(member).and(memberCoupon.memberCouponStatus.eq(CouponStatus.ACTIVE));
    }

    private BooleanExpression activeCouponByMember(Member member) {
        return memberCoupon.member.eq(member).and(coupon.couponStatus.eq(CouponStatus.ACTIVE));
    }


    private BooleanExpression couponTypeEq(CouponType couponType) {
        return coupon.couponType.eq(couponType);
    }


    private BooleanExpression isDirectCoupon(String keyword) {
        return statusEqActive().and(couponTypeNeAutoPublished().and(nameLike(keyword)));
    }

    private BooleanExpression isAutoCoupon(String keyword) {
        return statusEqActive().and(couponTypeEqAutoPublished().and(nameLike(keyword)));
    }

    private BooleanExpression couponTypeEqAutoPublished() {
        return coupon.couponType.eq(CouponType.AUTO_PUBLISHED);
    }

    private BooleanExpression couponTypeNeAutoPublished() {
        return coupon.couponType.ne(CouponType.AUTO_PUBLISHED);
    }

    private BooleanExpression statusEqActive() {
        return coupon.couponStatus.eq(CouponStatus.ACTIVE);
    }

    private BooleanExpression nameLike(String keyword) {
        return keyword != null && keyword.length() > 0 ? coupon.name.contains(keyword) : null;
    }
}
