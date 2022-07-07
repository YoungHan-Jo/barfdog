package com.bi.barfdog.repository.coupon;

import com.bi.barfdog.api.couponDto.*;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.List;

public interface CouponRepositoryCustom {

    Page<CouponListResponseDto> findRedirectCouponsByKeyword(String keyword, Pageable pageable);

    Page<CouponListResponseDto> findAutoCouponsByKeyword(String keyword, Pageable pageable);

    List<PublicationCouponDto> findPublicationCouponDtosByCouponType(CouponType adminPublished);

    List<AutoCouponsForUpdateDto> findAutoCouponDtosForUpdate();

    Page<QueryCouponsDto> findCouponsDtoByMember(Member member, Pageable pageable);

    QueryCouponsPageDto findCouponsPage(Member member, Pageable pageable, PagedResourcesAssembler<QueryCouponsDto> assembler);
}
