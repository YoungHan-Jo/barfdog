package com.bi.barfdog.api.reviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalReviewsRequestDto {

    @Builder.Default
    private List<Long> reviewIdList = new ArrayList<>();
}
