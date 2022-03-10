package com.bi.barfdog.api.dto;

import com.bi.barfdog.domain.banner.Banner;
import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.BannerTargets;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BannerSaveRequestDto {

    private String bannerType; // [ main, mypage, popup, top ]

    private String name;
    private BannerTargets targets;
    private BannerStatus status;
    private int leakedOrder;

    private String pcLinkUrl;
    private String mobileLinkUrl;

    private String backgroundColor;
    private String fontColor;

}
