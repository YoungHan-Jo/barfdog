package com.bi.barfdog.repository.item;

import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long>, ItemImageRepositoryCustom {
    List<ItemImage> findByItemOrderByLeakOrderAsc(Item item);

    List<ItemImage> findByItem(Item item);

    void deleteByItem(Item item);
}
