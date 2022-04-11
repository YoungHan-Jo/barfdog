package com.bi.barfdog.domain.banner;

import com.bi.barfdog.api.bannerDto.MyPageBannerSaveRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("myPage")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageBanner extends Banner{

    @Embedded
    private ImgFile imgFile;

    @Builder
    public MyPageBanner(Long id, String name, String pcLinkUrl, String mobileLinkUrl, BannerStatus status, ImgFile imgFile) {
        super(id, name, pcLinkUrl, mobileLinkUrl, status);
        this.imgFile = imgFile;
    }

    public MyPageBanner updateBanner(MyPageBannerSaveRequestDto requestDto) {
        setName(requestDto.getName());
        setPcLinkUrl(requestDto.getPcLinkUrl());
        setMobileLinkUrl(requestDto.getMobileLinkUrl());
        setStatus(requestDto.getStatus());

        return this;
    }

    public MyPageBanner updateBanner(MyPageBannerSaveRequestDto requestDto, ImgFile imgFile) {
        String folder = imgFile.getFolder();
        String filenameMobile = imgFile.getFilenameMobile();
        String filenamePc = imgFile.getFilenamePc();

        if(filenamePc == null){
            this.imgFile.setFolder(folder);
            this.imgFile.setFilenameMobile(filenameMobile);
        }else if(filenameMobile == null){
            this.imgFile.setFolder(folder);
            this.imgFile.setFilenamePc(filenamePc);
        }else{
            this.imgFile.setFolder(folder);
            this.imgFile.setFilenamePc(filenamePc);
            this.imgFile.setFilenameMobile(filenameMobile);
        }
        setName(requestDto.getName());
        setPcLinkUrl(requestDto.getPcLinkUrl());
        setMobileLinkUrl(requestDto.getMobileLinkUrl());
        setStatus(requestDto.getStatus());

        return this;
    }
}
