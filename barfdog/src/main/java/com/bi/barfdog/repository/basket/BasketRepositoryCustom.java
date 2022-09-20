package com.bi.barfdog.repository.basket;

import com.bi.barfdog.api.basketDto.QueryBasketsDto;
import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface BasketRepositoryCustom {
    List<QueryBasketsDto> findBasketsDto(Member member);

    List<Basket> findByMemberAndItems(Member member, List<Item> selectItemList);

    Optional<Basket> findByMemberAndItemId(Member member, Long itemId);
}
