package com.bi.barfdog.domain.banner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue("main")
@Getter @Setter
@NoArgsConstructor
public class MainBanner extends Banner{

    private String pcImg;
    private String pcUrl;
    private String mobileImg;
    private String mobileUrl;

    @Enumerated(EnumType.STRING)
    private BannerTargets targets; // [ALL, GUESTS, MEMBERS, SUBSCRIBERS]

}
