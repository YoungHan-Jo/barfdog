package com.bi.barfdog.domain.banner;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ImgFile {

    private String folder;
    private String filenamePc;
    private String filenameMobile;

}
