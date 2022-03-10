package com.bi.barfdog.domain.banner;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("mypage")
@Getter @NoArgsConstructor
public class MypageBanner extends Banner{

    @Embedded
    private ImgFile imgfile;

    @Builder
    public MypageBanner(Long id, String name, int leakedOrder, LinkUrl linkUrl, BannerStatus status, ImgFile imgfile) {
        super(id, name, leakedOrder, linkUrl, status);
        this.imgfile = imgfile;
    }
}
