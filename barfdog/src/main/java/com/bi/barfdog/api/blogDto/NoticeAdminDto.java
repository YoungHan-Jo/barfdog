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
public class NoticeAdminDto {

    private Long id;
    private BlogStatus status; // [LEAKED, HIDDEN]
    private String title;
    private String contents; // 상세내용

}
