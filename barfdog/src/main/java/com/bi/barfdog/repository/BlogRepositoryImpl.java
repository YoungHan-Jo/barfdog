package com.bi.barfdog.repository;


import com.bi.barfdog.api.NoticeApiController;
import com.bi.barfdog.api.blogDto.BlogAdminDto;
import com.bi.barfdog.api.blogDto.BlogTitlesDto;
import com.bi.barfdog.api.blogDto.NoticeAdminDto;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import com.bi.barfdog.api.noticeDto.QueryNoticePageDto;
import com.bi.barfdog.api.noticeDto.QueryNoticesDto;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.domain.blog.QBlogThumbnail;
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
import static com.bi.barfdog.domain.blog.QBlogThumbnail.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
        BlogAdminDto result = queryFactory
                .select(Projections.constructor(BlogAdminDto.class,
                        blog.id,
                        blog.status,
                        blog.title,
                        blog.category,
                        blogThumbnail.id,
                        blogThumbnail.filename,
                        blogThumbnail.filename,
                        blog.contents
                ))
                .from(blog)
                .join(blog.blogThumbnail, blogThumbnail)
                .where(blog.id.eq(id))
                .fetchOne();

        result.changeUrl();

        return result;
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
                .where(eqLeakedNotice())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(blog.createdDate.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(blog.count())
                .from(blog)
                .where(eqLeakedNotice())
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public QueryNoticePageDto findNoticePageDtoById(Long id) {

        QueryNoticePageDto.NoticeDto noticeDto = queryFactory.select(Projections.constructor(QueryNoticePageDto.NoticeDto.class,
                        blog.id,
                        blog.title,
                        blog.createdDate,
                        blog.contents
                ))
                .from(blog)
                .where(blog.id.eq(id))
                .fetchOne();
        List<Blog> notices = queryFactory
                .select(blog)
                .from(blog)
                .where(eqLeakedNotice())
                .orderBy(blog.createdDate.desc())
                .fetch();

        QueryNoticePageDto.AnotherNotice previous = null;
        QueryNoticePageDto.AnotherNotice next = null;

        for (int i = 0; i < notices.size(); ++i) {
            Blog notice = notices.get(i);
            if (notice.getId() == id) {
                previous = getPrevious(notices, previous, i);
                next = getNext(notices, next, i);
                break;
            }
        }

        QueryNoticePageDto result = QueryNoticePageDto.builder()
                .noticeDto(noticeDto)
                .previous(previous)
                .next(next)
                .build();

        return result;
    }

    private QueryNoticePageDto.AnotherNotice getNext(List<Blog> notices, QueryNoticePageDto.AnotherNotice next, int i) {
        if (i + 1 < notices.size()) {
            Blog nextNotice = notices.get(i + 1);
            Long nextId = nextNotice.getId();
            next = QueryNoticePageDto.AnotherNotice.builder()
                    .id(nextId)
                    .title(nextNotice.getTitle())
                    ._link(linkTo(NoticeApiController.class).slash(nextId).toString())
                    .build();
        }
        return next;
    }

    private QueryNoticePageDto.AnotherNotice getPrevious(List<Blog> notices, QueryNoticePageDto.AnotherNotice previous, int i) {
        if (i - 1 >= 0) {
            Blog previousNotice = notices.get(i - 1);
            Long prevId = previousNotice.getId();
            previous = QueryNoticePageDto.AnotherNotice.builder()
                    .id(prevId)
                    .title(previousNotice.getTitle())
                    ._link(linkTo(NoticeApiController.class).slash(prevId).toString())
                    .build();
        }
        return previous;
    }

    private BooleanExpression eqLeakedNotice() {
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
