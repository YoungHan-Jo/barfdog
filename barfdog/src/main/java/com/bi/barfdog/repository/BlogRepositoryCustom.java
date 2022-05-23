package com.bi.barfdog.repository;

import com.bi.barfdog.api.blogDto.BlogAdminDto;
import com.bi.barfdog.api.blogDto.BlogTitlesDto;
import com.bi.barfdog.api.blogDto.NoticeAdminDto;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import com.bi.barfdog.domain.blog.Blog;
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
}
