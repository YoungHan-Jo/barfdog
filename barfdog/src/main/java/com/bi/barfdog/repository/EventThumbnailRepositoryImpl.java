package com.bi.barfdog.repository;

import com.bi.barfdog.domain.event.QEventThumbnail;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.bi.barfdog.domain.event.QEventThumbnail.*;

@RequiredArgsConstructor
@Repository
public class EventThumbnailRepositoryImpl implements EventThumbnailRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public String findFilenameByEvent(Long eventId) {
        return queryFactory
                .select(eventThumbnail.filename)
                .from(eventThumbnail)
                .where(eventThumbnail.event.id.eq(eventId))
                .fetchOne()
                ;
    }
}
