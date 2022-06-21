package com.bi.barfdog.api.basketDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveBasketDto {

    @NotNull
    private Long itemId;
    @Positive
    private int itemAmount;

    @Valid
    @Builder.Default
    private List<OptionDto> optionDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OptionDto {
        @NotNull
        private Long optionId;

        @Positive
        private int optionAmount;
    }

}
