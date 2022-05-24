package com.bi.barfdog.repository;

import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.domain.event.QEventImage;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.event.QEventImage.*;

@RequiredArgsConstructor
@Repository
public class EventImageRepositoryImpl implements EventImageRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QueryEventAdminDto.EventImageDto> findEventImageDtoByEventId(Long id) {
        return queryFactory
                .select(Projections.constructor(QueryEventAdminDto.EventImageDto.class,
                        eventImage.id,
                        eventImage.leakOrder,
                        eventImage.filename,
                        eventImage.filename
                        ))
                .from(eventImage)
                .where(eventImage.event.id.eq(id))
                .fetch()
                ;
    }
}
