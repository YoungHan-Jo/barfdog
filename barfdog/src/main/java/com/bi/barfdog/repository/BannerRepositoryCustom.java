package com.bi.barfdog.repository;

import com.bi.barfdog.domain.banner.MainBanner;
import com.bi.barfdog.domain.banner.MyPageBanner;
import com.bi.barfdog.domain.banner.PopupBanner;
import com.bi.barfdog.domain.banner.TopBanner;

import java.util.List;

public interface BannerRepositoryCustom {
    List<MainBanner> findMainBanners();

    List<PopupBanner> findPopupBanners();

    List<TopBanner> findTopBanners();

    List<MyPageBanner> findMyPageBanners();

    MainBanner findToDownByOrder(int order);
}
