package com.bi.barfdog.repository.member;

import com.bi.barfdog.api.barfDto.FriendTalkGroupDto;
import com.bi.barfdog.api.barfDto.MypageDto;
import com.bi.barfdog.api.couponDto.GroupPublishRequestDto;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.api.rewardDto.PublishToGroupDto;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    List<MemberPublishResponseDto> searchMemberDtosInPublication(MemberConditionToPublish condition);

    List<Member> findByIdList(List<Long> memberIdList);

    List<Member> findMembersByGroupCouponCond(GroupPublishRequestDto requestDto);

    List<Member> findByGrades(List<Grade> gradeList);

    List<Long> findMemberIdList(PublishToGroupDto requestDto);

    Page<QueryMembersDto> findDtosByCond(Pageable pageable, QueryMembersCond cond);

    Optional<QueryMemberDto> findMemberDto(Long id);

    int findRewardById(Long id);

    Long findCountByMyCode(String myRecommendationCode);

    QuerySnsDto findProviderByMember(Member member);

    MypageDto findMypageDtoByMember(Member member);

    List<Member> findFriendTalkGroupDto(FriendTalkGroupDto requestDto);

    Optional<MemberInfoResponseDto> findMemberInfoDto(Member member);
}
