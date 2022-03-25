package com.bi.barfdog.repository;

import com.bi.barfdog.domain.banner.MainBanner;
import com.bi.barfdog.domain.banner.PopupBanner;

import java.util.List;

public interface BannerRepositoryCustom {
    List<MainBanner> findMainBanners();

    List<PopupBanner> findPopupBanners();


}
