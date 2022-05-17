package com.bi.barfdog.api.blogDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogImageDto {

    private Long id;

    private String url;
    private String thumbnailUrl;

}
