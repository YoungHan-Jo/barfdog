package com.bi.barfdog.repository;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;

import java.util.List;

public interface ItemImageRepositoryCustom {
    List<QueryItemAdminDto.ItemImageAdminDto> findAdminDtoByItemId(Long id);
}
