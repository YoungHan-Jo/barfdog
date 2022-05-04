package com.bi.barfdog.repository;

import com.bi.barfdog.api.bannerDto.*;
import com.bi.barfdog.domain.banner.MainBanner;
import com.bi.barfdog.domain.banner.MyPageBanner;
import com.bi.barfdog.domain.banner.PopupBanner;
import com.bi.barfdog.domain.banner.TopBanner;

import java.util.List;
import java.util.Optional;

public interface BannerRepositoryCustom {
    List<MainBanner> findMainBanners();

    List<PopupBanner> findPopupBanners();

    List<TopBanner> findTopBanners();

    List<MyPageBanner> findMyPageBanners();

    MainBanner findMainBannerByOrder(int order);

    PopupBanner findPopupBannerByOrder(int order);

    List<MainBannerListResponseDto> findMainBannersDtos();

    Optional<MainBannerResponseDto> findMainBannerDtoById(Long id);

    Optional<MainBanner> findMainBannerById(Long id);

    Optional<MyPageBannerResponseDto> findFirstMyPageBanner();

    Optional<TopBannerResponseDto> findFirstTopBannerDto();

    List<PopupBannerListResponseDto> findPopupBannerDtos();

    Optional<PopupBannerResponseDto> findPopupBannerDtoById(Long id);

    Optional<PopupBanner> findPopupBannerById(Long id);

    Optional<MyPageBanner> findMyPageBannerById(Long id);

    Optional<TopBanner> findTopBannerById(Long id);

    List<MainBanner> findMainBannersByName(String name);
}
