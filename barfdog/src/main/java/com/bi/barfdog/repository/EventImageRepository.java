package com.bi.barfdog.repository;

import com.bi.barfdog.domain.event.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventImageRepository extends JpaRepository<EventImage, Long>, EventImageRepositoryCustom {
}
