package com.bi.barfdog.repository;

import com.bi.barfdog.domain.blog.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
    Optional<Article> findByNumber(int i);

}
