package com.bi.barfdog.repository.article;

import com.bi.barfdog.api.blogDto.ArticlesAdminDto;
import com.bi.barfdog.api.blogDto.ArticlesDto;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<ArticlesAdminDto> findArticlesAdminDto();

    Long findCountByBlogId(Long id);

    List<ArticlesDto> findArticlesDto();
}
