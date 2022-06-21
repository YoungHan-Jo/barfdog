package com.bi.barfdog.api.reviewDto;

import com.bi.barfdog.api.InfoController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryCommunityReviewDto {

    private ReviewDto reviewDto;

    @Builder.Default
    private List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class ReviewImageDto {
        private String filename;
        private String url;

        public void changeUrl(String filename) {
            url = linkTo(InfoController.class).slash("display/reviews?filename=" + filename).toString();
        }
    }

    @Data
    @AllArgsConstructor
    public static class ReviewDto {
        private Long id;
        private LocalDate writtenDate;
        private int star;
        private String username;
        private String contents;
    }

}
