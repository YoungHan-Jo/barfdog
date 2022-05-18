package com.bi.barfdog.repository;

import com.bi.barfdog.domain.blog.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
}
