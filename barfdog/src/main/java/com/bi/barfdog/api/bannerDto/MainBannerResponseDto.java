package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.BannerTargets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainBannerResponseDto {

    private Long id;

    private String name;

    private BannerTargets targets; // [ALL, GUEST, USER, SUBSCRIBER]

    private BannerStatus status; // [LEAKED, HIDDEN]

    private String filenamePc;
    private String filenameMobile;

    private String pcLinkUrl;
    private String mobileLinkUrl;

}
