package com.bi.barfdog.repository;

import com.bi.barfdog.api.blogDto.QueryBlogsAdminDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogRepositoryCustom {
    Page<QueryBlogsAdminDto> findAdminListDtos(Pageable pageable);
}
