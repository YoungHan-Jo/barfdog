package com.bi.barfdog.service;

import com.bi.barfdog.api.couponDto.*;
import com.bi.barfdog.api.resource.CouponsDtoResource;
import com.bi.barfdog.directsend.CodeCouponAlimDto;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.api.couponDto.UpdateAutoCouponRequest.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final DogRepository dogRepository;
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

        LocalDateTime expiredDate = getExpiredDate(requestDto.getExpiredDate());

        coupon.publish(expiredDate);

        List<CodeCouponAlimDto> codeCouponAlimDtoList = new ArrayList<>();

        if (couponType == CouponType.CODE_PUBLISHED) { // 코드 발행 쿠폰
            for (Member member : memberList) {
                CodeCouponAlimDto codeCouponAlimDto = addMemberCouponInList(member, coupon, expiredDate, CouponStatus.INACTIVE, memberCouponList);
                codeCouponAlimDtoList.add(codeCouponAlimDto);
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
            if (couponType == CouponType.CODE_PUBLISHED) {
                DirectSendUtils.sendCodeCouponPublishAlim(codeCouponAlimDtoList);
            } else {
                DirectSendUtils.sendCouponAlim(memberCouponList);
            }
        }


    }

    @Transactional
    public void publishCouponsToGroup(GroupPublishRequestDto requestDto) throws IOException {
        List<MemberCoupon> memberCouponList = new ArrayList<>();
        Coupon coupon = couponRepository.findById(requestDto.getCouponId()).get();
        List<Member> memberList = memberRepository.findMembersByGroupCouponCond(requestDto);
        CouponType couponType = requestDto.getCouponType();

        LocalDateTime expiredDate = getExpiredDate(requestDto.getExpiredDate());

        coupon.publish(expiredDate);

        List<CodeCouponAlimDto> codeCouponAlimDtoList = new ArrayList<>();

        if (couponType == CouponType.CODE_PUBLISHED) { // 코드 발행 쿠폰
            for (Member member : memberList) {
                CodeCouponAlimDto codeCouponAlimDto = addMemberCouponInList(member, coupon, expiredDate, CouponStatus.INACTIVE, memberCouponList);

                codeCouponAlimDtoList.add(codeCouponAlimDto);
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
            if (couponType == CouponType.CODE_PUBLISHED) {
                DirectSendUtils.sendCodeCouponPublishAlim(codeCouponAlimDtoList);
            } else {
                DirectSendUtils.sendCouponAlim(memberCouponList);
            }
        }


    }

    @Transactional
    public void publishCouponsToAll(AllPublishRequestDto requestDto) throws IOException {
        List<MemberCoupon> memberCouponList = new ArrayList<>();
        Coupon coupon = couponRepository.findById(requestDto.getCouponId()).get();
        List<Member> memberList = memberRepository.findAll();
        CouponType couponType = requestDto.getCouponType();

        LocalDateTime expiredDate = getExpiredDate(requestDto.getExpiredDate());
        coupon.publish(expiredDate);

        List<CodeCouponAlimDto> codeCouponAlimDtoList = new ArrayList<>();

        if (couponType == CouponType.CODE_PUBLISHED) { // 코드 발행 쿠폰
            for (Member member : memberList) {
                CodeCouponAlimDto codeCouponAlimDto = addMemberCouponInList(member, coupon, expiredDate, CouponStatus.INACTIVE, memberCouponList);

                codeCouponAlimDtoList.add(codeCouponAlimDto);
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
            if (couponType == CouponType.CODE_PUBLISHED) {
                DirectSendUtils.sendCodeCouponPublishAlim(codeCouponAlimDtoList);
            } else {
                DirectSendUtils.sendCouponAlim(memberCouponList);
            }
        }
    }

    @Transactional
    public void updateAutoCoupons(UpdateAutoCouponRequest requestDto) {
        List<UpdateAutoCouponRequestDto> dtoList = requestDto.getUpdateAutoCouponRequestDtoList();
        for (UpdateAutoCouponRequestDto dto : dtoList) {
            Coupon coupon = couponRepository.findById(dto.getId()).get();
            coupon.updateAutoCoupon(dto);
        }
    }

    @Transactional
    public void getCodeCoupon(Member member, CodeCouponRequestDto requestDto) {
        String code = requestDto.getCode();
        List<MemberCoupon> memberCoupons = memberCouponRepository.findByMemberAndCode(member, code);
        for (MemberCoupon memberCoupon : memberCoupons) {
            memberCoupon.active();
        }
    }

    private LocalDateTime getExpiredDate(String requestDto) {
        String dateStr = requestDto;
        dateStr += " 23:59:59";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expiredDate = LocalDateTime.parse(dateStr, formatter);
        return expiredDate;
    }

    private String getRepresentativeDogName(Member member) {
        List<Dog> dogList = dogRepository.findRepresentativeDogByMember(member);
        String dogName = "";
        if (dogList.size() > 0) {
            dogName = dogList.get(0).getName();
        } else {
            dogName = "반려견";
        }
        return dogName;
    }

    private CodeCouponAlimDto addMemberCouponInList(Member member, Coupon coupon, LocalDateTime expiredDate, CouponStatus inactive, List<MemberCoupon> memberCouponList) {
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .expiredDate(expiredDate)
                .remaining(coupon.getAmount())
                .memberCouponStatus(inactive)
                .build();
        memberCouponList.add(memberCoupon);

        String dogName = getRepresentativeDogName(member);


        return CodeCouponAlimDto.builder()
                .name(member.getName())
                .phone(member.getPhoneNumber())
                .dogName(dogName)
                .couponName(coupon.getName())
                .code(coupon.getCode())
                .build();
    }


}
