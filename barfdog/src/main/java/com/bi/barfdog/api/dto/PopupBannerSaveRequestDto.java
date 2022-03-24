package com.bi.barfdog.api.dto;

import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.PopupBannerPosition;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PopupBannerSaveRequestDto {

    @NotEmpty
    private String name;

    private PopupBannerPosition position = PopupBannerPosition.LEFT;

    private BannerStatus status = BannerStatus.LEAKED;

    @NotEmpty
    private String pcLinkUrl;

    @NotEmpty
    private String mobileLinkUrl;



}
