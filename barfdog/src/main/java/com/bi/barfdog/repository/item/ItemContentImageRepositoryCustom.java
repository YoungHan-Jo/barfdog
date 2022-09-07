package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;

import java.util.List;

public interface ItemContentImageRepositoryCustom {
    List<QueryItemAdminDto.ItemContentImageDto> findAdminDtoByItemId(Long id);

    List<String> findFilename();
}
