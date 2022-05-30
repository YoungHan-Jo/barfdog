package com.bi.barfdog.repository;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;

import java.util.List;

public interface ItemContentImageRepositoryCustom {
    List<QueryItemAdminDto.ItemContentImageDto> findAdminDtoByItemId(Long id);
}
