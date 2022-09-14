package com.bi.barfdog.domain.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ImgFile {

    private String folder; // 저장된 폴더이름
    private String filenamePc; // 파일 이름 PC
    private String filenameMobile; // 파일 이름 Mobile

}
