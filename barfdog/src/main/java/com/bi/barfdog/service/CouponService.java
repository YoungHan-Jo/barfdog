package com.bi.barfdog.service;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.api.couponDto.GroupPublishRequestDto;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        String dateStr = requestDto.getExpiredDate();
        dateStr += " 23:59:59";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expiredDate = LocalDateTime.parse(dateStr, formatter);

        coupon.publish(expiredDate);

        if (couponType == CouponType.CODE_PUBLISHED) { // 코드 발행 쿠폰
            for (Member member : memberList) {
                addMemberCouponInList(member, coupon, expiredDate, CouponStatus.INACTIVE, memberCouponList);
            }
        } else if (couponType == CouponType.GENERAL_PUBLISHED) { // 일반 발행 쿠폰
            for (Member member : memberList) {
                addMemberCouponInList(member, coupon, expiredDate, CouponStatus.ACTIVE, memberCouponList);
            }
        }

        memberCouponRepository.saveAll(memberCouponList);

        em.flush(); // 벌크 쿼리라서 플러시 해줘야함
        em.clear();


        // 알림톡 보내기
        if (requestDto.isAlimTalk()) {
            DirectSendUtils.sendCouponAlim(memberCouponList);
        }


    }

    @Transactional
    public void publishCouponsToGroup(GroupPublishRequestDto requestDto) throws IOException {
        List<MemberCoupon> memberCouponList = new ArrayList<>();
        Coupon coupon = couponRepository.findById(requestDto.getCouponId()).get();
        List<Member> memberList = memberRepository.findMembersByGroupCouponCond(requestDto);
        CouponType couponType = requestDto.getCouponType();

        String dateStr = requestDto.getExpiredDate();
        dateStr += " 23:59:59";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expiredDate = LocalDateTime.parse(dateStr, formatter);

        coupon.publish(expiredDate);

        if (couponType == CouponType.CODE_PUBLISHED) { // 코드 발행 쿠폰
            for (Member member : memberList) {
                addMemberCouponInList(member, coupon, expiredDate, CouponStatus.INACTIVE, memberCouponList);
            }
        } else if (couponType == CouponType.GENERAL_PUBLISHED) { // 일반 발행 쿠폰
            for (Member member : memberList) {
                addMemberCouponInList(member, coupon, expiredDate, CouponStatus.ACTIVE, memberCouponList);
            }
        }

        memberCouponRepository.saveAll(memberCouponList);

        em.flush(); // 벌크 쿼리라서 플러시 해줘야함
        em.clear();

        // 알림톡 보내기
        if (requestDto.isAlimTalk()) {
            DirectSendUtils.sendCouponAlim(memberCouponList);
        }

    }

    private void addMemberCouponInList(Member member, Coupon coupon, LocalDateTime expiredDate, CouponStatus inactive, List<MemberCoupon> memberCouponList) {
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .expiredDate(expiredDate)
                .remaining(coupon.getAmount())
                .memberCouponStatus(inactive)
                .build();

        memberCouponList.add(memberCoupon);
    }
}
