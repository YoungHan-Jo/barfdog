package com.bi.barfdog.api.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSheetGeneralRequestDto {

    @Valid
    @Builder.Default
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemDto{
        @Valid
        private ItemDto itemDto;
        @Valid
        @Builder.Default
        private List<ItemOptionDto> itemOptionDtoList = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemDto {
        private Long itemId;
        private int amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemOptionDto {
        private Long itemOptionId;
        private int amount;
    }

}
