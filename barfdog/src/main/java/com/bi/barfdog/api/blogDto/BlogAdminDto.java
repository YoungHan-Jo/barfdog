package com.bi.barfdog.api.blogDto;

import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogAdminDto {

    private Long id;
    private BlogStatus status; // [LEAKED, HIDDEN]
    private String title;
    private BlogCategory category; // [NUTRITION,HEALTH,LIFE]
    private String contents; // 상세내용

}
