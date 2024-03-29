package com.bi.barfdog.repository.event;

import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.domain.event.EventImage;
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
                .orderBy(eventImage.leakOrder.asc())
                .fetch()
                ;
    }

    @Override
    public void deleteByIdList(List<Long> deleteImageIdList) {
        queryFactory
                .delete(eventImage)
                .where(eventImage.id.in(deleteImageIdList)).execute();

    }

    @Override
    public List<EventImage> findByEventId(Long id) {
        return queryFactory
                .selectFrom(eventImage)
                .where(eventImage.event.id.eq(id))
                .orderBy(eventImage.leakOrder.asc())
                .fetch();
    }

    @Override
    public List<String> findFilename() {
       return queryFactory
               .select(eventImage.filename)
               .from(eventImage)
               .fetch()
               ;

    }

}
