package com.bi.barfdog.domain.banner;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("popup")
@Getter @NoArgsConstructor
public class PopupBanner extends Banner{

    @Embedded
    private ImgFile imgfile;

    @Builder
    public PopupBanner(Long id, String name, int leakedOrder, LinkUrl linkUrl, BannerStatus status, ImgFile imgfile) {
        super(id, name, leakedOrder, linkUrl, status);
        this.imgfile = imgfile;
    }
}
