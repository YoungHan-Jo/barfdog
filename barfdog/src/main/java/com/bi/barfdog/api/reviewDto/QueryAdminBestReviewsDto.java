package com.bi.barfdog.api.reviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminBestReviewsDto {

    private Long id;
    private int leakedOrder;
    private Long reviewId;
    private String title;
    private int star;
    private String contents;
    private LocalDate createdDate;
    private String name;
    private String email;

}
