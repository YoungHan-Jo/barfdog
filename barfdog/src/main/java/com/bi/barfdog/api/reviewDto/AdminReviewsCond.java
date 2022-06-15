package com.bi.barfdog.api.reviewDto;

import com.bi.barfdog.domain.review.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminReviewsCond {

    private ReviewStatus status; // [ALL,REQUEST,RETURN,APPROVAL,ADMIN]
    private String order; // [asc, desc]

}
