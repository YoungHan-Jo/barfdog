package com.bi.barfdog.repository.review;

import com.bi.barfdog.domain.review.Review;
import com.bi.barfdog.domain.review.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>,ReviewRepositoryCustom {

    List<Review> findAllByStatus(ReviewStatus approval);

}
