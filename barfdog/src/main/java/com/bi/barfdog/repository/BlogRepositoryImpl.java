package com.bi.barfdog.repository;


import com.bi.barfdog.api.blogDto.BlogAdminDto;
import com.bi.barfdog.api.blogDto.BlogTitlesDto;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.domain.blog.QBlog;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.blog.QBlog.*;

@RequiredArgsConstructor
@Repository
public class BlogRepositoryImpl implements BlogRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<QueryBlogsAdminDto> findAdminListDtos(Pageable pageable) {
        List<QueryBlogsAdminDto> result = queryFactory
                .select(Projections.constructor(QueryBlogsAdminDto.class,
                        blog.id,
                        blog.title,
                        blog.createdDate,
                        blog.status
                ))
                .from(blog)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(blog.id.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(blog.count())
                .from(blog)
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public List<BlogTitlesDto> findTitleDtos() {
        return queryFactory
                .select(Projections.constructor(BlogTitlesDto.class,
                        blog.id,
                        blog.title
                ))
                .from(blog)
                .where(blog.status.eq(BlogStatus.LEAKED))
                .orderBy(blog.createdDate.desc())
                .fetch();
    }

    @Override
    public BlogAdminDto findAdminDtoById(Long id) {
        return queryFactory
                .select(Projections.constructor(BlogAdminDto.class,
                        blog.id,
                        blog.status,
                        blog.title,
                        blog.category,
                        blog.contents
                ))
                .from(blog)
                .where(blog.id.eq(id))
                .fetchOne();
    }
}
