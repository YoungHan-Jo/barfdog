package com.bi.barfdog.service;

import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.config.RewardPoint;
import com.bi.barfdog.directsend.DirectSend;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Transactional
    public Member join(MemberSaveRequestDto requestDto) {

        String password = bCryptPasswordEncoder.encode(requestDto.getPassword());

        Member member = Member.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(password)
                .phoneNumber(requestDto.getPhoneNumber())
                .address(requestDto.getAddress())
                .birthday(requestDto.getBirthday())
                .gender(requestDto.getGender())
                .agreement(requestDto.getAgreement())
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .grade(Grade.BRONZE)
                .rewardPoint(0)
                .roles("USER")
                .build();

        String recommendCode = requestDto.getRecommendCode();
        if (recommendCode != null) {
            Optional<Member> optionalMember = memberRepository.findByMyRecommendationCode(recommendCode);
            if (optionalMember.isPresent()) {
                member.setRecommendCode(recommendCode);
                member.chargePoint(RewardPoint.RECOMMEND_CODE);
            }
        }

        if (member.getAgreement().isReceiveEmail() &&
                member.getAgreement().isReceiveSms()) {
            member.chargePoint(RewardPoint.RECEIVE_AGREEMENT);
        }


        return memberRepository.save(member);

    }

    public String sendJoinPhoneAuth(String phoneNumber) throws IOException {

        String authNumber = "";

        for (int i = 0; i < 4; i++) {
            authNumber += Integer.toString(new Random().nextInt(10));
        }

        String title = "바프독 회원가입 - 인증";
        String message = "휴대폰 인증 번호는 " + "[" + authNumber + "] 입니다.";

        DirectSendUtils.sendSmsDirect(title, message, phoneNumber);

        return authNumber;
    }
}
