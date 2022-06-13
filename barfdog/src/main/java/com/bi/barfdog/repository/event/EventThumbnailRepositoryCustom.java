package com.bi.barfdog.repository.event;

import com.bi.barfdog.api.eventDto.QueryEventsDto;
import com.bi.barfdog.domain.event.EventThumbnail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventThumbnailRepositoryCustom {
    String findFilenameByEventId(Long eventId);

    Optional<EventThumbnail> findByEventId(Long id);


    Page<QueryEventsDto> findEventDtos(Pageable pageable);
}
