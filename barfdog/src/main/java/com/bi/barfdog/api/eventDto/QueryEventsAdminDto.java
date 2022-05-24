package com.bi.barfdog.api.eventDto;

import com.bi.barfdog.domain.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryEventsAdminDto {

    private EventsAdminDto eventsAdminDto;

    private String imageUrl;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EventsAdminDto{
        private Long id;

        private String title;

        private LocalDateTime createdDate;

        private EventStatus status;
    }

}
