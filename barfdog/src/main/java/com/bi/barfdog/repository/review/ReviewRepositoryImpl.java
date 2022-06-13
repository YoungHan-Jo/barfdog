package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.reviewDto.QueryReviewsDto;
import com.bi.barfdog.api.reviewDto.QueryWriteableReviewsDto;
import com.bi.barfdog.api.reviewDto.ReviewType;
import com.bi.barfdog.domain.dog.QDog;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.QItem;
import com.bi.barfdog.domain.item.QItemImage;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.QRecipe;
import com.bi.barfdog.domain.review.QItemReview;
import com.bi.barfdog.domain.review.QReview;
import com.bi.barfdog.domain.review.QSubscribeReview;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bi.barfdog.domain.dog.QDog.*;
import static com.bi.barfdog.domain.item.QItem.*;
import static com.bi.barfdog.domain.item.QItemImage.*;
import static com.bi.barfdog.domain.recipe.QRecipe.*;
import static com.bi.barfdog.domain.review.QItemReview.*;
import static com.bi.barfdog.domain.review.QReview.*;
import static com.bi.barfdog.domain.review.QSubscribeReview.*;

@RequiredArgsConstructor
@Repository
public class ReviewRepositoryImpl implements ReviewRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Page<QueryWriteableReviewsDto> findWriteableReviewDto(Member member, Pageable pageable) {

        String sql = "" +
                "SELECT *\n" +
                "FROM(\n" +
                "\t\tSELECT oi.order_item_id AS id, i.item_id AS targetId, 'ITEM' AS reviewType, ii.filename AS imageUrl, i.name AS title, oi.created_date AS orderedDate\n" +
                "\t\tFROM orders o\n" +
                "\t\tJOIN order_item oi\n" +
                "\t\tON o.order_id=oi.order_id\n" +
                "\t\tJOIN item i\n" +
                "\t\tON oi.item_id=i.item_id\n" +
                "\t\tJOIN item_image ii\n" +
                "\t\tON i.item_id=ii.item_id\n" +
                "\t\tWHERE o.member_id = " + member.getId() +
                "\t\tAND oi.writeable_review = true" +
                "\t\tAND oi.status = 'CONFIRM'" +
                "\t\tAND ii.leak_order = 1\n" +
                "\t\tUNION\n" +
                "\t\tSELECT s.subscribe_id AS id, r.recipe_id AS targetId, 'SUBSCRIBE' AS reviewType, r.filename1 AS imageUrl, '구독상품' AS title, o.created_date AS orderedDate\n" +
                "\t\tFROM orders o\n" +
                "\t\tJOIN subscribe s\n" +
                "\t\tON o.order_id = s.order_id\n" +
                "\t\tJOIN dog d\n" +
                "\t\tON d.subscribe_id = s.subscribe_id\n" +
                "\t\tJOIN recipe r\n" +
                "\t\tON r.recipe_id = d.recipe_id\n" +
                "\t\tWHERE d.member_id =" + member.getId() +
                "\t\tAND s.writeable_review = true\n" +
                "\t\tAND s.status = 'SUBSCRIBING'" +
                ") AS sub\n" +
                "ORDER BY sub.orderedDate DESC\n" +
                "LIMIT " + pageable.getOffset() + "," + pageable.getPageSize() ;

        List<Object[]> resultList = em.createNativeQuery(sql).getResultList();

        List<QueryWriteableReviewsDto> result = resultList.stream().map(product -> new QueryWriteableReviewsDto(
                ((BigInteger) product[0]).longValue(),
                ((BigInteger) product[1]).longValue(),
                ReviewType.valueOf(product[2].toString()),
                product[3].toString(),
                product[4].toString(),
                product[5].toString()
        )).collect(Collectors.toList());

        for (QueryWriteableReviewsDto dto : result) {
            dto.changeUrl(dto.getImageUrl());
        }

