package com.bi.barfdog.service;

import com.bi.barfdog.api.barfDto.SendInviteSmsDto;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.config.finalVariable.AutoCoupon;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.reward.*;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.directsend.AuthResponseDto;
import com.bi.barfdog.domain.member.FirstReward;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.snsLogin.ConnectSnsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회만 사용하는 서비스에서 좀 더 속도가 효율적으로 나옴
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RewardRepository rewardRepository;
    private final DogRepository dogRepository;
    private final SubscribeRepository subscribeRepository;
    private final SubscribeRecipeRepository subscribeRecipeRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Transactional // 조회 서비스가 아닌 수정 서비스라면 @Transactional 붙여 줘야함 readonly = false 가 디폴트
    public JoinResponseDto join(MemberSaveRequestDto requestDto) {

        Member member = saveMember(requestDto);
        recommendFriend(requestDto, member);
        generateJoinSubscribeCoupon(member);

        JoinResponseDto responseDto = JoinResponseDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .build();
        return responseDto;
    }

    private void generateJoinSubscribeCoupon(Member member) {
        Optional<Coupon> optionalCoupon = couponRepository.findByName(AutoCoupon.JOIN_SUBSCRIBE_COUPON);
        if (optionalCoupon.isPresent()) {
            Coupon coupon = optionalCoupon.get();

            MemberCoupon memberCoupon = MemberCoupon.builder()
                    .member(member)
                    .coupon(coupon)
                    .expiredDate(LocalDateTime.now().plusDays(AutoCoupon.JOIN_SUBSCRIBE_COUPON_DAY))
                    .remaining(1)
                    .memberCouponStatus(CouponStatus.ACTIVE)
                    .build();
            memberCouponRepository.save(memberCoupon);
        }
    }

    private void recommendFriend(MemberSaveRequestDto requestDto, Member member) {
        String recommendCode = requestDto.getRecommendCode();
        if (recommendCode != null) {
            Optional<Member> optionalMember = memberRepository.findByMyRecommendationCode(recommendCode);
            if (optionalMember.isPresent()) {

                Member findMember = optionalMember.get();
                findMember.chargeReward(RewardPoint.RECOMMEND);

                member.recommendFriend(recommendCode);

                Reward reward = Reward.builder()
                        .member(member)
                        .rewardType(RewardType.RECOMMEND)
                        .rewardStatus(RewardStatus.SAVED)
                        .tradeReward(RewardPoint.RECOMMEND)
                        .name(RewardName.RECOMMEND + " ("+findMember.getName()+")")
                        .build();
                rewardRepository.save(reward);
            }
        }
    }

    private Member saveMember(MemberSaveRequestDto requestDto) {
        String rawPassword = requestDto.getPassword();
        String password = rawPassword == null || rawPassword.isEmpty() ? null : bCryptPasswordEncoder.encode(rawPassword);
        String provider = requestDto.getProvider();
        Member member = Member.builder()
                .provider(provider)
                .providerId(requestDto.getProviderId())
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(password)
                .phoneNumber(requestDto.getPhoneNumber())
                .address(requestDto.getAddress())
                .birthday(requestDto.getBirthday())
                .gender(requestDto.getGender())
                .agreement(requestDto.getAgreement())
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .grade(Grade.브론즈)
                .reward(0)
                .accumulatedAmount(0)
                .firstReward(new FirstReward(false,false))
                .roles("USER")
                .needToSetPassword(isUnKnownPassword(provider))
                .build();
        memberRepository.save(member);
        return member;
    }

    private boolean isUnKnownPassword(String provider) {
        if (provider == null) {
            return false;
        }
        if (provider.isEmpty()) {
            return false;
        }
        return true;
    }

    public DirectSendResponseDto sendPhoneAuth(String phoneNumber) throws IOException {

        DirectSendResponseDto responseDto = sendSmsAndGetPhoneAuthResponseDto(phoneNumber);

        return responseDto;
    }

    @Transactional
    public DirectSendResponseDto temporaryPassword(FindPasswordRequestDto requestDto) throws IOException {

        Member member = memberRepository.findByEmailAndNameAndPhoneNumber(requestDto.getEmail(), requestDto.getName(), requestDto.getPhoneNumber()).get();

        String rawPassword = "Barf" + BarfUtils.generate4Number();
        String hashPassword = bCryptPasswordEncoder.encode(rawPassword);

        String title = "바프독 임시 비밀번호";
        String message = "임시 비밀번호는 " + "[" + rawPassword + "] 입니다.";

        DirectSendResponseDto responseDto = DirectSendUtils.sendSmsDirect(title, message, member.getPhoneNumber());

        member.temporaryPassword(hashPassword);

        return responseDto;
    }

    public DirectSendResponseDto sendAdminPasswordEmailAuth(EmailAuthDto requestDto) throws Exception {

        String authNumber = BarfUtils.generate4Number();

        String title = "바프독 이메일 인증";
        String contents = "인증 번호는 " + "[" + authNumber + "] 입니다.";

        DirectSendResponseDto directSendResponseDto = DirectSendUtils.sendEmailDirect(title, contents, requestDto.getEmail());

        DirectSendResponseDto responseDto = new AuthResponseDto(directSendResponseDto.getResponseCode(),
                directSendResponseDto.getStatus(), directSendResponseDto.getMsg(), authNumber);

        return responseDto;
    }

    @Transactional
    public void updateAdminPassword(UpdateAdminPasswordRequestDto requestDto) {
        Member findMember = memberRepository.findByEmail(requestDto.getEmail()).get();
        String hashPassword = bCryptPasswordEncoder.encode(requestDto.getPassword());
        findMember.changePassword(hashPassword);
    }

    @Transactional
    public void updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
        Member findMember = memberRepository.findById(memberId).get();

        String hashPassword = bCryptPasswordEncoder.encode(requestDto.getPassword());
        requestDto.setPassword(hashPassword);

        System.out.println("isReceiveEmail : " + requestDto.isReceiveEmail());
        System.out.println("isReceiveSms : " + requestDto.isReceiveSms());

        findMember.updateMember(requestDto);

    }

    @Transactional
    public void login(Member member) {
        member.login();
    }

    private DirectSendResponseDto sendSmsAndGetPhoneAuthResponseDto(String phoneNumber) throws IOException {

        String authNumber = BarfUtils.generate4Number();

        String title = "바프독 본인 인증";
        String message = "휴대폰 인증 번호는 " + "[" + authNumber + "] 입니다.";

        DirectSendResponseDto directSendResponseDto = DirectSendUtils.sendSmsDirect(title, message, phoneNumber);

        DirectSendResponseDto responseDto = new AuthResponseDto(directSendResponseDto.getResponseCode(),
                directSendResponseDto.getStatus(), directSendResponseDto.getMsg(), authNumber);

        return responseDto;
    }

    public DirectSendResponseDto sendInviteSms(Member member, SendInviteSmsDto requestDto) throws IOException {

        String title = "[바프독] 친구 초대";
        String message = "[바프독]\n" +
                member.getName() +"님이 " + requestDto.getName() + "님에게 바프독 적립금을 선물했습니다.\n" +
                "가입 후 마이페이지에서 추천코드를 입력해 주세요!\n" +
                "추천코드 : " + member.getMyRecommendationCode() + "\n" +
                "가입하러 가기 : " + requestDto.getHomePageUrl();

        return DirectSendUtils.sendSmsDirect(title, message, requestDto.getPhone());
    }

    public QueryMemberAndDogsDto getMemberDto(Long id) {

        QueryMemberDto memberDto = memberRepository.findMemberDto(id).get();
        List<String> dogNames = dogRepository.findDogNamesByMemberId(id);

        QueryMemberAndDogsDto responseDto = QueryMemberAndDogsDto.builder()
                .memberDto(memberDto)
                .dogNames(dogNames)
                .build();

        return responseDto;
    }

    @Transactional
    public void updateBirthday(Long id, UpdateBirthdayRequestDto requestDto) {
        Member member = memberRepository.findById(id).get();
        member.updateBirthday(requestDto.getBirthday());
    }

    @Transactional
    public void updateGrade(Long id, UpdateGradeRequestDto requestDto) {
        Member member = memberRepository.findById(id).get();
        member.changeGrade(requestDto.getGrade());
    }

    @Transactional
    public void deleteMember(Long memberId) {

        Member member = memberRepository.findById(memberId).get();
        member.withdrawal();
    }

    @Transactional
    public void unconnectSns(Long memberId) {
        Member member = memberRepository.findById(memberId).get();
        member.unconnectSns();
    }

    @Transactional
    public ConnectSnsResponseDto connectSns(ConnectSnsRequestDto requestDto) {
        Member member = memberRepository.findByPhoneNumber(requestDto.getPhoneNumber()).get();
        member.connectSns(requestDto.getProvider(), requestDto.getProviderId());

        return ConnectSnsResponseDto.builder()
                .email(member.getEmail())
                .provider(member.getProvider())
                .build();
    }


    @Transactional
    public void gradeScheduler() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            Grade beforeGrade = member.getGrade();
            int subscribeCount = member.getAccumulatedSubscribe();
            int amount = member.getAccumulatedAmount();

            if (subscribeCount >= 1 || amount >= 90000) member.changeGrade(Grade.실버);
            if (subscribeCount >= 5 || amount >= 450000) member.changeGrade(Grade.골드);
            if (subscribeCount >= 10 || amount >= 900000) member.changeGrade(Grade.플래티넘);
            if (subscribeCount >= 15 || amount >= 1350000) member.changeGrade(Grade.다이아몬드);
            if (subscribeCount >= 25 || amount >= 2250000) member.changeGrade(Grade.더바프);

            Grade newGrade = member.getGrade();
            if (beforeGrade != newGrade) {
                try {
                    DirectSendUtils.sendGradeAlim(member, beforeGrade, newGrade);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // TODO: 2022-07-25 등급별 쿠폰 발급

            LocalDateTime expiredDate = getExpiredDate();

            if (newGrade == Grade.실버) {
                publishCoupon(member, expiredDate, AutoCoupon.SILVER_COUPON);
            }
            if (newGrade == Grade.골드) {
                publishCoupon(member, expiredDate, AutoCoupon.SILVER_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.GOLD_COUPON);
            }
            if (newGrade == Grade.플래티넘) {
                publishCoupon(member, expiredDate, AutoCoupon.SILVER_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.GOLD_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.PLATINUM_COUPON);
            }
            if (newGrade == Grade.다이아몬드) {
                publishCoupon(member, expiredDate, AutoCoupon.SILVER_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.GOLD_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.PLATINUM_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.DIAMOND_COUPON);
            }
            if (newGrade == Grade.더바프) {
                publishCoupon(member, expiredDate, AutoCoupon.SILVER_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.GOLD_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.PLATINUM_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.DIAMOND_COUPON);
                publishCoupon(member, expiredDate, AutoCoupon.BARF_COUPON);
            }

        }
    }

    private void publishCoupon(Member member, LocalDateTime expiredDate, String couponName) {
        Coupon coupon = couponRepository.findByName(couponName).get();
        saveMemberCoupon(member, expiredDate, coupon);
    }

    private void saveMemberCoupon(Member member, LocalDateTime expiredDate, Coupon coupon) {
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .expiredDate(expiredDate)
                .remaining(1)
                .memberCouponStatus(CouponStatus.ACTIVE)
                .build();
        memberCouponRepository.save(memberCoupon);
    }

    private LocalDateTime getExpiredDate() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDateTime expiredDate = lastDayOfMonth.atTime(23, 59, 59);
        return expiredDate;
    }

    @Transactional
    public void updatePassword(Long memberId, String newPassword) {
        Member member = memberRepository.findById(memberId).get();
        String hashPassword = bCryptPasswordEncoder.encode(newPassword);
        member.changePassword(hashPassword);
    }

    @Transactional
    public void setPasswordSnsMember(Long id, SnsLoginSetPasswordDto requestDto) {

        Member member = memberRepository.findById(id).get();
        String hashPassword = bCryptPasswordEncoder.encode(requestDto.getPassword());
        member.changeSnsPassword(hashPassword);

    }

    public IsNeedToSetPasswordDto checkNeedToSetPassword(Long id) {
        Member member = memberRepository.findById(id).get();

        boolean isNeedToSetPassword = member.isNeedToSetPassword();

        IsNeedToSetPasswordDto responseDto = IsNeedToSetPasswordDto.builder()
                .isNeedToSetPassword(isNeedToSetPassword)
                .build();

        return responseDto;
    }
}
