package com.bi.barfdog.repository;


import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
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
}
