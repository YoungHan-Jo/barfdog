package com.bi.barfdog.domain.banner;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("popup")
@Getter @NoArgsConstructor
public class PopupBanner extends Banner{

    private int leakedOrder;

    @Enumerated(EnumType.STRING)
    private PopupBannerPosition position; // [LEFT, MID, RIGHT]

    @Embedded
    private ImgFile imgFile;

    @Builder
    public PopupBanner(Long id, String name, String pcLinkUrl, String mobileLinkUrl, BannerStatus status, int leakedOrder, PopupBannerPosition position, ImgFile imgFile) {
        super(id, name, pcLinkUrl, mobileLinkUrl, status);
        this.leakedOrder = leakedOrder;
        this.position = position;
        this.imgFile = imgFile;
    }
}
