package com.bi.barfdog.repository.review;

import com.bi.barfdog.domain.review.ItemReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemReviewRepository extends JpaRepository<ItemReview, Long> {
}
