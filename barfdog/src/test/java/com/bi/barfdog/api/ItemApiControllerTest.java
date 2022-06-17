package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemImage;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.review.ItemReview;
import com.bi.barfdog.domain.review.Review;
import com.bi.barfdog.domain.review.ReviewImage;
import com.bi.barfdog.domain.review.ReviewStatus;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.review.ItemReviewRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ItemApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemReviewRepository itemReviewRepository;
    @Autowired
    ReviewImageRepository reviewImageRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ItemImageRepository itemImageRepository;


    @Test
    @DisplayName("정상적으로 전체 아이템 리스트 조회")
    public void queryItems() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,5).forEach(i -> {
            Item item = generateItemGoods(i);
            IntStream.range(1,4).forEach(j -> {
                generateItemReview(member, item, i+j+1, ReviewStatus.APPROVAL);
                generateItemImage(item, j);
            });
        });

        IntStream.range(1,6).forEach(i -> {
            Item item = generateItemRaw(i);
            IntStream.range(1,4).forEach(j -> {
                generateItemReview(member, item, i+j+2, ReviewStatus.APPROVAL);
                generateItemImage(item, j);
            });
        });

        IntStream.range(1,7).forEach(i -> {
            Item item = generateItemTopping(i);
            IntStream.range(1,4).forEach(j -> {
                generateItemReview(member, item, i+j+3, ReviewStatus.APPROVAL);
                generateItemImage(item, j);
            });
        });

       //when & then
        mockMvc.perform(get("/api/items")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("sortBy","recent")
                        .param("itemType","ALL"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }





    private ItemImage generateItemImage(Item item, int i) {
        ItemImage itemImage = ItemImage.builder()
                .item(item)
                .leakOrder(i)
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        return itemImageRepository.save(itemImage);
    }



    private ItemReview generateItemReview(Member member, Item item, int i, ReviewStatus reviewStatus) {
        ItemReview itemReview = ItemReview.builder()
                .member(member)
                .writtenDate(LocalDate.now().minusDays(30L))
                .username(member.getName())
                .star(((i + 5) % 5)==0? 5 : (i + 5) % 5)
                .contents("열글자 이상의 내용 "+i)
                .status(reviewStatus)
                .item(item)
                .build();
        itemReviewRepository.save(itemReview);

        IntStream.range(1,4).forEach(j -> {
            generateReviewImage(j, itemReview);
        });

        return itemReview;
    }

    private ReviewImage generateReviewImage(int i, Review review) {
        ReviewImage reviewImage = ReviewImage.builder()
                .review(review)
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        return reviewImageRepository.save(reviewImage);
    }



    private Item generateItemRaw(int i) {
        Item item = Item.builder()
                .itemType(ItemType.RAW)
                .name("상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .itemIcons("BEST,NEW")
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
    }

    private Item generateItemGoods(int i) {
        Item item = Item.builder()
                .itemType(ItemType.GOODS)
                .name("상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
    }

    private Item generateItemTopping(int i) {
        Item item = Item.builder()
                .itemType(ItemType.TOPPING)
                .name("상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
    }




    private String getAdminToken() throws Exception {
        return getBearerToken(appProperties.getAdminEmail(), appProperties.getAdminPassword());
    }

    private String getUserToken() throws Exception {
        return getBearerToken(appProperties.getUserEmail(), appProperties.getUserPassword());
    }

    private String getBearerToken(String appProperties, String appProperties1) throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .email(appProperties)
                .password(appProperties1)
                .build();

        //when & then
        ResultActions perform = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        MockHttpServletResponse response = perform.andReturn().getResponse();
        return response.getHeaders("Authorization").get(0);
    }


}