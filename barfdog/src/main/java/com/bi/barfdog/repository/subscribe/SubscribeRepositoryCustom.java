package com.bi.barfdog.repository.subscribe;

import com.bi.barfdog.api.memberDto.MemberSubscribeAdminDto;
import com.bi.barfdog.api.orderDto.OrderSheetSubscribeResponseDto;
import com.bi.barfdog.api.subscribeDto.QuerySubscribeDto;
import com.bi.barfdog.api.subscribeDto.QuerySubscribesDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.Subscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscribeRepositoryCustom {
    Page<MemberSubscribeAdminDto> findSubscribeAdminDtoByMemberId(Long id, Pageable pageable);

    List<Subscribe> findWriteableByMember(Member member);

    OrderSheetSubscribeResponseDto.SubscribeDto findOrderSheetSubscribeDtoById(Long subscribeId);

    List<String> findRecipeNamesById(Long subscribeId);

    List<Subscribe> findAllByMember(Member member);

    Page<QuerySubscribesDto> findSubscribesDto(Member member, Pageable pageable);

    QuerySubscribeDto findSubscribeDto(Member member, Long id);

    List<Subscribe> findTomorrowPayment();

    Long findSubscribingCountByMember(Member member);

}
