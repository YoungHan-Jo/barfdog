package com.bi.barfdog.repository.card;

import com.bi.barfdog.api.cardDto.QuerySubscribeCardsDto;
import com.bi.barfdog.domain.member.Member;

import java.util.List;

public interface CardRepositoryCustom {
    List<QuerySubscribeCardsDto> findSubscribeCards(Member member);
}
