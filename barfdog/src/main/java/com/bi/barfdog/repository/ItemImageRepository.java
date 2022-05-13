package com.bi.barfdog.repository;

import com.bi.barfdog.domain.item.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
}
