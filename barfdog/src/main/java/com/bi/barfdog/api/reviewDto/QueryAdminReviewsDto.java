package com.bi.barfdog.api.reviewDto;

import com.bi.barfdog.domain.review.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminReviewsDto {

    private Long id;
    private ReviewStatus status; // [REQUEST,RETURN,APPROVAL,ADMIN]
    private String title;
    private int star;
    private String contents;
    private LocalDate createdDate;
    private String name;
    private String email;

}
