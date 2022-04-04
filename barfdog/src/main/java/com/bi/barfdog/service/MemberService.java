package com.bi.barfdog.service;

import com.bi.barfdog.api.memberDto.FindPasswordRequestDto;
import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.config.RewardPoint;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.directsend.PhoneAuthResponseDto;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

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

        System.out.println("temporaryPassword = " + rawPassword);

        member.temporaryPassword(hashPassword);

        return responseDto;
    }









    private DirectSendResponseDto sendSmsAndGetPhoneAuthResponseDto(String phoneNumber) throws IOException {

        String authNumber = BarfUtils.generate4Number();

        String title = "바프독 본인 인증";
        String message = "휴대폰 인증 번호는 " + "[" + authNumber + "] 입니다.";

        PhoneAuthResponseDto responseDto = (PhoneAuthResponseDto) DirectSendUtils.sendSmsDirect(title, message, phoneNumber);
        responseDto.setAuthNumber(authNumber);
        return responseDto;
    }
}
