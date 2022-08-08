package com.bi.barfdog.service;

import com.bi.barfdog.api.barfDto.FriendTalkAllDto;
import com.bi.barfdog.api.barfDto.FriendTalkGroupDto;
import com.bi.barfdog.api.barfDto.HomePageDto;
import com.bi.barfdog.api.reviewDto.QueryBestReviewsDto;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.directsend.FriendTalkResponseDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.banner.BannerRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.review.BestReviewRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BarfService {

    private final BannerRepository bannerRepository;
    private final RecipeRepository recipeRepository;
    private final BestReviewRepository bestReviewRepository;
    private final MemberRepository memberRepository;


    public HomePageDto getHome(Member member) {

        HomePageDto.TopBannerDto topBannerDto = bannerRepository.findTopBannerDto();
        List<HomePageDto.MainBannerDto> mainBannerDtoList = bannerRepository.findMainBannerDtoListByMember(member);

        List<HomePageDto.RecipeDto> recipeDtoList = recipeRepository.findRecipeDto();
        List<QueryBestReviewsDto> queryBestReviewsDtoList = bestReviewRepository.findBestReviewsDto();

        HomePageDto homePageDto = HomePageDto.builder()
                .topBannerDto(topBannerDto)
                .mainBannerDtoList(mainBannerDtoList)
                .recipeDtoList(recipeDtoList)
                .queryBestReviewsDtoList(queryBestReviewsDtoList)
                .build();

        return homePageDto;
    }


    public FriendTalkResponseDto sendFriendTalkAll(FriendTalkAllDto requestDto) {

        List<Member> memberList = memberRepository.findAll();

        try {
            FriendTalkResponseDto responseDto = DirectSendUtils.sendFriendTalk(requestDto.getTemplateNum() + "", memberList);
            return responseDto;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FriendTalkResponseDto.builder().build();
    }

    public FriendTalkResponseDto sendFriendTalkGroup(FriendTalkGroupDto requestDto) {

        List<Member> memberList = memberRepository.findFriendTalkGroupDto(requestDto);

        try {
            FriendTalkResponseDto responseDto = DirectSendUtils.sendFriendTalk(requestDto.getTemplateNum() + "", memberList);
            return responseDto;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FriendTalkResponseDto.builder().build();
    }
}
