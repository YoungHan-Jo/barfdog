package com.bi.barfdog.api.reviewDto;

import com.bi.barfdog.api.InfoController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryWriteableReviewsDto {

    private Long id; // orderItemId or subscribeId

    private Long targetId; // itemId or recipeId

    private ReviewType reviewType; // [ITEM,SUBSCRIBE]

    private String imageUrl;

    private String title;

    private String orderedDate;

    public void changeUrl(String filename) {
        if (reviewType == ReviewType.ITEM) {
            this.imageUrl = linkTo(InfoController.class).slash("display/items?filename=" + filename).toString();
        } else {
            this.imageUrl = linkTo(InfoController.class).slash("display/recipes?filename=" + filename).toString();
        }
    }
}
