package com.bi.barfdog.service;

import com.bi.barfdog.api.barfDto.HomePageDto;
import com.bi.barfdog.api.reviewDto.QueryBestReviewsDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.banner.BannerRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.review.BestReviewRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BarfService {

    private final BannerRepository bannerRepository;
    private final RecipeRepository recipeRepository;
    private final BestReviewRepository bestReviewRepository;


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

}
