package com.bi.barfdog.api.reviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBestReviewLeakedOrderDto {

    @Valid
    @Builder.Default
    private List<LeakedOrderDto> leakedOrderDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LeakedOrderDto {

        @NotNull
        private Long id;

        @NotNull
        private int leakedOrder;
    }
}
