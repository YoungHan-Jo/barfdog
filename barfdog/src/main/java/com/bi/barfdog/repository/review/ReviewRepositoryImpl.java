package com.bi.barfdog.repository.review;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.itemDto.ItemReviewsDto;
import com.bi.barfdog.api.reviewDto.*;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemImage;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.review.*;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bi.barfdog.domain.review.QBestReview.*;
import static com.bi.barfdog.domain.review.QItemReview.itemReview;
import static com.bi.barfdog.domain.review.QReview.review;
import static com.bi.barfdog.domain.review.QReviewImage.*;
import static com.bi.barfdog.domain.review.QSubscribeReview.subscribeReview;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Repository
public class ReviewRepositoryImpl implements ReviewRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    private final ItemImageRepository itemImageRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewImageRepository reviewImageRepository;

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
                "\t\tJoin subscribe_order so\n" +
                "\t\tON o.order_id = so.order_id\n" +
                "\t\tJOIN subscribe s\n" +
                "\t\tON so.subscribe_id = s.subscribe_id\n" +
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

        List<Review> reviews = queryFactory
                .selectFrom(review)
                .leftJoin(itemReview).on(itemReview.eq(review))
                .leftJoin(subscribeReview).on(subscribeReview.eq(review))
                .where(review.member.eq(member))
                .orderBy(review.writtenDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<QueryReviewsDto> result = getQueryReviewsDtos(reviews);

        Long totalCount = queryFactory
                .select(review.count())
                .from(review)
                .where(review.member.eq(member))
                .fetchOne();

        return new PageImpl<>(result,pageable,totalCount);
    }

    @Override
    public QueryReviewDto findReviewDtoById(Long id) {
        List<QueryReviewDto.ReviewImageDto> reviewImageDtoList = getReviewImageDtos(id);

        QueryReviewDto.ReviewDto reviewDto = getReviewDto(id);

        QueryReviewDto result = QueryReviewDto.builder()
                .reviewDto(reviewDto)
                .reviewImageDtoList(reviewImageDtoList)
                .build();

        return result;
    }

    @Override
    public Page<QueryAdminReviewsDto> findAdminReviewsDto(Pageable pageable, AdminReviewsCond cond) {

        OrderSpecifier<LocalDate> orderByCond = cond.getOrder().equals("asc")? review.writtenDate.asc():review.writtenDate.desc();


        List<Review> reviews = queryFactory
                .select(review)
                .from(review)
                .leftJoin(itemReview).on(itemReview.eq(review))
                .leftJoin(subscribeReview).on(subscribeReview.eq(review))
                .where(statusEqCond(cond.getStatus(), cond))
                .orderBy(orderByCond)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<QueryAdminReviewsDto> result = getQueryAdminReviewsDtos(reviews);

        Long totalCount = queryFactory
                .select(review.count())
                .from(review)
                .leftJoin(itemReview).on(itemReview.eq(review))
                .leftJoin(subscribeReview).on(subscribeReview.eq(review))
                .where(statusEqCond(cond.getStatus(), cond))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public QueryAdminReviewDto findAdminReviewDto(Long id) {

        QueryAdminReviewDto.ReviewDto reviewDto = queryFactory
                .select(Projections.constructor(QueryAdminReviewDto.ReviewDto.class,
                        review.id,
                        review.status,
                        review.writtenDate,
                        review.star,
                        review.username,
                        review.contents
                        ))
                .from(review)
                .where(review.id.eq(id))
                .fetchOne();

        List<BestReview> bestReviews = queryFactory
                .select(bestReview)
                .from(bestReview)
                .where(bestReview.review.id.eq(id))
                .fetch();


        List<QueryAdminReviewDto.ImageUrl> imageUrlList = queryFactory
                .select(Projections.constructor(QueryAdminReviewDto.ImageUrl.class,
                        reviewImage.filename,
                        reviewImage.filename
                ))
                .from(reviewImage)
                .where(reviewImage.review.id.eq(id))
                .fetch();
        for (QueryAdminReviewDto.ImageUrl imageUrl : imageUrlList) {
            imageUrl.changeUrl(imageUrl.getFilename());
        }

        QueryAdminReviewDto result = QueryAdminReviewDto.builder()
                .reviewDto(reviewDto)
                .isBestReview(bestReviews.size() > 0 ? true : false)
                .imageUrlList(imageUrlList)
                .build();

        return result;
    }

    @Override
    public Page<ItemReviewsDto> findItemReviewsDtoByItemId(Pageable pageable, Long id) {

        List<ItemReviewsDto.ReviewDto> reviewDtoList = queryFactory
                .select(Projections.constructor(ItemReviewsDto.ReviewDto.class,
                        itemReview.id,
                        itemReview.star,
                        itemReview.contents,
                        itemReview.username,
                        itemReview.writtenDate
                ))
                .from(itemReview)
                .where(itemReview.item.id.eq(id).and(itemReview.status.in(ReviewStatus.APPROVAL, ReviewStatus.ADMIN)))
                .orderBy(itemReview.writtenDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ItemReviewsDto> result = getItemReviewsDtos(reviewDtoList);

        Long totalCount = queryFactory
                .select(itemReview.count())
                .from(itemReview)
                .where(itemReview.item.id.eq(id).and(itemReview.status.in(ReviewStatus.APPROVAL, ReviewStatus.ADMIN)))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public Page<QueryCommunityReviewsDto> findCommunityReviewsDto(Pageable pageable) {
        List<Review> reviews = queryFactory
                .select(review)
                .from(review)
                .leftJoin(itemReview).on(itemReview.eq(review))
                .leftJoin(subscribeReview).on(subscribeReview.eq(review))
                .where(viewable())
                .orderBy(review.writtenDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<QueryCommunityReviewsDto> result = getQueryCommunityReviewsDtos(reviews);

        Long totalCount = queryFactory
                .select(review.count())
                .from(review)
                .where(viewable())
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public QueryCommunityReviewDto findCommunityReviewDtoById(Long id) {

        QueryCommunityReviewDto.ReviewDto reviewDto = queryFactory
                .select(Projections.constructor(QueryCommunityReviewDto.ReviewDto.class,
                        review.id,
                        review.writtenDate,
                        review.star,
                        review.username,
                        review.contents
                ))
                .from(review)
                .where(review.id.eq(id))
                .fetchOne();

        List<QueryCommunityReviewDto.ReviewImageDto> reviewImageDtoList = getReviewImageDtoList(id);

        QueryCommunityReviewDto result = QueryCommunityReviewDto.builder()
                .reviewDto(reviewDto)
                .reviewImageDtoList(reviewImageDtoList)
                .build();
        return result;
    }

    private List<QueryCommunityReviewDto.ReviewImageDto> getReviewImageDtoList(Long id) {
        List<QueryCommunityReviewDto.ReviewImageDto> reviewImageDtoList = queryFactory
                .select(Projections.constructor(QueryCommunityReviewDto.ReviewImageDto.class,
                        reviewImage.filename,
                        reviewImage.filename
                ))
                .from(reviewImage)
                .where(reviewImage.review.id.eq(id))
                .fetch();
        for (QueryCommunityReviewDto.ReviewImageDto dto : reviewImageDtoList) {
            dto.changeUrl(dto.getFilename());
        }
        return reviewImageDtoList;
    }

    private BooleanExpression viewable() {
        return review.status.in(ReviewStatus.APPROVAL, ReviewStatus.ADMIN);
    }

    private List<QueryCommunityReviewsDto> getQueryCommunityReviewsDtos(List<Review> reviews) {
        List<QueryCommunityReviewsDto> result = new ArrayList<>();

        for (Review review : reviews) {
            if (review instanceof ItemReview) {
                ItemReview itemreview = (ItemReview) review;
                Item item = itemreview.getItem();
                String thumbnailUrl = getThumbnailUrl(item);

                QueryCommunityReviewsDto queryCommunityReviewsDto = getQueryCommunityReviewsDto(review, thumbnailUrl);

                result.add(queryCommunityReviewsDto);
            }
            if (review instanceof SubscribeReview) {
                SubscribeReview subscribeReview = (SubscribeReview) review;
                String thumbnailUrl = getThumbnailUrl(subscribeReview);

                QueryCommunityReviewsDto queryCommunityReviewsDto = getQueryCommunityReviewsDto(review, thumbnailUrl);

                result.add(queryCommunityReviewsDto);
            }
        }
        return result;
    }

    private QueryCommunityReviewsDto getQueryCommunityReviewsDto(Review review, String thumbnailUrl) {
        QueryCommunityReviewsDto.ReviewDto reviewDto = getReviewDto(review, thumbnailUrl);

        List<QueryCommunityReviewsDto.ReviewImageDto> reviewImageDtoList = getReviewImageDtoList(review);

        QueryCommunityReviewsDto queryCommunityReviewsDto = QueryCommunityReviewsDto.builder()
                .reviewDto(reviewDto)
                .reviewImageDtoList(reviewImageDtoList)
                .build();
        return queryCommunityReviewsDto;
    }

    private QueryCommunityReviewsDto.ReviewDto getReviewDto(Review review, String thumbnailUrl) {
        QueryCommunityReviewsDto.ReviewDto reviewDto = QueryCommunityReviewsDto.ReviewDto.builder()
                .id(review.getId())
                .thumbnailUrl(thumbnailUrl)
                .star(review.getStar())
                .contents(review.getContents())
                .username(review.getUsername())
                .writtenDate(review.getWrittenDate())
                .build();
        return reviewDto;
    }

    private List<QueryCommunityReviewsDto.ReviewImageDto> getReviewImageDtoList(Review review) {
        List<QueryCommunityReviewsDto.ReviewImageDto> reviewImageDtoList = queryFactory
                .select(Projections.constructor(QueryCommunityReviewsDto.ReviewImageDto.class,
                        reviewImage.filename,
                        reviewImage.filename
                ))
                .from(reviewImage)
                .where(reviewImage.review.eq(review))
                .fetch();
        return reviewImageDtoList;
    }

    private List<ItemReviewsDto> getItemReviewsDtos(List<ItemReviewsDto.ReviewDto> reviewDtoList) {
        List<ItemReviewsDto> result = new ArrayList<>();

        for (ItemReviewsDto.ReviewDto dto : reviewDtoList) {

            List<ItemReviewsDto.ReviewImageDto> reviewImageDtoList = queryFactory
                    .select(Projections.constructor(ItemReviewsDto.ReviewImageDto.class,
                            reviewImage.filename,
                            reviewImage.filename
                    ))
                    .from(reviewImage)
                    .where(reviewImage.review.id.eq(dto.getId()))
                    .fetch();
            for (ItemReviewsDto.ReviewImageDto imageDto : reviewImageDtoList) {
                imageDto.changeUrl(imageDto.getFilename());
            }

            ItemReviewsDto itemReviewsDto = ItemReviewsDto.builder()
                    .reviewDto(dto)
                    .reviewImageDtoList(reviewImageDtoList)
                    .build();
            result.add(itemReviewsDto);
        }
        return result;
    }

    private List<QueryAdminReviewsDto> getQueryAdminReviewsDtos(List<Review> reviews) {
        List<QueryAdminReviewsDto> result = new ArrayList<>();

        for (Review review : reviews) {

            if (review instanceof ItemReview) {
                ItemReview itemreview = (ItemReview) review;
                Item item = itemreview.getItem();

                QueryAdminReviewsDto dto = QueryAdminReviewsDto.builder()
                        .id(review.getId())
                        .status(review.getStatus())
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

                QueryAdminReviewsDto dto = QueryAdminReviewsDto.builder()
                        .id(review.getId())
                        .status(review.getStatus())
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

    private BooleanExpression statusEqCond(ReviewStatus status, AdminReviewsCond cond) {
        switch (status) {
            case ADMIN:
                return review.status.eq(ReviewStatus.ADMIN).and(review.writtenDate.between(cond.getFrom(),cond.getTo()));
            case RETURN:
                return review.status.eq(ReviewStatus.RETURN).and(review.writtenDate.between(cond.getFrom(),cond.getTo()));
            case REQUEST:
                return review.status.eq(ReviewStatus.REQUEST).and(review.writtenDate.between(cond.getFrom(),cond.getTo()));
            case APPROVAL:
                return review.status.eq(ReviewStatus.APPROVAL).and(review.writtenDate.between(cond.getFrom(),cond.getTo()));
            default:
                return review.writtenDate.between(cond.getFrom(),cond.getTo());
        }
    }

    private List<QueryReviewDto.ReviewImageDto> getReviewImageDtos(Long id) {
        List<QueryReviewDto.ReviewImageDto> reviewImageDtoList = queryFactory
                .select(Projections.constructor(QueryReviewDto.ReviewImageDto.class,
                        reviewImage.id,
                        reviewImage.filename,
                        reviewImage.filename
                        ))
                .from(reviewImage)
                .where(reviewImage.review.id.eq(id))
                .fetch();
        for (QueryReviewDto.ReviewImageDto dto : reviewImageDtoList) {
            dto.changeUrl(dto.getFilename());
        }
        return reviewImageDtoList;
    }

    private QueryReviewDto.ReviewDto getReviewDto(Long id) {
        Review review = queryFactory
                .selectFrom(QReview.review)
                .where(QReview.review.id.eq(id))
                .fetchOne();

        QueryReviewDto.ReviewDto reviewDto = QueryReviewDto.ReviewDto.builder().build();

        if (review instanceof ItemReview) {
            ItemReview itemReview = (ItemReview) review;
            Item item = itemReview.getItem();
            String thumbnailUrl = getThumbnailUrl(item);

            reviewDto = QueryReviewDto.ReviewDto.builder()
                    .id(review.getId())
                    .title(item.getName())
                    .name(review.getUsername())
                    .thumbnailUrl(thumbnailUrl)
                    .writtenDate(review.getWrittenDate())
                    .star(review.getStar())
                    .contents(review.getContents())
                    .build();
        }
        if (review instanceof SubscribeReview) {
            SubscribeReview subscribeReview = (SubscribeReview) review;
            String thumbnailUrl = getThumbnailUrl(subscribeReview);

            reviewDto = QueryReviewDto.ReviewDto.builder()
                    .id(review.getId())
                    .title("구독 상품")
                    .name(review.getUsername())
                    .thumbnailUrl(thumbnailUrl)
                    .writtenDate(review.getWrittenDate())
                    .star(review.getStar())
                    .contents(review.getContents())
                    .build();
        }
        return reviewDto;
    }

    private List<QueryReviewsDto> getQueryReviewsDtos(List<Review> reviews) {
        List<QueryReviewsDto> result = new ArrayList<>();

        for (Review review : reviews) {
            List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
            int imageCount = reviewImages.size();
            String imageUrl = "";
            imageUrl = getReviewImageUrl(reviewImages, imageCount, imageUrl);

            if (review instanceof ItemReview) {
                ItemReview itemreview = (ItemReview) review;
                Item item = itemreview.getItem();
                String thumbnailUrl = getThumbnailUrl(item);

                QueryReviewsDto queryReviewsDto = QueryReviewsDto.builder()
                        .id(review.getId())
                        .thumbnailUrl(thumbnailUrl)
                        .title(item.getName())
                        .reviewType(ReviewType.ITEM)
                        .star(review.getStar())
                        .contents(review.getContents())
                        .createdDate(review.getWrittenDate())
                        .imageUrl(imageUrl)
                        .imageCount(imageCount)
                        .status(review.getStatus())
                        .returnReason(review.getReturnReason())
                        .build();
                result.add(queryReviewsDto);

            }
            if (review instanceof SubscribeReview) {
                SubscribeReview subscribeReview = (SubscribeReview) review;
                String thumbnailUrl = getThumbnailUrl(subscribeReview);

                QueryReviewsDto queryReviewsDto = QueryReviewsDto.builder()
                        .id(review.getId())
                        .thumbnailUrl(thumbnailUrl)
                        .title("구독 상품")
                        .reviewType(ReviewType.SUBSCRIBE)
                        .star(review.getStar())
                        .contents(review.getContents())
                        .createdDate(review.getWrittenDate())
                        .imageUrl(imageUrl)
                        .imageCount(imageCount)
                        .status(review.getStatus())
                        .returnReason(review.getReturnReason())
                        .build();
                result.add(queryReviewsDto);
            }
        }
        return result;
    }

    private String getThumbnailUrl(Item item) {
        String thumbnailUrl = "";
        List<ItemImage> itemImages = itemImageRepository.findByItemOrderByLeakOrderAsc(item);
        if (itemImages.size() > 0) {
            ItemImage itemImage = itemImages.get(0);
            String filename = itemImage.getFilename();
            thumbnailUrl = linkTo(InfoController.class).slash("display/items?filename=" + filename).toString();
        }
        return thumbnailUrl;
    }

    private String getThumbnailUrl(SubscribeReview subscribeReview) {
        Recipe recipe = subscribeReview.getSubscribe().getDog().getRecommendRecipe();
        String filename = recipe.getThumbnailImage().getFilename1();
        String thumbnailUrl = linkTo(InfoController.class).slash("display/recipes?filename=" + filename).toString();
        return thumbnailUrl;
    }

    private String getReviewImageUrl(List<ReviewImage> reviewImages, int imageCount, String imageUrl) {
        if (imageCount > 0) {
            ReviewImage reviewImage = reviewImages.get(0);
            String filename = reviewImage.getFilename();
            imageUrl = linkTo(InfoController.class).slash("display/reviews?filename=" + filename).toString();
        }
        return imageUrl;
    }


}
