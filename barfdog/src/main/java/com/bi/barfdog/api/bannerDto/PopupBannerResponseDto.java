package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.PopupBannerPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopupBannerResponseDto {

    private Long id;
    private String name;
    private BannerStatus status;

    private PopupBannerPosition position;

    private String filenamePc;
    private String filenameMobile;

    private String pcLinkUrl;
    private String mobileLinkUrl;

}
