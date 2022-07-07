package com.bi.barfdog.service;

import com.bi.barfdog.api.barfDto.HomePageDto;
import com.bi.barfdog.api.barfDto.SendInviteSmsDto;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.domain.reward.*;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.directsend.AuthResponseDto;
import com.bi.barfdog.domain.member.FirstReward;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.snsLogin.ConnectSnsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RewardRepository rewardRepository;
    private final DogRepository dogRepository;
    private final SubscribeRepository subscribeRepository;
    private final SubscribeRecipeRepository subscribeRecipeRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Transactional
    public JoinResponseDto join(MemberSaveRequestDto requestDto) {

        String password = bCryptPasswordEncoder.encode(requestDto.getPassword());

        Member member = Member.builder()
                .provider(requestDto.getProvider())
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
                .build();

        memberRepository.save(member);

        String recommendCode = requestDto.getRecommendCode();
        if (recommendCode != null) {
            Optional<Member> optionalMember = memberRepository.findByMyRecommendationCode(recommendCode);
            if (optionalMember.isPresent()) {

                Member findMember = optionalMember.get();
                findMember.chargePoint(RewardPoint.RECOMMEND);

                member.setRecommendCode(recommendCode);

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

//        if (member.getAgreement().isReceiveEmail() &&
//                member.getAgreement().isReceiveSms()) {
//            member.chargePoint(RewardPoint.RECEIVE_AGREEMENT);
//        }

        JoinResponseDto responseDto = JoinResponseDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .build();

        return responseDto;
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

        member.changePassword(hashPassword);

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
        member.updateGrade(requestDto.getGrade());
    }

    @Transactional
    public void deleteMember(Member member) {
        member.withdrawal();
    }

    @Transactional
    public void unconnectSns(Member member) {
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



}
