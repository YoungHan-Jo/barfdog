package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.UploadedImageAdminDto;
import com.bi.barfdog.api.reviewDto.*;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.review.*;
import com.bi.barfdog.domain.reward.*;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.review.BestReviewRepository;
import com.bi.barfdog.repository.review.ItemReviewRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.repository.review.SubscribeReviewRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StorageService storageService;
    private final ReviewImageRepository reviewImageRepository;
    private final ItemRepository itemRepository;
    private final ItemReviewRepository itemReviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final SubscribeRepository subscribeRepository;
    private final SubscribeReviewRepository subscribeReviewRepository;
    private final RewardRepository rewardRepository;
    private final BestReviewRepository bestReviewRepository;

    @Transactional
    public UploadedImageAdminDto uploadImage(MultipartFile file) {
        ImgFilenamePath path = storageService.storeReviewImg(file);

        UploadedImageAdminDto reviewImageDto = saveReviewImageAndGetReviewImageDto(path);

        return reviewImageDto;
    }

    private UploadedImageAdminDto saveReviewImageAndGetReviewImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        ReviewImage reviewImage = ReviewImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();
        ReviewImage savedImage = reviewImageRepository.save(reviewImage);

        String url = linkTo(InfoController.class).slash("display/reviews?filename=" + filename).toString();

        UploadedImageAdminDto reviewImageDto = UploadedImageAdminDto.builder()
                .id(savedImage.getId())
                .url(url)
                .build();
        return reviewImageDto;
    }

    @Transactional
    public void writeReview(Member member, WriteReviewDto requestDto) {
        ReviewType reviewType = requestDto.getReviewType();
        if (reviewType == ReviewType.ITEM) {
            writeItemReview(member, requestDto);
        } else if (reviewType == ReviewType.SUBSCRIBE) {
            writeSubscribeReview(member, requestDto);
        }
    }

    private void writeSubscribeReview(Member member, WriteReviewDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(requestDto.getId()).get();

        SubscribeReview subscribeReview = SubscribeReview.builder()
                .member(member)
                .writtenDate(LocalDate.now())
                .username(member.getName())
                .star(requestDto.getStar())
                .contents(requestDto.getContents())
                .status(ReviewStatus.REQUEST)
                .subscribe(subscribe)
                .build();
        subscribeReviewRepository.save(subscribeReview);

        List<ReviewImage> reviewImages = reviewImageRepository.findAllById(requestDto.getReviewImageIdList());

        for (ReviewImage reviewImage : reviewImages) {
            reviewImage.setImageToReview(subscribeReview);
        }

        subscribe.writeReview();
    }

    private void writeItemReview(Member member, WriteReviewDto requestDto) {
        Item item = itemRepository.findById(requestDto.getTargetId()).get();
        ItemReview itemReview = ItemReview.builder()
                .member(member)
                .writtenDate(LocalDate.now())
                .username(member.getName())
                .star(requestDto.getStar())
                .contents(requestDto.getContents())
                .status(ReviewStatus.REQUEST)
                .item(item)
                .build();
        itemReviewRepository.save(itemReview);

        List<ReviewImage> reviewImages = reviewImageRepository.findAllById(requestDto.getReviewImageIdList());
        for (ReviewImage reviewImage : reviewImages) {
            reviewImage.setImageToReview(itemReview);
        }
        OrderItem orderItem = orderItemRepository.findById(requestDto.getId()).get();
        orderItem.writeReview();
    }

    @Transactional
    public void deleteReview(Review review) {
        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        reviewImageRepository.deleteAll(reviewImages);

        reviewRepository.delete(review);
    }

    @Transactional
    public void updateReview(Long id, UpdateReviewDto requestDto) {
        Review review = reviewRepository.findById(id).get();
        review.update(requestDto);

        reviewImageRepository.deleteAllById(requestDto.getDeleteImageIdList());
        addReviewImages(requestDto, review);
    }

    private void addReviewImages(UpdateReviewDto requestDto, Review review) {
        List<ReviewImage> addImages = reviewImageRepository.findAllById(requestDto.getAddImageIdList());
        for (ReviewImage reviewImage : addImages) {
            reviewImage.setImageToReview(review);
        }
    }

    @Transactional
    public void approvalReviews(ReviewIdListDto requestDto) {
        List<Review> reviews = reviewRepository.findAllById(requestDto.getReviewIdList());
        for (Review review : reviews) {
            if (review.isRequest()) {
                review.approval();
                int rewardPoint = 0;
                if (review.getContents().length() >= 50) {
                    rewardPoint += RewardPoint.REVIEW_CONTENTS;
                }
                if (reviewImageRepository.findByReview(review).size() > 0) {
                    rewardPoint += RewardPoint.REVIEW_IMAGE;
                }
                if (rewardPoint > 0) {
                    Reward reward = Reward.builder()
                            .member(review.getMember())
                            .name(RewardName.REVIEW)
                            .rewardType(RewardType.REVIEW)
                            .rewardStatus(RewardStatus.SAVED)
                            .tradeReward(rewardPoint)
                            .build();
                    rewardRepository.save(reward);
                    review.getMember().chargePoint(rewardPoint);
                }
            }
        }
    }

    @Transactional
    public void createBestReviews(ReviewIdListDto requestDto) {

        int nextLeakedOrder = bestReviewRepository.findNextLeakedOrder();

        List<Review> reviews = reviewRepository.findAllById(requestDto.getReviewIdList());
        for (Review review : reviews) {
            Optional<BestReview> optionalBestReview = bestReviewRepository.findByReview(review);
            if (review.getStatus() == ReviewStatus.APPROVAL && !optionalBestReview.isPresent()) {
                BestReview bestReview = BestReview.builder()
                        .leakedOrder(nextLeakedOrder++)
                        .review(review)
                        .build();
                bestReviewRepository.save(bestReview);
            }
        }
    }

    @Transactional
    public void returnReview(Long id, ReturnReviewDto requestDto) {
        Review review = reviewRepository.findById(id).get();
        review.returnReview(requestDto);
    }

    @Transactional
    public void deleteBestReview(Long id) {
        BestReview bestReview = bestReviewRepository.findById(id).get();
        bestReviewRepository.delete(bestReview);

        int leakedOrder = bestReview.getLeakedOrder();
        bestReviewRepository.increaseLeakedOrder(leakedOrder);

    }

    @Transactional
    public void updateBestReviewLeakedOrder(UpdateBestReviewLeakedOrderDto requestDto) {
        List<UpdateBestReviewLeakedOrderDto.LeakedOrderDto> leakedOrderDtoList = requestDto.getLeakedOrderDtoList();
        for (UpdateBestReviewLeakedOrderDto.LeakedOrderDto dto : leakedOrderDtoList) {
            Long id = dto.getId();
            BestReview bestReview = bestReviewRepository.findById(id).get();
            bestReview.changeLeakedOrder(dto.getLeakedOrder());
        }
    }
}