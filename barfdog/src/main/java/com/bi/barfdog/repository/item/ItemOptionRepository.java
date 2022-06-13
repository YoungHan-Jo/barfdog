package com.bi.barfdog.repository.item;

import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemOptionRepository extends JpaRepository<ItemOption,Long>, ItemOptionRepositoryCustom {
    List<ItemOption> findByItem(Item item);


    void deleteByItem(Item item);
}
