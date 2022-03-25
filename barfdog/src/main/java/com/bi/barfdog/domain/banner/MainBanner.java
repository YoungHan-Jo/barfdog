package com.bi.barfdog.domain.banner;

import com.bi.barfdog.api.dto.MainBannerSaveRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("main")
@Getter @NoArgsConstructor
public class MainBanner extends Banner{

    private int leakedOrder;

    @Embedded
    private ImgFile imgFile;

    @Enumerated(EnumType.STRING)
    private BannerTargets targets = BannerTargets.ALL; // [ALL, GUESTS, MEMBERS, SUBSCRIBERS]

    @Builder
    public MainBanner(Long id, String name, String pcLinkUrl, String mobileLinkUrl, BannerStatus status, int leakedOrder, ImgFile imgFile, BannerTargets targets) {
        super(id, name, pcLinkUrl, mobileLinkUrl, status);
        this.leakedOrder = leakedOrder;
        this.imgFile = imgFile;
        this.targets = targets;
    }

    public MainBanner updateBanner(MainBannerSaveRequestDto requestDto) {
        setName(requestDto.getName());
        setPcLinkUrl(requestDto.getPcLinkUrl());
        setMobileLinkUrl(requestDto.getMobileLinkUrl());
        setStatus(requestDto.getStatus());
        this.targets = requestDto.getTargets();

        return this;
    }

    public MainBanner updateBanner(MainBannerSaveRequestDto requestDto, ImgFile imgFile) {
        String folder = imgFile.getFolder();
        String filenamePc = imgFile.getFilenamePc();
        String filenameMobile = imgFile.getFilenameMobile();

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
        this.targets = requestDto.getTargets();

        return this;
    }

    public void orderUp() {
        this.leakedOrder -= leakedOrder;
    }

    public void orderDown() {
        this.leakedOrder += leakedOrder;
    }
}
