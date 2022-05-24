package com.bi.barfdog.repository;

import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.api.eventDto.QueryEventsAdminDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.bi.barfdog.api.eventDto.QueryEventAdminDto.*;

public interface EventRepositoryCustom {
    Page<QueryEventsAdminDto> findAdminEventsDtoList(Pageable pageable);

    EventAdminDto findEventAdminDto(Long id);
}
