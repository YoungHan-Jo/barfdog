package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.reviewDto.QueryWriteableReviewsDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>,ReviewRepositoryCustom {

}
