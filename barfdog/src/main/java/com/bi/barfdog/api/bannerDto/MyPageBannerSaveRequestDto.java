package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.BannerStatus;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MyPageBannerSaveRequestDto {

    @NotEmpty
    private String name;
    @Builder.Default
    private BannerStatus status = BannerStatus.LEAKED;

    @NotEmpty
    private String pcLinkUrl;

    @NotEmpty
    private String mobileLinkUrl;

}
