package com.bi.barfdog.repository.event;

import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.domain.event.EventImage;

import java.util.List;

public interface EventImageRepositoryCustom {
    List<QueryEventAdminDto.EventImageDto> findEventImageDtoByEventId(Long id);

    void deleteByIdList(List<Long> deleteImageIdList);

    List<EventImage> findByEventId(Long id);
}
