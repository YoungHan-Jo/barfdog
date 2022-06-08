package com.bi.barfdog.api.blogDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogAdminDto {

    private Long id;
    private BlogStatus status; // [LEAKED, HIDDEN]
    private String title;
    private BlogCategory category; // [NUTRITION,HEALTH,LIFE]
    private Long thumbnailId;
    private String filename;
    private String thumbnailUrl;
    private String contents; // 상세내용

    public void changeUrl() {
        this.thumbnailUrl = linkTo(InfoController.class).slash("display/blogs?filename=" + filename).toString();
    }
}
