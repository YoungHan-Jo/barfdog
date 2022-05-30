package com.bi.barfdog.repository;

import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemContentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemContentImageRepository extends JpaRepository<ItemContentImage, Long>, ItemContentImageRepositoryCustom {
    List<ItemContentImage> findByItem(Item item);
}
