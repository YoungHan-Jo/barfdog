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
@Getter @NoArgsConstructor
public class TopBanner extends Banner{

    private String backgroundColor;
    private String fontColor;

    @Builder
    public TopBanner(Long id, String name, int leakedOrder, LinkUrl linkUrl, BannerStatus status, String backgroundColor, String fontColor) {
        super(id, name, leakedOrder, linkUrl, status);
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
    }
}
