package com.bi.barfdog.api.dto;

import com.bi.barfdog.domain.banner.BannerStatus;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TopBannerSaveRequestDto {

    @NotEmpty
    private String name;

    private BannerStatus status = BannerStatus.LEAKED;

    private String backgroundColor = "#CA0101";
    private String fontColor = "#fff";

    @NotEmpty
    private String pcLinkUrl;

    @NotEmpty
    private String mobileLinkUrl;
}
