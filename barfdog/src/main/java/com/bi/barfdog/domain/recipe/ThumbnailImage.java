package com.bi.barfdog.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ThumbnailImage {

    private String folder;
    private String filename1; // 썸네일1
    private String filename2; // 썸네일2

}
