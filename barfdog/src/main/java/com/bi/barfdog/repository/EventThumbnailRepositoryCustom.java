package com.bi.barfdog.repository;

import com.bi.barfdog.domain.event.EventThumbnail;

import java.util.Optional;

public interface EventThumbnailRepositoryCustom {
    String findFilenameByEventId(Long eventId);

    Optional<EventThumbnail> findByEventId(Long id);
    

}
