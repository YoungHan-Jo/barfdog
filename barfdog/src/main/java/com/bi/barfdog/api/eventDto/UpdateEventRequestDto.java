package com.bi.barfdog.api.eventDto;

import com.bi.barfdog.domain.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventRequestDto {

    @NotNull
    private EventStatus status; // [LEAKED, HIDDEN]
    @NotEmpty
    private String title;
    @NotNull
    private Long thumbnailId;

    @Builder.Default
    private List<Long> addImageIdList = new ArrayList<>();
    @Builder.Default
    private List<Long> deleteImageIdList = new ArrayList<>();

    @NotNull
    @Valid
    private List<ImageOrderDto> imageOrderDtoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ImageOrderDto {

        @NotNull
        private Long id;

        @NotNull
        @Positive
        private int leakOrder;

    }

}
