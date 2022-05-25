package com.bi.barfdog.repository;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.eventDto.QueryEventAdminDto;
import com.bi.barfdog.api.eventDto.QueryEventsAdminDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.api.eventDto.QueryEventsAdminDto.*;
import static com.bi.barfdog.domain.event.QEvent.*;
import static com.bi.barfdog.domain.event.QEventThumbnail.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Repository
public class EventRepositoryImpl implements EventRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EventThumbnailRepository eventThumbnailRepository;


    @Override
    public Page<QueryEventsAdminDto> findAdminEventsDtoList(Pageable pageable) {
        List<EventsAdminDto> eventsAdminDtoList = queryFactory
                .select(Projections.constructor(EventsAdminDto.class,
                        event.id,
                        event.title,
                        event.createdDate,
                        event.status
                ))
                .from(event)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(event.id.desc())
                .fetch();

        List<QueryEventsAdminDto> result = getQueryEventsAdminDtos(eventsAdminDtoList);

        Long totalCount = queryFactory
                .select(event.count())
                .from(event)
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public QueryEventAdminDto.EventAdminDto findEventAdminDto(Long id) {
        return queryFactory
                .select(Projections.constructor(QueryEventAdminDto.EventAdminDto.class,
                        event.id,
                        event.status,
                        event.title,
                        eventThumbnail.id,
                        eventThumbnail.filename,
                        eventThumbnail.filename
                        ))
                .from(eventThumbnail)
                .join(eventThumbnail.event, event)
                .where(event.id.eq(id))
                .fetchOne();
    }


    private List<QueryEventsAdminDto> getQueryEventsAdminDtos(List<EventsAdminDto> eventsAdminDtoList) {
        List<QueryEventsAdminDto> result = new ArrayList<>();

        for (EventsAdminDto eventsAdminDto : eventsAdminDtoList) {

            String filename = eventThumbnailRepository.findFilenameByEventId(eventsAdminDto.getId());

            String url = linkTo(InfoController.class).slash("events").slash("blogs?filename=" + filename).toString();

            QueryEventsAdminDto queryEventsAdminDto = builder()
                    .eventsAdminDto(eventsAdminDto)
                    .imageUrl(url)
                    .build();
            result.add(queryEventsAdminDto);
        }
        return result;
    }
}
