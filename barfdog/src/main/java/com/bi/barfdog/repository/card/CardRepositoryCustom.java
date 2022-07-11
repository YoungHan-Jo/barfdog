package com.bi.barfdog.repository.card;

import com.bi.barfdog.api.cardDto.QuerySubscribeCardsDto;
import com.bi.barfdog.domain.member.Member;

public interface CardRepositoryCustom {
    QuerySubscribeCardsDto findSubscribeCards(Member member);
}
