package com.bi.barfdog.repository;

import com.bi.barfdog.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long>, ItemRepositoryCustom {
}
