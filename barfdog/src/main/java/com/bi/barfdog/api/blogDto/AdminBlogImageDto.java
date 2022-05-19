package com.bi.barfdog.api.blogDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminBlogImageDto {

    private Long blogImageId;

    private String filename;

    private String url;

    public void setDisplayUrl(String url) {
        this.url = url + filename;
    }
}
