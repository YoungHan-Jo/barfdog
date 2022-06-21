package com.bi.barfdog.api.reviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryBestReviewsDto {

    private Long id;
    private String imageUrl;
    private int leakedOrder;
    private String contents;

}
