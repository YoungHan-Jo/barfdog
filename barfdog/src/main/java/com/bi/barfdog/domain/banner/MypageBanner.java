package com.bi.barfdog.domain.banner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("mypage")
@Getter @Setter
@NoArgsConstructor
public class MypageBanner extends Banner{

    private String pcImg;
    private String pcUrl;
    private String mobileImg;
    private String mobileUrl;

}
