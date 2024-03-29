package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.PopupBannerPosition;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PopupBannerSaveRequestDto {

    @NotEmpty
    private String name;
    @Builder.Default
    private PopupBannerPosition position = PopupBannerPosition.LEFT;
    @Builder.Default
    private BannerStatus status = BannerStatus.LEAKED;

    @NotNull
    private String pcLinkUrl;

    @NotNull
    private String mobileLinkUrl;



}
