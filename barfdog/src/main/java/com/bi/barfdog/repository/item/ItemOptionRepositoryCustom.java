package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;

import java.util.List;

public interface ItemOptionRepositoryCustom {
    List<QueryItemAdminDto.ItemOptionAdminDto> findAdminDtoListByItemId(Long id);
}
