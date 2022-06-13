package com.bi.barfdog.repository.article;

import com.bi.barfdog.api.blogDto.ArticlesAdminDto;
import com.bi.barfdog.api.blogDto.ArticlesDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.blog.QArticle.*;
import static com.bi.barfdog.domain.blog.QBlog.blog;
import static com.bi.barfdog.domain.blog.QBlogThumbnail.*;

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

    @Override
    public Long findCountByBlogId(Long id) {
        return queryFactory
                .select(article.count())
                .from(article)
                .where(article.blog.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<ArticlesDto> findArticlesDto() {
        List<ArticlesDto> result = queryFactory
                .select(Projections.constructor(ArticlesDto.class,
                        blog.id,
                        article.number,
                        blogThumbnail.filename,
                        blog.category,
                        blog.title,
                        blog.createdDate
                ))
                .from(article)
                .join(article.blog, blog)
                .join(blog.blogThumbnail, blogThumbnail)
                .orderBy(article.number.asc())
                .fetch();
        for (ArticlesDto articlesDto : result) {
            articlesDto.changeUrl(articlesDto.getUrl());
        }

        return result;
    }


}
