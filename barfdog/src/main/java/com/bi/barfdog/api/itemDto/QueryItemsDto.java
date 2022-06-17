package com.bi.barfdog.api.itemDto;

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
public class QueryItemsDto {

    private Long id;
    private String thumbnailUrl;
    private String name;
    private int originalPrice; // 원가
    private int salePrice; // 판매가
    private boolean inStock; // 재고 여부

    private double star;
    private Long reviewCount;

    public void changeUrl(String filename) {
        this.thumbnailUrl = linkTo(InfoController.class).slash("display/items?filename=" + filename).toString();
    }
}
