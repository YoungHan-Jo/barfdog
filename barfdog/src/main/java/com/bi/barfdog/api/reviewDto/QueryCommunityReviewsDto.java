package com.bi.barfdog.api.reviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryCommunityReviewsDto {

    private ReviewDto reviewDto;
    @Builder.Default
    private List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @Builder
    public static class ReviewDto{
        private Long id;
        private String thumbnailUrl;
        private int star;
        private String contents;
        private String username;
        private LocalDate writtenDate;
    }

    @Data
    @AllArgsConstructor
    public static class ReviewImageDto{
        private String filename;
        private String url;
    }
}
