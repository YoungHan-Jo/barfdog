package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.reviewDto.QueryReviewImagesDto;
import com.bi.barfdog.domain.review.Review;

import java.util.List;

public interface ReviewImageRepositoryCustom {
    List<QueryReviewImagesDto> findImagesDtoByReview(Review review);

    List<String> findFilename();
}
