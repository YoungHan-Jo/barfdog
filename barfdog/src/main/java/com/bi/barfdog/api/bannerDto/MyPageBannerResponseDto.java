package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.BannerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageBannerResponseDto {

    private Long id;
    private String name;
    private BannerStatus status;

    private String filenamePc;
    private String filenameMobile;

    private String pcLinkUrl;
    private String mobileLinkUrl;

}
