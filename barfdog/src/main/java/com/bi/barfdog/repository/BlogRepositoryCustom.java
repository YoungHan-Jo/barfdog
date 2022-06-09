package com.bi.barfdog.repository;

import com.bi.barfdog.api.blogDto.*;
import com.bi.barfdog.api.noticeDto.QueryNoticePageDto;
import com.bi.barfdog.api.noticeDto.QueryNoticesDto;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogRepositoryCustom {
    Page<QueryBlogsAdminDto> findAdminBlogListDtos(Pageable pageable);

    List<BlogTitlesDto> findTitleDtosForArticles();

    BlogAdminDto findAdminDtoById(Long id);

    List<Blog> findAllNotices();

    Page<QueryBlogsAdminDto> findAdminNoticeListDtos(Pageable pageable);

    List<Blog> findLeakedNotices();

    NoticeAdminDto findAdminNoticeDtoById(Long id);

    Page<QueryNoticesDto> findNoticeDtos(Pageable pageable);

    QueryNoticePageDto findNoticePageDtoById(Long id);

    Page<QueryBlogsDto> findBlogsDto(Pageable pageable);

    Page<QueryBlogsDto> findBlogsDtoByCategory(Pageable pageable, BlogCategory blogCategory);

    QueryBlogDto findBlogDtoById(Long id);
}