        String countSql = "" +
                "SELECT count(*) as count\n" +
                "FROM(\n" +
                "\t\tSELECT oi.order_item_id AS id, i.item_id AS targetId, 'ITEM' AS reviewType, ii.filename AS imageUrl, i.name AS title, oi.created_date AS orderedDate\n" +
                "\t\tFROM orders o\n" +
                "\t\tJOIN order_item oi\n" +
                "\t\tON o.order_id=oi.order_id\n" +
                "\t\tJOIN item i\n" +
                "\t\tON oi.item_id=i.item_id\n" +
                "\t\tJOIN item_image ii\n" +
                "\t\tON i.item_id=ii.item_id\n" +
                "\t\tWHERE o.member_id = " + member.getId() +
                "\t\tAND oi.writeable_review = true" +
                "\t\tAND oi.status = 'CONFIRM'" +
                "\t\tAND ii.leak_order = 1\n" +
                "\t\tUNION\n" +
                "\t\tSELECT s.subscribe_id AS id, r.recipe_id AS targetId, 'SUBSCRIBE' AS reviewType, r.filename1 AS imageUrl, '구독상품' AS title, o.created_date AS orderedDate\n" +
                "\t\tFROM subscribe s\n" +
                "\t\tJOIN dog d\n" +
                "\t\tON d.subscribe_id = s.subscribe_id\n" +
                "\t\tJOIN recipe r\n" +
                "\t\tON d.recipe_id = r.recipe_id\n" +
                "\t\tJOIN subscribe_order so\n" +
                "\t\tON so.subscribe_id = s.subscribe_id\n" +
                "\t\tJOIN orders o\n" +
                "\t\tON so.order_id = o.order_id\n" +
                "\t\tWHERE d.member_id = " + member.getId() +
                "\t\tAND s.writeable_review = TRUE\n" +
                "\t\tAND s.status = 'SUBSCRIBING'" +
                ")\n";

        Object singleResult = em.createNativeQuery(countSql).getSingleResult();
        BigInteger bigInt = (BigInteger) singleResult;
        long totalCount = bigInt.longValue();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public Page<QueryReviewsDto> findReviewsDtoByMember(Pageable pageable, Member member) {

        List<Tuple> tuples = queryFactory
                .select(review, itemReview, subscribeReview)
                .from(review)
                .leftJoin(itemReview).on(review.id.eq(itemReview.id))
                .leftJoin(subscribeReview).on(review.id.eq(subscribeReview.id))
                .where(review.member.eq(member))
                .orderBy(review.writtenDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<QueryReviewsDto> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Item item = tuple.get(itemReview.item);
            if (item != null) {
                
                QueryReviewsDto queryReviewsDto = QueryReviewsDto.builder()
                        .id(tuple.get(itemReview.id))
                        .thumbnailUrl("")
                        .title(tuple.get(itemReview.item.name))
                        .star(tuple.get(itemReview.star))
                        .contents(tuple.get(itemReview.contents))
                        .createdDate(tuple.get(itemReview.writtenDate))
                        .imageUrl("")
                        .imageCount(1L)
                        .status(tuple.get(itemReview.status))
                        .build();
                result.add(queryReviewsDto);
            } else {
                QueryReviewsDto queryReviewsDto = QueryReviewsDto.builder()
                        .id(tuple.get(subscribeReview.id))
                        .thumbnailUrl("")
                        .title("구독상품")
                        .star(tuple.get(subscribeReview.star))
                        .contents(tuple.get(subscribeReview.contents))
                        .createdDate(tuple.get(subscribeReview.writtenDate))
                        .imageUrl("")
                        .imageCount(1L)
                        .status(tuple.get(subscribeReview.status))
                        .build();
                result.add(queryReviewsDto);
            }


        }


        Long totalCount = queryFactory
                .select(review.count())
                .from(review)
                .fetchOne();

        return new PageImpl<>(result,pageable,totalCount);
    }


}
