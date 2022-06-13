package com.bi.barfdog.repository;

import com.bi.barfdog.domain.review.Review;
import com.bi.barfdog.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReview(Review review);
}
