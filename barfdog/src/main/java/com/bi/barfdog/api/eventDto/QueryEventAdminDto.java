package com.bi.barfdog.api.eventDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryEventAdminDto {

    private EventAdminDto eventAdminDto;

    private List<EventImageDto> eventImageDtoList = new ArrayList();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EventAdminDto {

        private Long eventId;

        private EventStatus status;

        private String title;

        private Long thumbnailId;
        private String filename;
        private String url;

        public void changeUrl() {
            this.url = linkTo(InfoController.class).slash("display").slash("events?filename=" + filename).toString();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EventImageDto{

        private Long id;

        private int leakOrder;

        private String filename;

        private String url;

        public void changeUrl() {
            this.url = linkTo(InfoController.class).slash("display").slash("events?filename=" + filename).toString();
        }

    }


}
