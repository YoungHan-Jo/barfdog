package com.bi.barfdog.service;

import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.config.RewardPoint;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
                .myRecommendationCode(String.valueOf(UUID.randomUUID()))
                .grade(Grade.BRONZE)
                .rewardPoint(0)
                .roles("USER")
                .build();

        String recommendCode = requestDto.getRecommendCode();
        if (recommendCode != null) {
            member.setRecommendCode(recommendCode);
            member.chargePoint(RewardPoint.RECOMMEND_CODE);
        }

        if (member.getAgreement().isReceiveEmail() &&
                member.getAgreement().isReceiveSms()) {
            member.chargePoint(RewardPoint.RECEIVE_AGREEMENT);
        }


        return memberRepository.save(member);

    }
}
