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

    // 상단 띠 배너는 이미지를 삽입하지 않고, 그냥 글자만 넣기 때문에, 배경색과 폰트 색만 그대로 저장한다

    private String backgroundColor; // 상단 띠 배너 배경 색
    private String fontColor; // 상단 띠 배너 글씨 색깔

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
