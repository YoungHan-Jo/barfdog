package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.review.ItemReview;
import com.bi.barfdog.domain.review.Review;
import com.bi.barfdog.domain.review.ReviewImage;
import com.bi.barfdog.domain.review.ReviewStatus;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.review.ItemReviewRepository;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    SurveyReportRepository surveyReportRepository;
    @Autowired
    DogRepository dogRepository;
    @Autowired
    MemberCouponRepository memberCouponRepository;
    @Autowired
    CouponRepository couponRepository;

    @Before
    public void setUp() {

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemImageRepository.deleteAll();
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
        deliveryRepository.deleteAll();
        surveyReportRepository.deleteAll();
        dogRepository.deleteAll();
        memberCouponRepository.deleteAll();
        couponRepository.deleteAll();

    }

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

        IntStream.range(1,5).forEach(i -> {
            Item item = generateItemGoodsHidden(i);
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
                        .param("page","1")
                        .param("size","5")
                        .param("sortBy","recent")
                        .param("itemType","ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value("15"))
                .andDo(document("query_items",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수"),
                                parameterWithName("sortBy").description("정렬 조건 ['recent', 'registration', 'saleAmount'] 각 최신순, 등록순, 판매량순"),
                                parameterWithName("itemType").description("상품 카테고리 ['ALL', 'RAW', 'TOPPING', 'GOODS']")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryItemsDtoList[0].id").description("상품 id"),
                                fieldWithPath("_embedded.queryItemsDtoList[0].thumbnailUrl").description("상품 썸네일 url"),
                                fieldWithPath("_embedded.queryItemsDtoList[0].itemIcons").description("상품 아이콘 ['','BEST','NEW','BEST,NEW']"),
                                fieldWithPath("_embedded.queryItemsDtoList[0].name").description("상품 이름"),
                                fieldWithPath("_embedded.queryItemsDtoList[0].originalPrice").description("상품 원가"),
                                fieldWithPath("_embedded.queryItemsDtoList[0].salePrice").description("상품 판매가"),
                                fieldWithPath("_embedded.queryItemsDtoList[0].inStock").description("재고여부 true/false 각 유/무"),
                                fieldWithPath("_embedded.queryItemsDtoList[0].star").description("평균 평점"),
                                fieldWithPath("_embedded.queryItemsDtoList[0].reviewCount").description("리뷰 수"),
                                fieldWithPath("_embedded.queryItemsDtoList[0]._links.query_item.href").description("상품 하나 조회 링크"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 RAW 아이템 리스트 조회")
    public void queryItems_RAW() throws Exception {
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
                        .param("page","1")
                        .param("size","5")
                        .param("sortBy","recent")
                        .param("itemType","RAW"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value("5"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList").doesNotExist())
        ;
    }

    @Test
    @DisplayName("정상적으로 GOODS 아이템 리스트 조회")
    public void queryItems_GOODS() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,4).forEach(i -> {
            Item item = generateItemGoods(i);
            IntStream.range(1,4).forEach(j -> {
                generateItemReview(member, item, i+j+1, ReviewStatus.APPROVAL);
                generateItemImage(item, j);
            });
        });

        IntStream.range(1,5).forEach(i -> {
            Item item = generateItemGoodsHidden(i);
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
                        .param("page","1")
                        .param("size","5")
                        .param("sortBy","recent")
                        .param("itemType","GOODS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value("3"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList").doesNotExist())
        ;
    }

    @Test
    @DisplayName("정상적으로 TOPPING 아이템 리스트 조회")
    public void queryItems_TOPPING() throws Exception {
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
                generateItemReview(member, item, 8, ReviewStatus.APPROVAL);
                generateItemImage(item, j);
            });
        });

        //when & then
        mockMvc.perform(get("/api/items")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page","1")
                        .param("size","5")
                        .param("sortBy","recent")
                        .param("itemType","TOPPING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value("6"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList", hasSize(1)))
        ;
    }

    @Test
    @DisplayName("아이템 리스트 조회 시 리뷰 없는 상품은 평점0 리뷰0")
    public void queryItems_no_reviews() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,9).forEach(i -> {
            Item item = generateItemGoods(i);
            IntStream.range(1,4).forEach(j -> {
                generateItemImage(item, j);
            });
        });

        //when & then
        mockMvc.perform(get("/api/items")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortBy", "recent")
                        .param("itemType", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].star").value(0))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].reviewCount").value(0))
        ;
    }

    @Test
    @DisplayName("아이템 리스트 최신순으로 조회")
    public void queryItems_order_by_recent() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,9).forEach(i -> {
            Item item = generateItemGoods(i);
            IntStream.range(1,4).forEach(j -> {
                generateItemImage(item, j);
            });
        });

        //when & then
        mockMvc.perform(get("/api/items")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "recent")
                        .param("itemType", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].star").value(0))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].reviewCount").value(0))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].name").value("굿즈 상품8"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[1].name").value("굿즈 상품7"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[2].name").value("굿즈 상품6"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[3].name").value("굿즈 상품5"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[4].name").value("굿즈 상품4"))
        ;
    }

    @Test
    @DisplayName("아이템 리스트 등록일 순으로 조회")
    public void queryItems_order_by_registration() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,9).forEach(i -> {
            Item item = generateItemGoods(i);
            IntStream.range(1,4).forEach(j -> {
                generateItemImage(item, j);
            });
        });

        //when & then
        mockMvc.perform(get("/api/items")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "registration") // recent, registration, saleAmount
                        .param("itemType", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].star").value(0))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].reviewCount").value(0))
