package com.bi.barfdog.repository.event;

import com.bi.barfdog.api.eventDto.QueryEventsDto;
import com.bi.barfdog.domain.event.EventStatus;
import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.domain.event.QEventThumbnail;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.bi.barfdog.domain.event.QEvent.*;
import static com.bi.barfdog.domain.event.QEventThumbnail.*;

@RequiredArgsConstructor
@Repository
public class EventThumbnailRepositoryImpl implements EventThumbnailRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public String findFilenameByEventId(Long eventId) {
        return queryFactory
                .select(eventThumbnail.filename)
                .from(eventThumbnail)
                .where(eventThumbnail.event.id.eq(eventId))
                .fetchOne()
                ;
    }

    @Override
    public Optional<EventThumbnail> findByEventId(Long id) {
        EventThumbnail eventThumbnail = queryFactory
                .selectFrom(QEventThumbnail.eventThumbnail)
                .where(QEventThumbnail.eventThumbnail.event.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(eventThumbnail);
    }

    @Override
    public Page<QueryEventsDto> findEventDtos(Pageable pageable) {
        List<QueryEventsDto> result = queryFactory
                .select(Projections.constructor(QueryEventsDto.class,
                        event.id,
                        event.title,
                        eventThumbnail.filename
                ))
                .from(eventThumbnail)
                .join(eventThumbnail.event, event)
                .where(event.status.eq(EventStatus.LEAKED))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(event.createdDate.desc())
                .fetch();

        for (QueryEventsDto queryEventsDto : result) {
            queryEventsDto.setUrl();
        }

        Long totalCount = queryFactory
                .select(event.count())
                .from(event)
                .where(event.status.eq(EventStatus.LEAKED))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }
}
