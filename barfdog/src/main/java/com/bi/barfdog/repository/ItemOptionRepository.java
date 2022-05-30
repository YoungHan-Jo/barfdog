package com.bi.barfdog.repository;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface ItemOptionRepository extends JpaRepository<ItemOption,Long>, ItemOptionRepositoryCustom {
    List<ItemOption> findByItem(Item item);


}
