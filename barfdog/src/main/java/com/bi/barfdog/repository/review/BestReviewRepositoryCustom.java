package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.reviewDto.QueryAdminBestReviewsDto;

import java.util.List;

public interface BestReviewRepositoryCustom {
    int findNextLeakedOrder();

    List<QueryAdminBestReviewsDto> findAdminBestReviewsDto();
}
