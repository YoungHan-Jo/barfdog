package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;

import java.util.Collection;
import java.util.List;

public interface ItemImageRepositoryCustom {
    List<QueryItemAdminDto.ItemImageAdminDto> findAdminDtoByItemId(Long id);

    List<String> findFilename();
}
