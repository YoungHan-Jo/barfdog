package com.bi.barfdog.repository;

import com.bi.barfdog.domain.event.EventThumbnail;
import com.bi.barfdog.domain.event.QEventThumbnail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}
