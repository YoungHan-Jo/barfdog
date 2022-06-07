package com.bi.barfdog.api.eventDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryEventDto {

    private EventDto eventDto;

    @Builder.Default
    private List<String> imageUrlList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class EventDto{
        private Long id;
        private String title;
        private LocalDateTime createdDate;
    }
}
