package com.bi.barfdog.domain.banner;

import com.bi.barfdog.api.dto.PopupBannerSaveRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("popup")
@Getter @NoArgsConstructor
public class PopupBanner extends Banner{

    private int leakedOrder;

    @Enumerated(EnumType.STRING)
    private PopupBannerPosition position; // [LEFT, MID, RIGHT]

    @Embedded
    private ImgFile imgFile;

    @Builder
    public PopupBanner(Long id, String name, String pcLinkUrl, String mobileLinkUrl, BannerStatus status, int leakedOrder, PopupBannerPosition position, ImgFile imgFile) {
        super(id, name, pcLinkUrl, mobileLinkUrl, status);
        this.leakedOrder = leakedOrder;
        this.position = position;
        this.imgFile = imgFile;
    }

    public PopupBanner updateBanner(PopupBannerSaveRequestDto requestDto) {
        setName(requestDto.getName());
        setPcLinkUrl(requestDto.getPcLinkUrl());
        setMobileLinkUrl(requestDto.getMobileLinkUrl());
        setStatus(requestDto.getStatus());
        this.position = requestDto.getPosition();

        return this;
    }

    public PopupBanner updateBanner(PopupBannerSaveRequestDto requestDto, ImgFile imgFile) {
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
        this.position = requestDto.getPosition();

        return this;
    }

    public void orderUp() {
        this.leakedOrder -= 1;
    }

    public void orderDown() {
        this.leakedOrder += 1;
    }
}
