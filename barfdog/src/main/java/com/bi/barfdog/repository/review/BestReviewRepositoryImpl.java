package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.reviewDto.QueryAdminBestReviewsDto;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.review.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.domain.review.QBestReview.*;
import static com.bi.barfdog.domain.review.QItemReview.*;
import static com.bi.barfdog.domain.review.QReview.*;
import static com.bi.barfdog.domain.review.QSubscribeReview.*;

@RequiredArgsConstructor
@Repository
public class BestReviewRepositoryImpl implements BestReviewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public int findNextLeakedOrder() {
        Integer result = queryFactory
                .select(new CaseBuilder()
                        .when(bestReview.leakedOrder.max().isNull()).then(1)
                        .otherwise(bestReview.leakedOrder.max().add(1))
                )
                .from(bestReview)
                .fetchOne();
        return result;
    }

    @Override
    public List<QueryAdminBestReviewsDto> findAdminBestReviewsDto() {
        List<Tuple> tuples = queryFactory
                .select(bestReview.id, bestReview.leakedOrder, review)
                .from(bestReview)
                .join(bestReview.review, review)
                .leftJoin(itemReview).on(itemReview.eq(review))
                .leftJoin(subscribeReview).on(subscribeReview.eq(review))
                .orderBy(bestReview.leakedOrder.asc())
                .fetch();
        List<QueryAdminBestReviewsDto> result = new ArrayList<>();

        for (Tuple tuple : tuples) {
            Long id = tuple.get(bestReview.id);
            int leakedOrder = tuple.get(bestReview.leakedOrder);
            Review review = tuple.get(QReview.review);
            if (review instanceof ItemReview) {
                ItemReview itemreview = (ItemReview) review;
                Item item = itemreview.getItem();

                QueryAdminBestReviewsDto dto = QueryAdminBestReviewsDto.builder()
                        .id(id)
                        .leakedOrder(leakedOrder)
                        .reviewId(review.getId())
                        .title(item.getName())
                        .star(review.getStar())
                        .contents(review.getContents())
                        .createdDate(review.getWrittenDate())
                        .name(review.getUsername())
                        .email(review.getMember().getEmail())
                        .build();
                result.add(dto);
            }

            if (review instanceof SubscribeReview) {

                QueryAdminBestReviewsDto dto = QueryAdminBestReviewsDto.builder()
                        .id(id)
                        .leakedOrder(leakedOrder)
                        .reviewId(review.getId())
                        .title("구독 상품")
                        .star(review.getStar())
                        .contents(review.getContents())
                        .createdDate(review.getWrittenDate())
                        .name(review.getUsername())
                        .email(review.getMember().getEmail())
                        .build();
                result.add(dto);
            }
        }

        return result;
    }
}
