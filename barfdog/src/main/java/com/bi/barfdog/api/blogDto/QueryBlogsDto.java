package com.bi.barfdog.api.blogDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.blog.BlogCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryBlogsDto {

    private Long id;
    private BlogCategory category;
    private String title;
    private String contents;
    private LocalDateTime createdDate;
    private String url;

    public void changeUrl(String filename) {
        this.url = linkTo(InfoController.class).slash("display/blogs?filename=" + filename).toString();
    }
}
