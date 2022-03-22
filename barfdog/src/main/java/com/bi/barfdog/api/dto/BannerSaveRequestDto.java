package com.bi.barfdog.api.dto;

import com.bi.barfdog.domain.banner.Banner;
import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.BannerTargets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerSaveRequestDto {

    @NotEmpty
    private String bannerType; // [ main, mypage, popup, top ]

    @NotEmpty
    private String name;

    private BannerTargets targets;

    @NotEmpty
    private BannerStatus status;

    private int leakedOrder;

    @NotEmpty
    private String pcLinkUrl;

    @NotEmpty
    private String mobileLinkUrl;

    private String backgroundColor;
    private String fontColor;

}
