package com.bi.barfdog.api.eventDto;

import com.bi.barfdog.domain.blog.BlogCategory;
import com.bi.barfdog.domain.blog.BlogStatus;
import com.bi.barfdog.domain.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventSaveDto {

    @NotNull
    private EventStatus status;

    @NotEmpty
    private String title;

    @NotNull
    private Long thumbnailId;

    @Builder.Default
    @Size(min = 1)
    @NotNull
    @Valid
    private List<EventImageRequestDto> eventImageRequestDtoList = new ArrayList<>();

    /*
     * 내부 클래스
     * */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EventImageRequestDto {

        @NotNull
        private Long id;

        @NotNull
        @Positive
        private int leakOrder;
    }



}
