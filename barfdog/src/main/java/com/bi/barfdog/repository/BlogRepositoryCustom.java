package com.bi.barfdog.repository;

import com.bi.barfdog.api.blogDto.BlogAdminDto;
import com.bi.barfdog.api.blogDto.BlogTitlesDto;
import com.bi.barfdog.api.blogDto.QueryAdminBlogDto;
import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogRepositoryCustom {
    Page<QueryBlogsAdminDto> findAdminListDtos(Pageable pageable);

    List<BlogTitlesDto> findTitleDtos();

    BlogAdminDto findAdminDtoById(Long id);

}
