package com.bi.barfdog.repository;

import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    List<ItemImage> findByItemOrderByLeakOrderAsc(Item item);
}
