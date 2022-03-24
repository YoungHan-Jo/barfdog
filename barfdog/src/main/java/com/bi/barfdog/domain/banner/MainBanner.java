package com.bi.barfdog.domain.banner;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("main")
@Getter @NoArgsConstructor
public class MainBanner extends Banner{

    private int leakedOrder;

    @Embedded
    private ImgFile imgFile;

    @Enumerated(EnumType.STRING)
    private BannerTargets targets = BannerTargets.ALL; // [ALL, GUESTS, MEMBERS, SUBSCRIBERS]

    @Builder
    public MainBanner(Long id, String name, String pcLinkUrl, String mobileLinkUrl, BannerStatus status, int leakedOrder, ImgFile imgFile, BannerTargets targets) {
        super(id, name, pcLinkUrl, mobileLinkUrl, status);
        this.leakedOrder = leakedOrder;
        this.imgFile = imgFile;
        this.targets = targets;
    }

}
