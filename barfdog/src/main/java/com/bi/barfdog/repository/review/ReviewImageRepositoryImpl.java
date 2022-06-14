package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.reviewDto.QueryReviewImagesDto;
import com.bi.barfdog.domain.review.QReview;
import com.bi.barfdog.domain.review.QReviewImage;
import com.bi.barfdog.domain.review.Review;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.review.QReviewImage.reviewImage;

@RequiredArgsConstructor
@Repository
public class ReviewImageRepositoryImpl implements ReviewImageRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QueryReviewImagesDto> findImagesDtoByReview(Review review) {
        List<QueryReviewImagesDto> result = queryFactory
                .select(Projections.constructor(QueryReviewImagesDto.class,
                        reviewImage.filename,
                        reviewImage.filename
                ))
                .from(reviewImage)
                .where(reviewImage.review.eq(review))
                .fetch();
        for (QueryReviewImagesDto dto : result) {
            dto.changeUrl(dto.getFilename());
        }

        return result;
    }
}
