package com.bi.barfdog.repository;

import com.bi.barfdog.api.blogDto.ArticlesAdminDto;
import com.bi.barfdog.domain.blog.QArticle;
import com.bi.barfdog.domain.blog.QBlog;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.blog.QArticle.*;
import static com.bi.barfdog.domain.blog.QBlog.blog;

@RequiredArgsConstructor
@Repository
public class ArticleRepositoryImpl implements ArticleRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ArticlesAdminDto> findArticlesAdminDto() {
        return queryFactory
                .select(Projections.constructor(ArticlesAdminDto.class,
                        article.id,
                        article.number,
                        blog.id,
                        blog.title
                        ))
                .from(article)
                .leftJoin(article.blog, blog)
                .orderBy(article.number.asc())
                .fetch();
    }











}