//                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].name").value("굿즈 상품1"))
//                .andExpect(jsonPath("_embedded.queryItemsDtoList[1].name").value("굿즈 상품2"))
//                .andExpect(jsonPath("_embedded.queryItemsDtoList[2].name").value("굿즈 상품3"))
//                .andExpect(jsonPath("_embedded.queryItemsDtoList[3].name").value("굿즈 상품4"))
        ;
    }

    @Test
    @DisplayName("아이템 리스트 판매량 순으로 조회")
    public void queryItems_order_by_saleAmount() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,9).forEach(i -> {
            Item item = generateItemGoods(i);
            IntStream.range(1,4).forEach(j -> {
                generateItemImage(item, j);
            });
        });

        //when & then
        mockMvc.perform(get("/api/items")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "saleAmount") // recent, registration, saleAmount
                        .param("itemType", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].star").value(0))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].reviewCount").value(0))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[0].name").value("굿즈 상품8"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[1].name").value("굿즈 상품7"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[2].name").value("굿즈 상품6"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[3].name").value("굿즈 상품5"))
                .andExpect(jsonPath("_embedded.queryItemsDtoList[4].name").value("굿즈 상품4"))
        ;
    }

    @Test
    @DisplayName("정상적으로 상품 하나 조회")
    public void queryItem() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItemGoods(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
            generateOption(item, i);
        });
        IntStream.range(1,4).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.APPROVAL);
        });

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/items/{id}",item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_item",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_item_reviews").description("리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 상품 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("itemDto.id").description("상품 id"),
                                fieldWithPath("itemDto.name").description("상품 이름"),
                                fieldWithPath("itemDto.description").description("상품 설명"),
                                fieldWithPath("itemDto.originalPrice").description("원가"),
                                fieldWithPath("itemDto.discountType").description("할인 타입 [FIXED_RATE, FLAT_RATE]"),
                                fieldWithPath("itemDto.discountDegree").description("할인 정도"),
                                fieldWithPath("itemDto.salePrice").description("할인 적용 후 판매가"),
                                fieldWithPath("itemDto.inStock").description("재고 여부 true/false"),
                                fieldWithPath("itemDto.remaining").description("재고 수량"),
                                fieldWithPath("itemDto.totalSalesAmount").description("누적 판매량"),
                                fieldWithPath("itemDto.contents").description("상품 내용"),
                                fieldWithPath("itemDto.itemIcons").description("상품 아이콘 ['','BEST','NEW','BEST,NEW']"),
                                fieldWithPath("itemDto.deliveryFree").description("배송비 무료 여부 true/false 각 무료/유료"),
                                fieldWithPath("deliveryCondDto.price").description("배송비 가격"),
                                fieldWithPath("deliveryCondDto.freeCondition").description("배송비 무료 조건 xx 원 이상 무료"),
                                fieldWithPath("itemOptionDtoList[0].id").description("옵션 id"),
                                fieldWithPath("itemOptionDtoList[0].name").description("옵션 이름"),
                                fieldWithPath("itemOptionDtoList[0].optionPrice").description("옵션 가격"),
                                fieldWithPath("itemOptionDtoList[0].remaining").description("옵션 재고 수량"),
                                fieldWithPath("itemImageDtoList[0].id").description("아이템 이미지 id"),
                                fieldWithPath("itemImageDtoList[0].leakedOrder").description("아이템 이미지 노출 순서"),
                                fieldWithPath("itemImageDtoList[0].filename").description("아이템 이미지 파일 이름"),
                                fieldWithPath("itemImageDtoList[0].url").description("아이템 이미지 url"),
                                fieldWithPath("reviewDto.star").description("리뷰 평균 평점"),
                                fieldWithPath("reviewDto.count").description("리뷰 개수"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_item_reviews.href").description("리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회할 상품이 존재하지않음 404")
    public void queryItem_notFound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItemGoods(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
            generateOption(item, i);
        });
        IntStream.range(1,14).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.APPROVAL);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/items/999999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 해당 상품의 리뷰 리스트 조회")
    public void queryItemReviews() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItemGoods(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
            generateOption(item, i);
        });
        IntStream.range(1,14).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.APPROVAL);
        });

        Item item2 = generateItemGoods(2);
        IntStream.range(1,3).forEach(i -> {
            generateItemImage(item2, i);
            generateOption(item2, i);
        });
        IntStream.range(1,4).forEach(i -> {
            generateItemReview(member, item2, 1+i, ReviewStatus.APPROVAL);
        });

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/items/{id}/reviews",item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andDo(document("query_item_reviews",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("query_review_recipes").description("관리자 생성 리뷰에 필요한 레시피 리스트 조회 링크"),
                                linkWithRel("query_review_items").description("관리자 생성 리뷰에 필요한 아이템 리스트 조회 링크"),
                                linkWithRel("admin_create_review").description("관리자 토큰일때만 나오는 리뷰 생성 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 상품 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.itemReviewsDtoList[0].reviewDto.id").description("리뷰 id"),
                                fieldWithPath("_embedded.itemReviewsDtoList[0].reviewDto.star").description("리뷰 평점"),
                                fieldWithPath("_embedded.itemReviewsDtoList[0].reviewDto.contents").description("리뷰 내용"),
                                fieldWithPath("_embedded.itemReviewsDtoList[0].reviewDto.username").description("리뷰 작성자 이름"),
                                fieldWithPath("_embedded.itemReviewsDtoList[0].reviewDto.createdDate").description("리뷰 작성일"),
                                fieldWithPath("_embedded.itemReviewsDtoList[0].reviewImageDtoList[0].filename").description("리뷰 이미지 파일이름"),
                                fieldWithPath("_embedded.itemReviewsDtoList[0].reviewImageDtoList[0].url").description("리뷰 이미지 url"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.query_review_recipes.href").description("관리자 생성 리뷰에 필요한 레시피 리스트 조회 링크"),
                                fieldWithPath("_links.query_review_items.href").description("관리자 생성 리뷰에 필요한 아이템 리스트 조회 링크"),
                                fieldWithPath("_links.admin_create_review.href").description("관리자 토큰일때만 나오는 리뷰 생성 링크. 즉,이 링크가 없으면 관리자 계정이 아님"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("상품 리뷰 리스트 조회 시 일반 유저일 경우 리뷰생성 링크 없음")
    public void queryItemReviews_user_noLink() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItemGoods(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
            generateOption(item, i);
        });
        IntStream.range(1,14).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.APPROVAL);
        });

        Item item2 = generateItemGoods(2);
        IntStream.range(1,3).forEach(i -> {
            generateItemImage(item2, i);
            generateOption(item2, i);
        });
        IntStream.range(1,4).forEach(i -> {
            generateItemReview(member, item2, 1+i, ReviewStatus.APPROVAL);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/items/{id}/reviews", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(13))
                .andExpect(jsonPath("_links.admin_create_review.href").doesNotExist())
        ;

    }

    @Test
    @DisplayName("상품 리뷰 리스트 조회 시 승인받은 리뷰, admin 리뷰만 조회됨")
    public void queryItemReviews_approval_admin() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItemGoods(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
            generateOption(item, i);
        });
        IntStream.range(1,11).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.APPROVAL);
        });

        IntStream.range(1,11).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.ADMIN);
        });

        IntStream.range(1,4).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.REQUEST);
        });

        IntStream.range(1,4).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.RETURN);
        });

        Item item2 = generateItemGoods(2);
        IntStream.range(1,3).forEach(i -> {
            generateItemImage(item2, i);
            generateOption(item2, i);
        });
        IntStream.range(1,4).forEach(i -> {
            generateItemReview(member, item2, 1+i, ReviewStatus.APPROVAL);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/items/{id}/reviews", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(20))
                .andExpect(jsonPath("_links.admin_create_review.href").doesNotExist())
        ;

    }

    @Test
    @DisplayName("상품 리뷰 리스트를 조회할 상품이 존재하지않음 404")
    public void queryItemReviews_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItemGoods(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
            generateOption(item, i);
        });
        IntStream.range(1,14).forEach(i -> {
            generateItemReview(member, item, 1+i, ReviewStatus.APPROVAL);
        });

        Item item2 = generateItemGoods(2);
        IntStream.range(1,3).forEach(i -> {
            generateItemImage(item2, i);
            generateOption(item2, i);
        });
        IntStream.range(1,4).forEach(i -> {
            generateItemReview(member, item2, 1+i, ReviewStatus.APPROVAL);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/items/999999/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isNotFound());
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

    private ItemOption generateOption(Item item, int i) {
        ItemOption itemOption = ItemOption.builder()
                .item(item)
                .name("옵션" + i)
                .optionPrice(i * 1000)
                .remaining(999)
                .build();
        return itemOptionRepository.save(itemOption);
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
                .name("생식 상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .itemIcons("NEW,BEST")
                .itemIcons("BEST,NEW")
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
    }

    private Item generateItemGoods(int i) {
        Item item = Item.builder()
                .itemType(ItemType.GOODS)
                .name("굿즈 상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .itemIcons("NEW,BEST")
                .totalSalesAmount(i)
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
    }

    private Item generateItemGoodsHidden(int i) {
        Item item = Item.builder()
                .itemType(ItemType.GOODS)
                .name("굿즈 상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .itemIcons("NEW,BEST")
                .totalSalesAmount(i)
                .deliveryFree(true)
                .status(ItemStatus.HIDDEN)
                .build();
        return itemRepository.save(item);
    }

    private Item generateItemTopping(int i) {
        Item item = Item.builder()
                .itemType(ItemType.TOPPING)
                .name("토핑 상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .itemIcons("NEW,BEST")
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