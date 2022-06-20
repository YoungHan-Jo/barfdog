package com.bi.barfdog.validator;

import com.bi.barfdog.api.reviewDto.ReviewType;
import com.bi.barfdog.api.reviewDto.UpdateBestReviewLeakedOrderDto;
import com.bi.barfdog.api.reviewDto.WriteReviewDto;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.review.BestReview;
import com.bi.barfdog.domain.review.Review;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.review.BestReviewRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ReviewValidator {

    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final SubscribeRepository subscribeRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final BestReviewRepository bestReviewRepository;

    public void validatorId(WriteReviewDto requestDto, Errors errors) {
        ReviewType reviewType = requestDto.getReviewType();
        if (reviewType == ReviewType.ITEM) {
            Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(requestDto.getId());
            if (!optionalOrderItem.isPresent()) {
                errors.reject("wrong id","존재하지않는 id 입니다.");
            }

            Optional<Item> optionalItem = itemRepository.findById(requestDto.getTargetId());
            if (!optionalItem.isPresent()) {
                errors.reject("wrong target id","존재하지않는 target id 입니다.");
            }

        } else if (reviewType == ReviewType.SUBSCRIBE) {
            Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(requestDto.getId());
            if (!optionalSubscribe.isPresent()) {
                errors.reject("wrong id","존재하지않는 id 입니다.");
            }

            Optional<Recipe> optionalRecipe = recipeRepository.findById(requestDto.getTargetId());
            if (!optionalRecipe.isPresent()) {
                errors.reject("wrong target id","존재하지않는 target id 입니다.");
            }


        }

    }

    public void validateExists(List<Long> reviewIdList, Errors errors) {
        for (Long id : reviewIdList) {
            Optional<Review> optionalReview = reviewRepository.findById(id);
            if (!optionalReview.isPresent()) {
                errors.reject("id dose not exist" ,"존재하지않는 리뷰 id 입니다.");
                break;
            }
        }
    }

    public void validateReturn(Review review, Errors errors) {
        if (review.isApproval()) {
            errors.reject("already approval review" ,"이미 승인완료된 리뷰입니다.");
        }
    }

    public void validateExistsBestReviews(UpdateBestReviewLeakedOrderDto requestDto, Errors errors) {
        List<UpdateBestReviewLeakedOrderDto.LeakedOrderDto> leakedOrderDtoList = requestDto.getLeakedOrderDtoList();
        for (UpdateBestReviewLeakedOrderDto.LeakedOrderDto dto : leakedOrderDtoList) {
            Long id = dto.getId();
            Optional<BestReview> optionalBestReview = bestReviewRepository.findById(id);
            if (!optionalBestReview.isPresent()) {
                errors.reject("best review id doesn't exist" ,"존재하지 않는 베스트 리뷰 id 입니다.");
            }

        }
    }
}
