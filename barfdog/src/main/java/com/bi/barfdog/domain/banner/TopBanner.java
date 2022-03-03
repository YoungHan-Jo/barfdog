package com.bi.barfdog.domain.banner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("top")
@Getter @Setter
@NoArgsConstructor
public class TopBanner extends Banner{

    private String pcUrl;
    private String mobileUrl;
    private String bgColor;
    private String fontColor;

}
