package com.bi.barfdog.repository.guest;

import com.bi.barfdog.api.guestDto.QueryAdminGuestDto;
import com.bi.barfdog.api.guestDto.QueryGuestCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GuestRepositoryCustom {
    Page<QueryAdminGuestDto> findAdminGuestDtos(Pageable pageable, QueryGuestCond cond);
}
