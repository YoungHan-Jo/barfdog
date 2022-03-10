package com.bi.barfdog.domain.banner;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("main")
@Getter @NoArgsConstructor
public class MainBanner extends Banner{

    @Embedded
    private ImgFile imgfile;

    @Enumerated(EnumType.STRING)
    private BannerTargets targets; // [ALL, GUESTS, MEMBERS, SUBSCRIBERS]

    @Builder
    public MainBanner(Long id, String name, int leakedOrder, LinkUrl linkUrl, BannerStatus status, ImgFile imgfile, BannerTargets targets) {
        super(id, name, leakedOrder, linkUrl, status);
        this.imgfile = imgfile;
        this.targets = targets;
    }
}
