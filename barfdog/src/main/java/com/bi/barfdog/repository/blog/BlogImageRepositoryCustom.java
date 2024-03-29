package com.bi.barfdog.repository.blog;

import com.bi.barfdog.api.blogDto.AdminBlogImageDto;

import java.util.List;

public interface BlogImageRepositoryCustom {
    List<AdminBlogImageDto> findAdminDtoByBlogId(Long id);

    List<String> findFilename();
}
