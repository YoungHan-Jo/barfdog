package com.bi.barfdog.api.reviewDto;

import com.bi.barfdog.domain.review.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryReviewsDto {

    private Long id;
    private String thumbnailUrl;
    private String title;
    private int star;
    private String contents;
    private LocalDate createdDate;
    private String imageUrl;
    private int imageCount;
    private ReviewStatus status; // [REQUEST,RETURN,APPROVAL,ADMIN]
    private String returnReason;

}
