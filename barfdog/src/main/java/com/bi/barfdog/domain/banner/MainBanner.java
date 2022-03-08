package com.bi.barfdog.domain.banner;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("main")
@Getter
public class MainBanner extends Banner{

    private String pcImgPath;
    private String mobileImgPath;

    @Enumerated(EnumType.STRING)
    private BannerTargets targets; // [ALL, GUESTS, MEMBERS, SUBSCRIBERS]

    @Builder
    public MainBanner(String name, LocalDateTime createDate, int leakedOrder, String pcUrlLink, String mobileUrlLink, BannerStatus status, String pcImgPath, String mobileImgPath, BannerTargets targets) {
        super(name, createDate, leakedOrder, pcUrlLink, mobileUrlLink, status);
        this.pcImgPath = pcImgPath;
        this.mobileImgPath = mobileImgPath;
        this.targets = targets;
    }
}
