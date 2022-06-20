package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.itemDto.*;
import com.bi.barfdog.api.reviewDto.ReviewItemsDto;
import com.bi.barfdog.domain.item.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    QueryItemAdminDto.ItemAdminDto findAdminDtoById(Long id);


    Page<QueryItemsAdminDto> findAdminDtoList(Pageable pageable, ItemType itemType);

    Page<QueryItemsDto> findItemsDto(Pageable pageable, ItemsCond cond);

    QueryItemDto findItemDtoById(Long id);

    List<ReviewItemsDto> findReviewItemsDtoByItemType(ItemType itemType);
}
