package com.bi.barfdog.api.dto;

import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.BannerTargets;
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

    private BannerStatus status = BannerStatus.LEAKED;

    @NotEmpty
    private String pcLinkUrl;

    @NotEmpty
    private String mobileLinkUrl;

}
