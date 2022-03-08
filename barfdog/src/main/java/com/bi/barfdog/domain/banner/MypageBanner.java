package com.bi.barfdog.domain.banner;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("mypage")
@Getter
public class MypageBanner extends Banner{

    private String pcImgPath;
    private String mobileImgPath;

    @Builder
    public MypageBanner(String name, LocalDateTime createDate, int leakedOrder, String pcUrlLink, String mobileUrlLink, BannerStatus status, String pcImgPath, String mobileImgPath) {
        super(name, createDate, leakedOrder, pcUrlLink, mobileUrlLink, status);
        this.pcImgPath = pcImgPath;
        this.mobileImgPath = mobileImgPath;
    }
}
