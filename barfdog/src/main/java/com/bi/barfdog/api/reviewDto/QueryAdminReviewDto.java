package com.bi.barfdog.api.reviewDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.review.ReviewStatus;
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
public class QueryAdminReviewDto {

    private ReviewDto reviewDto;

    private boolean isBestReview;

    @Builder.Default
    private List<ImageUrl> imageUrlList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class ReviewDto {

        private Long id;

        private ReviewStatus status;

        private LocalDate writtenDate;

        private int star;

        private String username;

        private String contents;
    }

    @Data
    @AllArgsConstructor
    public static class ImageUrl {
        private String filename;
        private String url;

        public void changeUrl(String filename) {
            this.url = linkTo(InfoController.class).slash("display/reviews?filename=" + filename).toString();
        }
    }




}
