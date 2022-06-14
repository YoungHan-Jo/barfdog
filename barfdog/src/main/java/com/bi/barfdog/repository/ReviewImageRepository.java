package com.bi.barfdog.repository;

import com.bi.barfdog.domain.review.Review;
import com.bi.barfdog.domain.review.ReviewImage;
import com.bi.barfdog.repository.review.ReviewImageRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long>, ReviewImageRepositoryCustom {
    List<ReviewImage> findByReview(Review review);

}
