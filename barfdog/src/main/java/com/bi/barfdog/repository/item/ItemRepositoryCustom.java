package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;
import com.bi.barfdog.api.itemDto.QueryItemsAdminDto;
import com.bi.barfdog.api.itemDto.QueryItemsAdminRequestDto;
import com.bi.barfdog.domain.item.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    QueryItemAdminDto.ItemAdminDto findAdminDtoById(Long id);


    Page<QueryItemsAdminDto> findAdminDtoList(Pageable pageable, ItemType itemType);
}
