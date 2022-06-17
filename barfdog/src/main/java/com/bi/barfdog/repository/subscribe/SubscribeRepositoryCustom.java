package com.bi.barfdog.repository.subscribe;

import com.bi.barfdog.api.memberDto.MemberSubscribeAdminDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.Subscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscribeRepositoryCustom {
    Page<MemberSubscribeAdminDto> findSubscribeAdminDtoByMemberId(Long id, Pageable pageable);

    List<Subscribe> findWriteableByMember(Member member);
}