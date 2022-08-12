package com.bi.barfdog.service;

import com.bi.barfdog.api.rewardDto.PublishToGroupDto;
import com.bi.barfdog.api.rewardDto.PublishToPersonalDto;
import com.bi.barfdog.api.rewardDto.RecommendFriendDto;
import com.bi.barfdog.directsend.CodeCouponAlimDto;
import com.bi.barfdog.directsend.DirectSend;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.directsend.RewardAlimDto;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.reward.*;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RewardService {

    private final RewardRepository rewardRepository;
    private final MemberRepository memberRepository;
    private final DogRepository dogRepository;

    @Transactional
    public void publishToPersonal(PublishToPersonalDto requestDto) {
        List<Long> memberIdList = requestDto.getMemberIdList();
        saveRewards(memberIdList, requestDto.getName(), requestDto.getAmount(), requestDto.isAlimTalk());
    }

    @Transactional
    public void publishToGroup(PublishToGroupDto requestDto) {
        List<Long> memberIdList = memberRepository.findMemberIdList(requestDto);
        saveRewards(memberIdList, requestDto.getName(), requestDto.getAmount(), requestDto.isAlimTalk());
    }

    @Transactional
    public void recommendFriend(Long memberId, RecommendFriendDto requestDto) {

        Member targetMember = memberRepository.findByMyRecommendationCode(requestDto.getRecommendCode()).get();

        Member member = memberRepository.findById(memberId).get();

        member.recommendFriend(requestDto.getRecommendCode());

        saveReward(member, targetMember);

    }

    private void saveReward(Member member, Member targetMember) {
        Reward reward = Reward.builder()
                .member(member)
                .name(RewardName.RECOMMEND + " (" + targetMember.getName() + ")")
                .rewardType(RewardType.RECOMMEND)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(RewardPoint.RECOMMEND)
                .build();
        rewardRepository.save(reward);
    }

    private void saveRewards(List<Long> memberIdList, String rewardName, int amount, boolean isAlimTalk) {

        List<RewardAlimDto> rewardAlimDtoList = new ArrayList<>();

        for (Long id : memberIdList) {
            Member member = memberRepository.findById(id).get();
            generateReward(rewardName, amount, member);

            member.saveReward(amount);

            String representativeDogName = getRepresentativeDogName(member);

            RewardAlimDto rewardAlimdto = RewardAlimDto.builder()
                    .name(member.getName())
                    .phone(member.getPhoneNumber())
                    .dogName(representativeDogName)
                    .rewardName(rewardName)
                    .amount(amount)
                    .build();

            rewardAlimDtoList.add(rewardAlimdto);
        }

        if (isAlimTalk) {
            try {
                DirectSendUtils.sendRewardPublishAlim(rewardAlimDtoList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    private void generateReward(String name, int amount, Member member) {
        Reward reward = Reward.builder()
                .member(member)
                .name(name)
                .rewardType(RewardType.ADMIN)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(amount)
                .build();
        rewardRepository.save(reward);
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


}
