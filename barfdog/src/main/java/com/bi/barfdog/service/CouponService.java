package com.bi.barfdog.service;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.api.couponDto.PersonalPublishRequestDto;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.repository.CouponRepository;
import com.bi.barfdog.repository.MemberCouponRepository;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final EntityManager em;

    @Transactional
    public void createCoupon(CouponSaveRequestDto requestDto) {
        Coupon coupon = null;

        if (requestDto.getCode() == null || requestDto.getCode().length() == 0) { // 관리자 발행
            coupon = Coupon.builder()
                    .name(requestDto.getName())
                    .code("")
                    .couponType(CouponType.GENERAL_PUBLISHED)
                    .description(requestDto.getDescription())
                    .amount(requestDto.getAmount())
                    .discountType(requestDto.getDiscountType())
                    .discountDegree(requestDto.getDiscountDegree())
                    .availableMaxDiscount(requestDto.getAvailableMaxDiscount())
                    .availableMinPrice(requestDto.getAvailableMinPrice())
                    .couponTarget(requestDto.getCouponTarget())
                    .couponStatus(CouponStatus.ACTIVE)
                    .build();

        } else { // 코드 발행
            coupon = Coupon.builder()
                    .name(requestDto.getName())
                    .couponType(CouponType.CODE_PUBLISHED)
                    .code(requestDto.getCode())
                    .description(requestDto.getDescription())
                    .amount(requestDto.getAmount())
                    .discountType(requestDto.getDiscountType())
                    .discountDegree(requestDto.getDiscountDegree())
                    .availableMaxDiscount(requestDto.getAvailableMaxDiscount())
                    .availableMinPrice(requestDto.getAvailableMinPrice())
                    .couponTarget(requestDto.getCouponTarget())
                    .couponStatus(CouponStatus.ACTIVE)
                    .build();
        }

        couponRepository.save(coupon);
    }

    @Transactional
    public void inactiveCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id).get();

        coupon.inactive();
    }

    @Transactional
    public void publishCouponsToPersonal(PersonalPublishRequestDto requestDto) throws IOException {

        List<Long> memberIdList = requestDto.getMemberIdList();

        List<MemberCoupon> memberCouponList = new ArrayList<>();

        Coupon coupon = couponRepository.findById(requestDto.getCouponId()).get();

        List<Member> memberList = memberRepository.findByIdList(memberIdList);

        CouponType couponType = requestDto.getCouponType();

        if (couponType == CouponType.CODE_PUBLISHED) {
            for (Member member : memberList) {
                MemberCoupon memberCoupon = MemberCoupon.builder()
                        .member(member)
                        .coupon(coupon)
                        .expiredDate(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).plusDays(requestDto.getCouponLife()))
                        .remaining(coupon.getAmount())
                        .memberCouponStatus(CouponStatus.ACTIVE)
                        .build();

                memberCouponList.add(memberCoupon);
            }
        }

        memberCouponRepository.saveAll(memberCouponList);

        if (requestDto.isAlimTalk()) {
            DirectSendUtils.sendCouponAlim(memberCouponList);
        }


    }
}
