package com.bi.barfdog.repository.event;

import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.api.eventDto.QueryEventDto;
import com.bi.barfdog.api.eventDto.QueryEventsAdminDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.bi.barfdog.api.eventDto.QueryEventAdminDto.*;

public interface EventRepositoryCustom {
    Page<QueryEventsAdminDto> findAdminEventsDtoList(Pageable pageable);

    EventAdminDto findEventAdminDto(Long id);

    QueryEventDto findEventDto(Long id);
}
