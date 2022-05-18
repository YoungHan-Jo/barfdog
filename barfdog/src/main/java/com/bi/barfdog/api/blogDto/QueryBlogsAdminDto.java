package com.bi.barfdog.api.blogDto;

import com.bi.barfdog.domain.blog.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryBlogsAdminDto {

    private Long id;

    private String title;

    private LocalDateTime createdDate;

    private BlogStatus status;

}
