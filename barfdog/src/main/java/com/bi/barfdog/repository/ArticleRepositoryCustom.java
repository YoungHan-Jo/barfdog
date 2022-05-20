package com.bi.barfdog.repository;

import com.bi.barfdog.api.blogDto.ArticlesAdminDto;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<ArticlesAdminDto> findArticlesAdminDto();

    Long findCountByBlogId(Long id);
}
