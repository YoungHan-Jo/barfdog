package com.bi.barfdog.domain.banner;

import com.bi.barfdog.api.bannerDto.TopBannerSaveRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("top")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopBanner extends Banner{

    private String backgroundColor;
    private String fontColor;

    @Builder
    public TopBanner(Long id, String name, String pcLinkUrl, String mobileLinkUrl, BannerStatus status, String backgroundColor, String fontColor) {
        super(id, name, pcLinkUrl, mobileLinkUrl, status);
        this.backgroundColor = backgroundColor;
        this.fontColor = fontColor;
    }

    public TopBanner update(TopBannerSaveRequestDto requestDto) {
        setName(requestDto.getName());
        setPcLinkUrl(requestDto.getPcLinkUrl());
        setMobileLinkUrl(requestDto.getMobileLinkUrl());
        setStatus(requestDto.getStatus());
        this.backgroundColor = requestDto.getBackgroundColor();
        this.fontColor = requestDto.getFontColor();

        return this;
    }
}
