package com.bi.barfdog.repository.event;

import com.bi.barfdog.domain.event.EventThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventThumbnailRepository extends JpaRepository<EventThumbnail,Long>, EventThumbnailRepositoryCustom {
}
