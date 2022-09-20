package com.bi.barfdog.api.deliveryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveDeliveryNumDto {

    private DataDto data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DataDto {
        @Builder.Default
        private List<ItemDto> items = new ArrayList();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemDto {
        @NotNull
        private String uniqueCd; // 고객사용번호
        @NotNull
        private String transUniqueCd; // 배송고유번호
        @NotNull
        private String deliverCode; // 택배사코드
        @NotNull
        private String sheetNo; // 운송장번호
    }

}

