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

    private String folder;
    private String filenamePc;
    private String filenameMobile;

}
