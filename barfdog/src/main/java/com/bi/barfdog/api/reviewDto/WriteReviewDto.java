package com.bi.barfdog.api.reviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WriteReviewDto {

    @NotNull
    private ReviewType reviewType; // [ITEM,SUBSCRIBE]

    @NotNull
    private Long id; // orderItemId or subscribeId
    @NotNull
    private Long targetId; // itemId or recipeId
    @NotNull
    @Min(1)
    @Max(5)
    private int star;
    @NotEmpty
    @Size(min = 10)
    private String contents;
    @Builder.Default
    private List<Long> reviewImageIdList = new ArrayList<>();

}
