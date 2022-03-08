package com.bi.barfdog.domain.banner;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("top")
@Getter
public class TopBanner extends Banner{

    private String backgroundColor;
    private String fontColor;

    @Builder
    public TopBanner(String name, LocalDateTime createDate, int leakedOrder, String pcUrlLink, String mobileUrlLink, BannerStatus status, String backgroundColor, String fontColor) {
        super(name, createDate, leakedOrder, pcUrlLink, mobileUrlLink, status);
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
    }
}
