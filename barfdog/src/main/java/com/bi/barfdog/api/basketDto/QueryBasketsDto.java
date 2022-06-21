package com.bi.barfdog.api.basketDto;

import com.bi.barfdog.api.InfoController;
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
public class QueryBasketsDto {

    private ItemDto itemDto;
    @Builder.Default
    private List<ItemOptionDto> itemOptionDtoList = new ArrayList<>();
    private int totalPrice;

    @Data
    @AllArgsConstructor
    public static class ItemDto {
        private Long basketId;
        private Long itemId;
        private String thumbnailUrl;
        private String name;
        private int originalPrice;
        private int salePrice;
        private int amount;
        private boolean deliveryFree;

        public void changeUrl(String filename) {
            thumbnailUrl = linkTo(InfoController.class).slash("display/items?filename=" + filename).toString();
        }
    }

    @Data
    @AllArgsConstructor
    public static class ItemOptionDto {
        private Long id;
        private String name;
        private int optionPrice;
        private int amount;
    }


}
