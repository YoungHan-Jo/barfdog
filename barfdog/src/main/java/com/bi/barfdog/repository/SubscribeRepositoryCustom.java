package com.bi.barfdog.repository;

import com.bi.barfdog.api.memberDto.MemberSubscribeAdminDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscribeRepositoryCustom {
    Page<MemberSubscribeAdminDto> findSubscribeAdminDtoByMemberId(Long id, Pageable pageable);
}
