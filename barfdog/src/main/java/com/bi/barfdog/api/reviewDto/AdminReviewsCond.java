package com.bi.barfdog.api.reviewDto;

import com.bi.barfdog.domain.review.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminReviewsCond {

    private ReviewStatus status; // [ALL,REQUEST,RETURN,APPROVAL,ADMIN]
    private String order; // [asc, desc]

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

}
