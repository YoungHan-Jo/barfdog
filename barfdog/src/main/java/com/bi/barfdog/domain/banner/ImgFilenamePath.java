package com.bi.barfdog.domain.banner;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImgFilenamePath {

    public String folder;
    public String filename;
}
