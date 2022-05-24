package com.bi.barfdog.repository;

import com.bi.barfdog.api.eventDto.QueryEventAdminDto;

import java.util.List;

public interface EventImageRepositoryCustom {
    List<QueryEventAdminDto.EventImageDto> findEventImageDtoByEventId(Long id);
}
