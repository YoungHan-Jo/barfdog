package com.bi.barfdog.api.blogDto;

import com.bi.barfdog.domain.blog.Blog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticlesAdminDto {

    private Long articleId;

    private int articleNumber;

    private Long blogId;

    private String blogTitle;

}
