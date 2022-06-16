package com.bi.barfdog.repository.review;

import com.bi.barfdog.domain.review.BestReview;
import com.bi.barfdog.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BestReviewRepository extends JpaRepository<BestReview, Long>, BestReviewRepositoryCustom {

    List<BestReview> findAllByOrderByLeakedOrderAsc();

    Optional<BestReview> findByReview(Review review);

    Optional<BestReview> findByLeakedOrder(int i);

    @Modifying(clearAutomatically = true)
    @Query("update BestReview b set b.leakedOrder = b.leakedOrder -1 where b.leakedOrder > :leakedOrder")
    void increaseLeakedOrder(@Param("leakedOrder") int leakedOrder);
}
