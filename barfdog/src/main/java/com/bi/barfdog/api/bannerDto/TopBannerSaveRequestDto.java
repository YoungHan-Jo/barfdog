package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.BannerStatus;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TopBannerSaveRequestDto {

    @NotEmpty
    private String name;

    @Builder.Default
    private BannerStatus status = BannerStatus.LEAKED;
    @Builder.Default
    private String backgroundColor = "#CA0101";
    @Builder.Default
    private String fontColor = "#fff";

    @NotNull
    private String pcLinkUrl;

    @NotNull
    private String mobileLinkUrl;
}
