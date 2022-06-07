package com.bi.barfdog.service;

import com.bi.barfdog.api.rewardDto.PublishToGroupDto;
import com.bi.barfdog.api.rewardDto.PublishToPersonalDto;
import com.bi.barfdog.api.rewardDto.RecommendFriendDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.reward.*;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RewardService {

    private final RewardRepository rewardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void publishToPersonal(PublishToPersonalDto requestDto) {
        List<Long> memberIdList = requestDto.getMemberIdList();
        saveRewards(memberIdList, requestDto.getName(), requestDto.getAmount());
    }

    @Transactional
    public void publishToGroup(PublishToGroupDto requestDto) {
        List<Long> memberIdList = memberRepository.findMemberIdList(requestDto);
        saveRewards(memberIdList, requestDto.getName(), requestDto.getAmount());
    }

    @Transactional
    public void recommendFriend(Member member, RecommendFriendDto requestDto) {
        Member targetMember = memberRepository.findByMyRecommendationCode(requestDto.getRecommendCode()).get();
        member.setRecommendCode(requestDto.getRecommendCode());
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

    private void saveRewards(List<Long> memberIdList, String name, int amount) {
        for (Long id : memberIdList) {
            Member member = memberRepository.findById(id).get();
            Reward reward = Reward.builder()
                    .member(member)
                    .name(name)
                    .rewardType(RewardType.ADMIN)
                    .rewardStatus(RewardStatus.SAVED)
                    .tradeReward(amount)
                    .build();
            rewardRepository.save(reward);
        }
    }


}
