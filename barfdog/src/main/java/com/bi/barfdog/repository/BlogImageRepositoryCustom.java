package com.bi.barfdog.repository;

import com.bi.barfdog.api.blogDto.AdminBlogImageDto;

import java.util.List;

public interface BlogImageRepositoryCustom {
    List<AdminBlogImageDto> findAdminDtoByBlogId(Long id);
}
