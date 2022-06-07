package com.bi.barfdog.api.eventDto;

import com.bi.barfdog.api.IndexApiController;
import com.bi.barfdog.api.InfoController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryEventsDto {

    private Long id;

    private String title;

    private String thumbnailUrl;

    public void setUrl() {
        this.thumbnailUrl = linkTo(InfoController.class).slash("display/events?filename=" + thumbnailUrl).toString();
    }
}
