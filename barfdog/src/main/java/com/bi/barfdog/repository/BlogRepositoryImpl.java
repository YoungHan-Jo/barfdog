package com.bi.barfdog.repository;


import com.bi.barfdog.api.blogDto.BlogAdminDto;
import com.bi.barfdog.api.blogDto.BlogTitlesDto;
import com.bi.barfdog.api.blogDto.NoticeAdminDto;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import com.bi.barfdog.api.noticeDto.QueryNoticesDto;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public Page<QueryBlogsAdminDto> findAdminBlogListDtos(Pageable pageable) {
        List<QueryBlogsAdminDto> result = queryFactory
                .select(Projections.constructor(QueryBlogsAdminDto.class,
                        blog.id,
                        blog.title,
                        blog.createdDate,
                        blog.status
                ))
                .from(blog)
                .where(categoryNotInNotice())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(blog.id.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(blog.count())
                .from(blog)
                .where(categoryNotInNotice())
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public List<BlogTitlesDto> findTitleDtosForArticles() {
        return queryFactory
                .select(Projections.constructor(BlogTitlesDto.class,
                        blog.id,
                        blog.title
                ))
                .from(blog)
                .where(blog.status.eq(BlogStatus.LEAKED).and(categoryNotInNotice()))
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

    @Override
    public NoticeAdminDto findAdminNoticeDtoById(Long id) {
        return queryFactory
                .select(Projections.constructor(NoticeAdminDto.class,
                        blog.id,
                        blog.status,
                        blog.title,
                        blog.contents
                        ))
                .from(blog)
                .where(blog.id.eq(id))
                .fetchOne()
                ;
    }

    @Override
    public Page<QueryNoticesDto> findNoticeDtos(Pageable pageable) {
        List<QueryNoticesDto> result = queryFactory
                .select(Projections.constructor(QueryNoticesDto.class,
                        blog.id,
                        blog.title,
                        blog.createdDate
                ))
                .from(blog)
                .where(EqLeakedNotice())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(blog.createdDate.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(blog.count())
                .from(blog)
                .where(EqLeakedNotice())
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression EqLeakedNotice() {
        return categoryEqNotice().and(blog.status.eq(BlogStatus.LEAKED));
    }

    @Override
    public List<Blog> findAllNotices() {
        return queryFactory
                .selectFrom(blog)
                .where(categoryEqNotice())
                .fetch()
                ;
    }

    @Override
    public Page<QueryBlogsAdminDto> findAdminNoticeListDtos(Pageable pageable) {
        List<QueryBlogsAdminDto> result = queryFactory
                .select(Projections.constructor(QueryBlogsAdminDto.class,
                        blog.id,
                        blog.title,
                        blog.createdDate,
                        blog.status
                ))
                .from(blog)
                .where(categoryEqNotice())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(blog.id.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(blog.count())
                .from(blog)
                .where(categoryEqNotice())
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public List<Blog> findLeakedNotices() {
        return queryFactory
                .selectFrom(blog)
                .where(blog.status.eq(BlogStatus.LEAKED).and(categoryEqNotice()))
                .fetch()
                ;

    }




    private BooleanExpression categoryNotInNotice() {
        return blog.category.notIn(BlogCategory.NOTICE);
    }

    private BooleanExpression categoryEqNotice() {
        return blog.category.eq(BlogCategory.NOTICE);
    }



}
