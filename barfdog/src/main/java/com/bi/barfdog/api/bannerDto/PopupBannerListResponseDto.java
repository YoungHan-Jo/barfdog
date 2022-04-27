package com.bi.barfdog.api.bannerDto;

import com.bi.barfdog.domain.banner.PopupBannerPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopupBannerListResponseDto {

    private Long id;

    private int leakedOrder;

    private PopupBannerPosition position;

    private String name;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private String filenamePc;
    private String filenameMobile;

}
