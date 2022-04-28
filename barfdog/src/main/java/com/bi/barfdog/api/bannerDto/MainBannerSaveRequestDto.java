package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.BannerTargets;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder @Data
public class MainBannerSaveRequestDto {

    @NotEmpty
    private String name;
    @Builder.Default
    private BannerTargets targets = BannerTargets.ALL;
    @Builder.Default
    private BannerStatus status = BannerStatus.LEAKED;

    @NotNull
    private String pcLinkUrl;

    @NotNull
    private String mobileLinkUrl;

}
