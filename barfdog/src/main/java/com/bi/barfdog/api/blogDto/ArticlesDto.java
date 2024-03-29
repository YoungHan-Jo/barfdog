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
public class ArticlesDto {

    private Long id;
    private int number;
    private String url;
    private BlogCategory category; // [NUTRITION,HEALTH,LIFE]
    private String title;
    private LocalDateTime createdDate;

    public void changeUrl(String filename) {
        url = linkTo(InfoController.class).slash("display/blogs?filename=" + filename).toString();
    }
}
