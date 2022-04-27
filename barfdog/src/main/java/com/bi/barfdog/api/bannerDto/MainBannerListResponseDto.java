package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.BannerTargets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainBannerListResponseDto {

    private Long id;
    private int leakedOrder;

    private String name;

    private BannerTargets targets;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private String filenamePc;
    private String filenameMobile;

}
