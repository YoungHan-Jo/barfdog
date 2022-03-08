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

    private String bannerType;

    private String name;
    private LocalDateTime createDate;
    private int leakedOrder;
    private BannerStatus status;

    private String pcUrlLink;
    private String mobileUrlLink;

    private String pcImgPath;
    private String mobileImgPath;

    private BannerTargets targets;

    private String backgroundColor;
    private String fontColor;




}
