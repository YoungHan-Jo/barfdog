package com.bi.barfdog.repository.basket;

import com.bi.barfdog.api.basketDto.QueryBasketsDto;
import com.bi.barfdog.domain.member.Member;

import java.util.List;

public interface BasketRepositoryCustom {
    List<QueryBasketsDto> findBasketsDto(Member member);
}