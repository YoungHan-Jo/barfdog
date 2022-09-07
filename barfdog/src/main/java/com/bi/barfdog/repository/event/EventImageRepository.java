package com.bi.barfdog.repository.event;

import com.bi.barfdog.domain.event.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventImageRepository extends JpaRepository<EventImage, Long>, EventImageRepositoryCustom {

}
