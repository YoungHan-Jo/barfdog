package com.bi.barfdog.api;

import com.bi.barfdog.api.reviewDto.*;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemImage;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.review.*;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardPoint;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.review.BestReviewRepository;
import com.bi.barfdog.repository.review.ItemReviewRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.repository.review.SubscribeReviewRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
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

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ReviewAdminControllerTest extends BaseTest {

    @Autowired
    EntityManager em;
    @Autowired
    AppProperties appProperties;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemImageRepository itemImageRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    DogRepository dogRepository;
    @Autowired
    SubscribeRepository subscribeRepository;
    @Autowired
    ReviewImageRepository reviewImageRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ItemReviewRepository itemReviewRepository;
    @Autowired
    SubscribeReviewRepository subscribeReviewRepository;
    @Autowired
    RewardRepository rewardRepository;
    @Autowired
    BestReviewRepository bestReviewRepository;

    
    @Test
    @DisplayName("정상적으로 레시피 이름 리스트 조회")
    public void queryReviewRecipes() throws Exception {
       //given
       
       //when & then
        mockMvc.perform(get("/api/admin/reviews/recipes")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.reviewRecipesDtoList", hasSize(4)))
                .andDo(document("admin_query_review_recipes",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.reviewRecipesDtoList[0].id").description(""),
                                fieldWithPath("_embedded.reviewRecipesDtoList[0].name").description(""),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("정상적으로 리뷰작성시 모든 아이템 조회하는 테스트")
    public void queryReviewItems() throws Exception {
       //given

        IntStream.range(1,4).forEach(i -> {
            Item item = generateItem(i, ItemType.RAW);
            generateItemImage(item, i);
        });
        IntStream.range(1,5).forEach(i -> {
            Item item = generateItem(i, ItemType.GOODS);
            generateItemImage(item, i);
        });
        IntStream.range(1,6).forEach(i -> {
            Item item = generateItem(i, ItemType.TOPPING);
            generateItemImage(item, i);
        });


        //when & then
        String itemType = "ALL"; // [ALL, RAW, TOPPING, GOODS]
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/reviews/items/{itemType}", itemType)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.reviewItemsDtoList", hasSize(12)))
                .andDo(document("admin_query_review_items",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("itemType").description("조회할 아이템 카테고리 [ALL, RAW, TOPPING, GOODS] 반드시 '대문자'로")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.reviewItemsDtoList[0].id").description("단일 상품 id"),
                                fieldWithPath("_embedded.reviewItemsDtoList[0].name").description("단일 상품 이름"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 리뷰작성시 RAW 아이템 조회하는 테스트")
    public void queryReviewItems_RAW() throws Exception {
        //given

        IntStream.range(1,4).forEach(i -> {
            Item item = generateItem(i, ItemType.RAW);
            generateItemImage(item, i);
        });
        IntStream.range(1,5).forEach(i -> {
            Item item = generateItem(i, ItemType.GOODS);
            generateItemImage(item, i);
        });
        IntStream.range(1,6).forEach(i -> {
            Item item = generateItem(i, ItemType.TOPPING);
            generateItemImage(item, i);
        });


        //when & then
        String itemType = "RAW"; // [ALL, RAW, TOPPING, GOODS]
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/reviews/items/{itemType}", itemType)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.reviewItemsDtoList", hasSize(3)))
        ;

    }

    @Test
    @DisplayName("정상적으로 리뷰작성시 TOPPING 아이템 조회하는 테스트")
    public void queryReviewItems_TOPPING() throws Exception {
        //given

        IntStream.range(1,4).forEach(i -> {
            Item item = generateItem(i, ItemType.RAW);
            generateItemImage(item, i);
        });
        IntStream.range(1,5).forEach(i -> {
            Item item = generateItem(i, ItemType.GOODS);
            generateItemImage(item, i);
        });
        IntStream.range(1,6).forEach(i -> {
            Item item = generateItem(i, ItemType.TOPPING);
            generateItemImage(item, i);
        });


        //when & then
        String itemType = "TOPPING"; // [ALL, RAW, TOPPING, GOODS]
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/reviews/items/{itemType}", itemType)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.reviewItemsDtoList", hasSize(5)))
        ;

    }

    @Test
    @DisplayName("정상적으로 리뷰작성시 GOODS 아이템 조회하는 테스트")
    public void queryReviewItems_GOODS() throws Exception {
        //given

        IntStream.range(1,4).forEach(i -> {
            Item item = generateItem(i, ItemType.RAW);
            generateItemImage(item, i);
        });
        IntStream.range(1,5).forEach(i -> {
            Item item = generateItem(i, ItemType.GOODS);
            generateItemImage(item, i);
        });
        IntStream.range(1,6).forEach(i -> {
            Item item = generateItem(i, ItemType.TOPPING);
            generateItemImage(item, i);
        });


        //when & then
        String itemType = "GOODS"; // [ALL, RAW, TOPPING, GOODS]
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/reviews/items/{itemType}", itemType)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.reviewItemsDtoList", hasSize(4)))
        ;

    }

    

    @Test
    @DisplayName("정상적으로 아이템 리뷰 생성")
    public void createReview_item() throws Exception {
       //given

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
        });

        List<Long> reviewImageIdList = new ArrayList<>();
        IntStream.range(1,5).forEach(i -> {
            ReviewImage reviewImage = generateReviewImage(i);
            reviewImageIdList.add(reviewImage.getId());
        });

        String username = "마재윤";
        String contents = "열 글자 이상의 내용";
        int star = 5;

        LocalDate writtenDate = LocalDate.now();
        SaveAdminReviewDto requestDto = SaveAdminReviewDto.builder()
                .type(ReviewType.ITEM)
                .id(item.getId())
                .writtenDate(writtenDate)
                .star(star)
                .contents(contents)
                .username(username)
                .reviewImageIdList(reviewImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("admin_create_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_reviews").description("관리자 리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestFields(
                                fieldWithPath("type").description("리뷰 타입 [ITEM,SUBSCRIBE]"),
                                fieldWithPath("id").description("리뷰 대상 id [상품리뷰 -> 아이템 id / 구독리뷰 -> 레시피 id]"),
                                fieldWithPath("writtenDate").description("리뷰 작성일 ['yyyy-MM-dd']"),
                                fieldWithPath("star").description("리뷰 평점 1~5 int"),
                                fieldWithPath("contents").description("리뷰 내용"),
                                fieldWithPath("username").description("리뷰 작성자 이름"),
                                fieldWithPath("reviewImageIdList").description("리뷰 이미지 id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_reviews.href").description("관리자 리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        List<Review> reviewList = reviewRepository.findAll();
        ItemReview findReview = (ItemReview) reviewList.get(0);
        assertThat(findReview.getItem().getId()).isEqualTo(item.getId());
        assertThat(findReview.getStatus()).isEqualTo(ReviewStatus.ADMIN);
        assertThat(findReview.getContents()).isEqualTo(contents);
        assertThat(findReview.getUsername()).isEqualTo(username);
        assertThat(findReview.getWrittenDate()).isEqualTo(writtenDate);
        assertThat(findReview.getStar()).isEqualTo(star);
    }

    @Test
    @DisplayName("정상적으로 구독 리뷰 생성")
    public void createReview_subscribe() throws Exception {
        //given

        List<Recipe> recipes = recipeRepository.findAll();
        Recipe recipe = recipes.get(0);

        List<Long> reviewImageIdList = new ArrayList<>();
        IntStream.range(1,5).forEach(i -> {
            ReviewImage reviewImage = generateReviewImage(i);
            reviewImageIdList.add(reviewImage.getId());
        });

        String username = "마재윤";
        String contents = "열 글자 이상의 내용";
        int star = 5;

        LocalDate writtenDate = LocalDate.now();
        SaveAdminReviewDto requestDto = SaveAdminReviewDto.builder()
                .type(ReviewType.SUBSCRIBE)
                .id(recipe.getId())
                .writtenDate(writtenDate)
                .star(star)
                .contents(contents)
                .username(username)
                .reviewImageIdList(reviewImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        em.flush();
        em.clear();

        List<Review> reviewList = reviewRepository.findAll();
        SubscribeReview findReview = (SubscribeReview) reviewList.get(0);
        assertThat(findReview.getSubscribe().getDog().getRecommendRecipe().getId()).isEqualTo(recipe.getId());
        assertThat(findReview.getStatus()).isEqualTo(ReviewStatus.ADMIN);
        assertThat(findReview.getContents()).isEqualTo(contents);
        assertThat(findReview.getUsername()).isEqualTo(username);
        assertThat(findReview.getWrittenDate()).isEqualTo(writtenDate);
        assertThat(findReview.getStar()).isEqualTo(star);

        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "0")
                        .param("status", "ADMIN")
                        .param("order", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(1))
        ;

        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "0")
                        .param("status", "APPROVAL")
                        .param("order", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(0))
        ;

        mockMvc.perform(get("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(1))
        ;
    }

    @Test
    @DisplayName("리뷰 생성시 아이템이 존재하지않음 404")
    public void createReview_not_exist_item() throws Exception {
        //given

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
        });

        List<Long> reviewImageIdList = new ArrayList<>();
        IntStream.range(1,5).forEach(i -> {
            ReviewImage reviewImage = generateReviewImage(i);
            reviewImageIdList.add(reviewImage.getId());
        });

        String username = "마재윤";
        String contents = "열 글자 이상의 내용";
        int star = 5;

        LocalDate writtenDate = LocalDate.now();
        SaveAdminReviewDto requestDto = SaveAdminReviewDto.builder()
                .type(ReviewType.ITEM)
                .id(999999L)
                .writtenDate(writtenDate)
                .star(star)
                .contents(contents)
                .username(username)
                .reviewImageIdList(reviewImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("리뷰 생성시 레시피가 존재하지않음 404")
    public void createReview_not_exist_recipe() throws Exception {
        //given

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
        });

        List<Long> reviewImageIdList = new ArrayList<>();
        IntStream.range(1,5).forEach(i -> {
            ReviewImage reviewImage = generateReviewImage(i);
            reviewImageIdList.add(reviewImage.getId());
        });

        String username = "마재윤";
        String contents = "열 글자 이상의 내용";
        int star = 5;

        LocalDate writtenDate = LocalDate.now();
        SaveAdminReviewDto requestDto = SaveAdminReviewDto.builder()
                .type(ReviewType.ITEM)
                .id(999999L)
                .writtenDate(writtenDate)
                .star(star)
                .contents(contents)
                .username(username)
                .reviewImageIdList(reviewImageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    @Test
    @DisplayName("정상적으로 전체 리뷰 조회")
    public void queryAllReviews() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,11).forEach(i -> {
            generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
        });

        IntStream.range(1,11).forEach(i -> {
            generateSubscribeReview(member, i, ReviewStatus.RETURN);
            generateSubscribeReview(admin, i, ReviewStatus.ADMIN);
        });

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

       //when & then
        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("status", "ALL")
                        .param("order", "desc")
                        .param("from", "2022-06-01")
                        .param("to", today))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(40))
                .andDo(document("query_admin_reviews",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("approve_reviews").description("리뷰 승인 링크"),
                                linkWithRel("create_best_reviews").description("베스트 리뷰 선정 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수"),
                                parameterWithName("status").description("검색할 카테고리 [ALL,REQUEST,RETURN,APPROVAL,ADMIN]"),
                                parameterWithName("order").description("정렬 옵션 ['desc' or 'asc'] desc=최신순"),
                                parameterWithName("from").description("검색 날짜 from 'yyyy-MM-dd'"),
                                parameterWithName("to").description("검색 날짜 to 'yyyy-MM-dd'")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0].id").description("리뷰 id"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0].status").description("리뷰 상태 [REQUEST,RETURN,APPROVAL,ADMIN]"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0].title").description("상품 이름"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0].star").description("리뷰 평점 1~5 정수"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0].contents").description("리뷰 내용"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0].createdDate").description("리뷰 작성날짜 [yyyy-MM-dd]"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0].name").description("작성자 이름"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0].email").description("작성자 email"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0]._links.query_review.href").description("리뷰 하나 조회 링크"),
                                fieldWithPath("_embedded.queryAdminReviewsDtoList[0]._links.delete_review.href").description("리뷰 삭제 링크"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.approve_reviews.href").description("리뷰 승인 링크"),
                                fieldWithPath("_links.create_best_reviews.href").description("베스트 리뷰 선정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }


    @Test
    @DisplayName("정상적으로 기간 설정해서 전체 리뷰 조회")
    public void queryAllReviews_FromTo() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,11).forEach(i -> {
            generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(1L));
            generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(1L));
        });

        LocalDate writtenDay = LocalDate.of(2022, 06, 15);
        IntStream.range(1,6).forEach(i -> {
            generateItemReview(member, item, i, ReviewStatus.REQUEST, writtenDay);
            generateItemReview(admin, item, i, ReviewStatus.ADMIN, writtenDay.plusDays(2));
        });

        //when & then
        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("status", "ALL")
                        .param("order", "desc")
                        .param("from", "2022-06-01")
                        .param("to", "2022-06-30"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(10))
        ;

    }

    @Test
    @DisplayName("정상적으로 반려된 리뷰 조회")
    public void queryAllReviews_RETURN() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,11).forEach(i -> {
            generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
        });

        IntStream.range(1,11).forEach(i -> {
            generateSubscribeReview(member, i, ReviewStatus.RETURN);
            generateSubscribeReview(admin, i, ReviewStatus.ADMIN);
        });

        //when & then
        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("status", "RETURN")
                        .param("order", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(10))
                .andExpect(jsonPath("_embedded.queryAdminReviewsDtoList[0].status").value("RETURN"))
        ;

    }

    @Test
    @DisplayName("정상적으로 ADMIN 리뷰 조회")
    public void queryAllReviews_ADMIN() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,11).forEach(i -> {
            generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
        });

        IntStream.range(1,11).forEach(i -> {
            generateSubscribeReview(member, i, ReviewStatus.RETURN);
            generateSubscribeReview(admin, i, ReviewStatus.ADMIN);
        });

        //when & then
        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("status", "ADMIN")
                        .param("order", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(20))
                .andExpect(jsonPath("_embedded.queryAdminReviewsDtoList[0].status").value("ADMIN"))
        ;

    }

    @Test
    @DisplayName("정상적으로 REQUEST 리뷰 조회")
    public void queryAllReviews_REQUEST() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,11).forEach(i -> {
            generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
        });

        IntStream.range(1,11).forEach(i -> {
            generateSubscribeReview(member, i, ReviewStatus.RETURN);
            generateSubscribeReview(admin, i, ReviewStatus.ADMIN);
        });

        //when & then
        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("status", "REQUEST")
                        .param("order", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(10))
                .andExpect(jsonPath("_embedded.queryAdminReviewsDtoList[0].status").value("REQUEST"))
        ;

    }

    @Test
    @DisplayName("정상적으로 APPROVAL 리뷰 조회")
    public void queryAllReviews_APPROVAL() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,11).forEach(i -> {
            generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
        });

        IntStream.range(1,11).forEach(i -> {
            generateSubscribeReview(member, i, ReviewStatus.RETURN);
            generateSubscribeReview(admin, i, ReviewStatus.APPROVAL);
        });

        //when & then
        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("status", "APPROVAL")
                        .param("order", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(10))
                .andExpect(jsonPath("_embedded.queryAdminReviewsDtoList[0].status").value("APPROVAL"))
        ;

    }


    @Test
    @DisplayName("잘못된 status 일 경우 400")
    public void queryReviews_wrongStatus() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/api/admin/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("status", "asdf")
                        .param("order", "desc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("정상적으로 리뷰 승인하기")
    public void approvalReviews() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<Long> reviewIdList = new ArrayList<>();
        IntStream.range(1,3).forEach(i -> {
            ItemReview itemReview = generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            SubscribeReview subscribeReview = (SubscribeReview) generateSubscribeReview(member, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
            reviewIdList.add(subscribeReview.getId());
        });

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/reviews/approval")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("approve_reviews",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_reviews").description("리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestFields(
                                fieldWithPath("reviewIdList").description("승인할 리뷰 id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_reviews.href").description("리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<Review> approvalReviews = reviewRepository.findAllByStatus(ReviewStatus.APPROVAL);
        assertThat(approvalReviews.size()).isEqualTo(4);
        for (Review review : approvalReviews) {
            assertThat(review.getReturnReason().equals("")).isTrue();
        }

    }

    @Test
    @DisplayName("리뷰 승인 50자 이상 적립금 300")
    public void approvalReviews_over50() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<Long> reviewIdList = new ArrayList<>();
        IntStream.range(1,5).forEach(i -> {
            ItemReview itemReview = generateItemReview_Over50_EmptyImage(member, item, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
        });

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/reviews/approval")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        Member findMember = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        assertThat(findMember.getReward()).isEqualTo(reward + RewardPoint.REVIEW_CONTENTS * 4);
        List<Review> reviews = reviewRepository.findAllById(reviewIdList);
        for (Review review : reviews) {
            assertThat(review.getStatus()).isEqualTo(ReviewStatus.APPROVAL);
        }
        List<Reward> rewardList = rewardRepository.findByMember(member);
        assertThat(rewardList.size()).isEqualTo(4);


    }

    @Test
    @DisplayName("리뷰 승인 이미지 삽입 적립금 500")
    public void approvalReviews_Image() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        int reward = member.getReward();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<Long> reviewIdList = new ArrayList<>();
        IntStream.range(1,5).forEach(i -> {
            ItemReview itemReview = generateItemReview_OnlyImage(member, item, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
        });

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/reviews/approval")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        Member findMember = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        assertThat(findMember.getReward()).isEqualTo(reward + RewardPoint.REVIEW_IMAGE * 4);
        List<Review> reviews = reviewRepository.findAllById(reviewIdList);
        for (Review review : reviews) {
            assertThat(review.getStatus()).isEqualTo(ReviewStatus.APPROVAL);
        }
        List<Reward> rewardList = rewardRepository.findByMember(member);
        assertThat(rewardList.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("리뷰 승인 이미지 삽입 50글자 이상 적립금 800")
    public void approvalReviews_Image_50length() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        int reward = member.getReward();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<Long> reviewIdList = new ArrayList<>();
        IntStream.range(1,5).forEach(i -> {
            ItemReview itemReview = generateItemReview_Over50AndImage(member, item, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
        });

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/reviews/approval")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        Member findMember = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        assertThat(findMember.getReward()).isEqualTo(reward + (RewardPoint.REVIEW_IMAGE + RewardPoint.REVIEW_CONTENTS) * 4);
        List<Review> reviews = reviewRepository.findAllById(reviewIdList);
        for (Review review : reviews) {
            assertThat(review.getStatus()).isEqualTo(ReviewStatus.APPROVAL);
        }
        List<Reward> rewardList = rewardRepository.findByMember(member);
        assertThat(rewardList.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("승인할 리뷰가 존재하지 않을 경우 400")
    public void approvalReviews_400() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<Long> reviewIdList = new ArrayList<>();
        IntStream.range(1,5).forEach(i -> {
            ItemReview itemReview = generateItemReview_Over50AndImage(member, item, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
        });
        reviewIdList.add(999999L);

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/reviews/approval")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("정상적으로 리뷰 삭제")
    public void deleteReview() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        ItemReview review = generateItemReview(member, item, 1, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_delete_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_reviews").description("리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("삭제할 리뷰 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_reviews.href").description("리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Optional<Review> optionalReview = reviewRepository.findById(review.getId());
        assertThat(optionalReview.isPresent()).isFalse();
        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        assertThat(reviewImages.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("삭제할 리뷰가 없을 경우 404")
    public void deleteReview_notFound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        ItemReview review = generateItemReview(member, item, 1, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/reviews/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 베스트리뷰 신청")
    public void createBestReview() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        List<Long> reviewIdList = new ArrayList<>();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview requestReview = generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
            reviewIdList.add(requestReview.getId());
            reviewIdList.add(adminReview.getId());
        });

        IntStream.range(1,6).forEach(i -> {
            Review returnReview = generateSubscribeReview(member, i, ReviewStatus.RETURN);
            Review approvalReview = generateSubscribeReview(admin, i, ReviewStatus.APPROVAL);
            reviewIdList.add(returnReview.getId());
            reviewIdList.add(approvalReview.getId());
        });

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/reviews/best")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("create_best_reviews",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_reviews").description("리뷰 리스트 조회 링크"),
                                linkWithRel("query_best_reviews").description("베스트 리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestFields(
                                fieldWithPath("reviewIdList").description("베스트로 등록할 리뷰 id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_reviews.href").description("리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.query_best_reviews.href").description("베스트 리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));


        em.flush();
        em.clear();

        List<BestReview> all = bestReviewRepository.findAllByOrderByLeakedOrderAsc();
        assertThat(all.size()).isEqualTo(5);
        assertThat(all.get(0).getReview().getStatus()).isEqualTo(ReviewStatus.APPROVAL);
        assertThat(all.get(0).getLeakedOrder()).isEqualTo(1);
        assertThat(all.get(1).getLeakedOrder()).isEqualTo(2);
        assertThat(all.get(2).getLeakedOrder()).isEqualTo(3);
        assertThat(all.get(3).getLeakedOrder()).isEqualTo(4);
        assertThat(all.get(4).getLeakedOrder()).isEqualTo(5);

    }

    @Test
    @DisplayName("첫번째 이후의 베스트리뷰 신청")
    public void createBestReview_fromSecond() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        List<Long> reviewIdList = new ArrayList<>();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview requestReview = generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
            reviewIdList.add(requestReview.getId());
            reviewIdList.add(adminReview.getId());
        });

        Review review = generateSubscribeReview(admin, 11, ReviewStatus.APPROVAL);
        generateBestReview(review, 1);

        IntStream.range(1,6).forEach(i -> {
            Review returnReview = generateSubscribeReview(member, i, ReviewStatus.RETURN);
            Review approvalReview = generateSubscribeReview(admin, i, ReviewStatus.APPROVAL);
            reviewIdList.add(returnReview.getId());
            reviewIdList.add(approvalReview.getId());
        });

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/reviews/best")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        List<BestReview> all = bestReviewRepository.findAllByOrderByLeakedOrderAsc();
        assertThat(all.size()).isEqualTo(6);
        assertThat(all.get(0).getReview().getStatus()).isEqualTo(ReviewStatus.APPROVAL);
        assertThat(all.get(0).getLeakedOrder()).isEqualTo(1);
        assertThat(all.get(0).getReview().getId()).isEqualTo(review.getId());
        assertThat(all.get(1).getLeakedOrder()).isEqualTo(2);
        assertThat(all.get(2).getLeakedOrder()).isEqualTo(3);
        assertThat(all.get(3).getLeakedOrder()).isEqualTo(4);
        assertThat(all.get(4).getLeakedOrder()).isEqualTo(5);
        assertThat(all.get(5).getLeakedOrder()).isEqualTo(6);

    }

    @Test
    @DisplayName("베스트리뷰 신청 시 이미 베스트리뷰인 리뷰는 무시")
    public void createBestReview_alreadyBestReview() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        List<Long> reviewIdList = new ArrayList<>();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview requestReview = generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
            reviewIdList.add(requestReview.getId());
            reviewIdList.add(adminReview.getId());
        });

        Review review = generateSubscribeReview(admin, 9999, ReviewStatus.APPROVAL);
        generateBestReview(review, 1);

        reviewIdList.add(review.getId());

        IntStream.range(1,6).forEach(i -> {
            Review returnReview = generateSubscribeReview(member, i, ReviewStatus.RETURN);
            Review approvalReview = generateSubscribeReview(admin, i, ReviewStatus.APPROVAL);
            reviewIdList.add(returnReview.getId());
            reviewIdList.add(approvalReview.getId());
        });

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/reviews/best")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        List<BestReview> all = bestReviewRepository.findAllByOrderByLeakedOrderAsc();
        assertThat(all.size()).isEqualTo(6);
        assertThat(all.get(0).getReview().getStatus()).isEqualTo(ReviewStatus.APPROVAL);
        assertThat(all.get(0).getReview().getId()).isEqualTo(review.getId());

    }

    @Test
    @DisplayName("베스트 리뷰 신청할 리뷰 id 리스트가 잘못됨")
    public void createBestReview_wrongId() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        List<Long> reviewIdList = new ArrayList<>();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview requestReview = generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
            reviewIdList.add(requestReview.getId());
            reviewIdList.add(adminReview.getId());
        });

        Review review = generateSubscribeReview(admin, 11, ReviewStatus.APPROVAL);
        generateBestReview(review, 1);

        IntStream.range(1,6).forEach(i -> {
            Review returnReview = generateSubscribeReview(member, i, ReviewStatus.RETURN);
            Review approvalReview = generateSubscribeReview(admin, i, ReviewStatus.APPROVAL);
            reviewIdList.add(returnReview.getId());
            reviewIdList.add(approvalReview.getId());
        });

        reviewIdList.add(9999L);

        ReviewIdListDto requestDto = ReviewIdListDto.builder()
                .reviewIdList(reviewIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/reviews/best")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("정상적으로 쿼리 하나 조회")
    public void queryReview() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview requestReview = generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
        });

        Review review = generateSubscribeReview(admin, 11, ReviewStatus.APPROVAL);
        generateBestReview(review, 1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("bestReview").value(true))
                .andDo(document("admin_query_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("create_best_reviews").description("베스트 리뷰 등록 링크"),
                                linkWithRel("approve_reviews").description("리뷰 승인 링크"),
                                linkWithRel("return_review").description("리뷰 반려 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 리뷰 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("reviewDto.id").description("리뷰 id"),
                                fieldWithPath("reviewDto.status").description("리뷰 상태 [REQUEST,RETURN,APPROVAL,ADMIN]"),
                                fieldWithPath("reviewDto.createdDate").description("리뷰 등록일"),
                                fieldWithPath("reviewDto.star").description("리뷰 평점"),
                                fieldWithPath("reviewDto.username").description("리뷰 작성자 이름"),
                                fieldWithPath("reviewDto.contents").description("리뷰 내용"),
                                fieldWithPath("imageUrlList[0].filename").description("리뷰 이미지 파일 이름"),
                                fieldWithPath("imageUrlList[0].url").description("리뷰 이미지 url"),
                                fieldWithPath("bestReview").description("베스트리뷰 여부 true/false"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.create_best_reviews.href").description("베스트 리뷰 등록 링크"),
                                fieldWithPath("_links.approve_reviews.href").description("리뷰 승인 링크"),
                                fieldWithPath("_links.return_review.href").description("리뷰 반려 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

    }

    @Test
    @DisplayName("정상적으로 쿼리 하나 조회-베스트 리뷰 false")
    public void queryReview_not_BestReview() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview requestReview = generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
        });

        Review review = generateSubscribeReview(admin, 11, ReviewStatus.APPROVAL);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("bestReview").value(false))
        ;

    }

    @Test
    @DisplayName("조회할 리뷰가 존재하지 않음 404")
    public void queryReview_not_found() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview requestReview = generateItemReview(member, item, i, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
        });

        Review review = generateSubscribeReview(admin, 11, ReviewStatus.APPROVAL);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/reviews/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }

    @Test
    @DisplayName("정상적으로 리뷰 반려")
    public void returnReview() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        ItemReview review = generateItemReview(member, item, 99, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));

        String returnReason = "적합하지않은 리뷰 입니다.";
        ReturnReviewDto requestDto = ReturnReviewDto.builder()
                .returnReason(returnReason)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/reviews/{id}/return", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("return_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_review").description("리뷰 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("반려할 리뷰 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestFields(
                                fieldWithPath("returnReason").description("반려 사유")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_review.href").description("리뷰 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));


        em.flush();
        em.clear();

        Review findReview = reviewRepository.findById(review.getId()).get();
        assertThat(findReview.getStatus()).isEqualTo(ReviewStatus.RETURN);
        assertThat(findReview.getReturnReason()).isEqualTo(returnReason);

    }

    @Test
    @DisplayName("반려할 리뷰가 이미 승인된 리뷰일 경우 400")
    public void returnReview_already_approval() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        ItemReview review = generateItemReview(member, item, 99, ReviewStatus.APPROVAL, LocalDate.now().minusDays(30L));

        String returnReason = "적합하지않은 리뷰 입니다.";
        ReturnReviewDto requestDto = ReturnReviewDto.builder()
                .returnReason(returnReason)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/reviews/{id}/return", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("반려할 리뷰가 존재하지않음 404")
    public void returnReview_404() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        ItemReview review = generateItemReview(member, item, 99, ReviewStatus.REQUEST, LocalDate.now().minusDays(30L));

        String returnReason = "적합하지않은 리뷰 입니다.";
        ReturnReviewDto requestDto = ReturnReviewDto.builder()
                .returnReason(returnReason)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/reviews/999999/return")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 베스트리뷰 리스트 조회하는 테스트")
    public void queryBestReviews() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(6,11).forEach(i -> {
            Review approvalReview = generateSubscribeReview(member, i, ReviewStatus.APPROVAL);
            generateBestReview(approvalReview, i);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
            generateBestReview(adminReview, i);
        });

       //when & then
        mockMvc.perform(get("/api/admin/reviews/best")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryAdminBestReviewsDtoList", hasSize(10)))

                .andDo(document("query_admin_best_reviews",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_leakedOrder").description("베스트 리뷰 순서 수정 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].id").description("베스트 리뷰 id"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].leakedOrder").description("노출 순서"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].reviewId").description("리뷰 id"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].title").description("리뷰 상품 이름"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].star").description("리뷰 평점"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].contents").description("리뷰 내용"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].createdDate").description("리뷰 등록날짜"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].name").description("리뷰 작성자 이름"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0].email").description("작성자 이메일"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0]._links.query_review.href").description("리뷰 하나 조회 링크"),
                                fieldWithPath("_embedded.queryAdminBestReviewsDtoList[0]._links.delete_best_review.href").description("베스트 리뷰 삭제 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_leakedOrder.href").description("베스트 리뷰 순서 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 베스트 리뷰 삭제")
    public void deleteBestReview() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(6,11).forEach(i -> {
            Review approvalReview = generateSubscribeReview(member, i, ReviewStatus.APPROVAL);
            generateBestReview(approvalReview, i);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
            generateBestReview(adminReview, i);
        });

        BestReview bestReview = bestReviewRepository.findByLeakedOrder(3).get();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/reviews/{id}/best", bestReview.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_delete_best_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_best_reviews").description("베스트 리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("삭제할 베스트 리뷰 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_best_reviews.href").description("베스트 리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Optional<BestReview> optionalBestReview = bestReviewRepository.findById(bestReview.getId());
        assertThat(optionalBestReview.isPresent()).isFalse();

        List<BestReview> bestReviews = bestReviewRepository.findAllByOrderByLeakedOrderAsc();
        assertThat(bestReviews.size()).isEqualTo(9);
        int leakedOrder = 1;
        for (BestReview review : bestReviews) {
            assertThat(review.getLeakedOrder()).isEqualTo(leakedOrder++);
        }

    }

    @Test
    @DisplayName("삭제할 베스트 리뷰가 존재하지않음 404")
    public void deleteBestReview_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(6,11).forEach(i -> {
            Review approvalReview = generateSubscribeReview(member, i, ReviewStatus.APPROVAL);
            generateBestReview(approvalReview, i);
        });

        IntStream.range(1,6).forEach(i -> {
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN, LocalDate.now().minusDays(30L));
            generateBestReview(adminReview, i);
        });

        BestReview bestReview = bestReviewRepository.findByLeakedOrder(3).get();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/reviews/999999/best")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("정상적으로 베스트 리뷰 순서 변경")
    public void updateBestReviewLeakedOrder() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<UpdateBestReviewLeakedOrderDto.LeakedOrderDto> leakedOrderDtoList = new ArrayList<>();

        Review approvalReview1 = generateSubscribeReview(member, 1, ReviewStatus.APPROVAL);
        Review approvalReview2 = generateSubscribeReview(member, 2, ReviewStatus.APPROVAL);
        Review approvalReview3 = generateSubscribeReview(member, 3, ReviewStatus.APPROVAL);
        BestReview bestReview1to2 = generateBestReview(approvalReview1, 1);
        BestReview bestReview2to3 = generateBestReview(approvalReview2, 2);
        BestReview bestReview3to1 = generateBestReview(approvalReview3, 3);
        UpdateBestReviewLeakedOrderDto.LeakedOrderDto leakedOrderDto1 = getLeakedOrderDto(bestReview1to2, 2);
        UpdateBestReviewLeakedOrderDto.LeakedOrderDto leakedOrderDto2 = getLeakedOrderDto(bestReview2to3, 3);
        UpdateBestReviewLeakedOrderDto.LeakedOrderDto leakedOrderDto3 = getLeakedOrderDto(bestReview3to1, 1);
        leakedOrderDtoList.add(leakedOrderDto1);
        leakedOrderDtoList.add(leakedOrderDto2);
        leakedOrderDtoList.add(leakedOrderDto3);
        
        em.flush();
        em.clear();

        BestReview beforeChange1 = bestReviewRepository.findById(bestReview1to2.getId()).get();
        BestReview beforeChange2 = bestReviewRepository.findById(bestReview2to3.getId()).get();
        BestReview beforeChange3 = bestReviewRepository.findById(bestReview3to1.getId()).get();
        assertThat(beforeChange1.getLeakedOrder()).isEqualTo(1);
        assertThat(beforeChange2.getLeakedOrder()).isEqualTo(2);
        assertThat(beforeChange3.getLeakedOrder()).isEqualTo(3);


        UpdateBestReviewLeakedOrderDto requestDto = UpdateBestReviewLeakedOrderDto.builder()
                .leakedOrderDtoList(leakedOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/reviews/best/leakedOrder")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_best_reviews_leakedOrder",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_best_reviews").description("베스트 리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestFields(
                                fieldWithPath("leakedOrderDtoList[0].id").description("베스트 리뷰 id"),
                                fieldWithPath("leakedOrderDtoList[0].leakedOrder").description("변경 시킬 노출 순서")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_best_reviews.href").description("베스트 리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        ;

        em.flush();
        em.clear();

        BestReview afterChange1 = bestReviewRepository.findById(bestReview1to2.getId()).get();
        BestReview afterChange2 = bestReviewRepository.findById(bestReview2to3.getId()).get();
        BestReview afterChange3 = bestReviewRepository.findById(bestReview3to1.getId()).get();
        assertThat(afterChange1.getLeakedOrder()).isEqualTo(2);
        assertThat(afterChange2.getLeakedOrder()).isEqualTo(3);
        assertThat(afterChange3.getLeakedOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("베스트 리뷰 순서 변경 시 존재하지않는 베스트 리뷰 id일 경우 400")
    public void updateBestReviewLeakedOrder_wrongId() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<UpdateBestReviewLeakedOrderDto.LeakedOrderDto> leakedOrderDtoList = new ArrayList<>();

        Review approvalReview1 = generateSubscribeReview(member, 1, ReviewStatus.APPROVAL);
        Review approvalReview2 = generateSubscribeReview(member, 2, ReviewStatus.APPROVAL);
        Review approvalReview3 = generateSubscribeReview(member, 3, ReviewStatus.APPROVAL);
        BestReview bestReview1to2 = generateBestReview(approvalReview1, 1);
        BestReview bestReview2to3 = generateBestReview(approvalReview2, 2);
        BestReview bestReview3to1 = generateBestReview(approvalReview3, 3);
        UpdateBestReviewLeakedOrderDto.LeakedOrderDto leakedOrderDto1 = getLeakedOrderDto(bestReview1to2, 2);
        UpdateBestReviewLeakedOrderDto.LeakedOrderDto leakedOrderDto2 = getLeakedOrderDto(bestReview2to3, 3);
        UpdateBestReviewLeakedOrderDto.LeakedOrderDto leakedOrderDto3 = getLeakedOrderDto(bestReview3to1, 1);
        UpdateBestReviewLeakedOrderDto.LeakedOrderDto wrongId = UpdateBestReviewLeakedOrderDto.LeakedOrderDto.builder()
                .id(9999L)
                .leakedOrder(4)
                .build();
        leakedOrderDtoList.add(leakedOrderDto1);
        leakedOrderDtoList.add(leakedOrderDto2);
        leakedOrderDtoList.add(leakedOrderDto3);
        leakedOrderDtoList.add(wrongId);

        UpdateBestReviewLeakedOrderDto requestDto = UpdateBestReviewLeakedOrderDto.builder()
                .leakedOrderDtoList(leakedOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/reviews/best/leakedOrder")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }







    private UpdateBestReviewLeakedOrderDto.LeakedOrderDto getLeakedOrderDto(BestReview bestReview1to2, int leakedOrder) {
        UpdateBestReviewLeakedOrderDto.LeakedOrderDto leakedOrderDto = UpdateBestReviewLeakedOrderDto.LeakedOrderDto.builder()
                .id(bestReview1to2.getId())
                .leakedOrder(leakedOrder)
                .build();
        return leakedOrderDto;
    }


    private BestReview generateBestReview(Review review, int i) {
        BestReview bestReview = BestReview.builder()
                .review(review)
                .leakedOrder(i)
                .build();
        return bestReviewRepository.save(bestReview);
    }


    private Review generateSubscribeReview(Member member, int i, ReviewStatus reviewStatus) {
        List<Recipe> recipes = recipeRepository.findAll();

        Subscribe subscribe = Subscribe.builder()
                .build();
        subscribeRepository.save(subscribe);

        Dog dog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE,
                1, 1, SnackCountLevel.NORMAL, recipes.get(0));
        dog.setSubscribe(subscribe);

        SubscribeReview subscribeReview = SubscribeReview.builder()
                .member(member)
                .writtenDate(LocalDate.now().minusDays(i))
                .username(member.getName())
                .star(3)
                .contents("열글자 이상의 구독 리뷰"+i)
                .status(reviewStatus)
                .returnReason("상품에 맞지 않은 리뷰 내용"+i)
                .subscribe(subscribe)
                .returnReason("적합하지않은 리뷰 내용")
                .build();
        reviewRepository.save(subscribeReview);

        IntStream.range(1,4).forEach(j -> {
            generateReviewImage(j, subscribeReview);
        });

        return subscribeReview;
    }

    private ItemReview generateItemReview(Member member, Item item, int i, ReviewStatus reviewStatus, LocalDate writtenDate) {
        ItemReview itemReview = ItemReview.builder()
                .member(member)
                .writtenDate(writtenDate)
                .username(member.getName())
                .star((i + 5) % 5)
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

    private ItemReview generateItemReview_Over50_EmptyImage(Member member, Item item, int i, ReviewStatus reviewStatus) {
        ItemReview itemReview = ItemReview.builder()
                .member(member)
                .writtenDate(LocalDate.now().minusDays(30L))
                .username(member.getName())
                .star((i + 5) % 5)
                .contents("오십글자 이상의 내용 오십글자 이상의 내용 오십글자 이상의 내용 오십글자 이상의 내용 오십글자 이상의 내용 "+i)
                .status(reviewStatus)
                .item(item)
                .build();
        itemReviewRepository.save(itemReview);

        return itemReview;
    }

    private ItemReview generateItemReview_OnlyImage(Member member, Item item, int i, ReviewStatus reviewStatus) {
        ItemReview itemReview = generateItemReview(member, item, i, reviewStatus, LocalDate.now().minusDays(30L));

        return itemReview;
    }

    private ItemReview generateItemReview_Over50AndImage(Member member, Item item, int i, ReviewStatus reviewStatus) {
        ItemReview itemReview = ItemReview.builder()
                .member(member)
                .writtenDate(LocalDate.now().minusDays(30L))
                .username(member.getName())
                .star((i + 5) % 5)
                .contents("오십글자 이상의 내용 오십글자 이상의 내용 오십글자 이상의 내용 오십글자 이상의 내용 오십글자 이상의 내용 "+i)
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


    private void generateWriteableReviewSubscribe(Member member) {
        List<Recipe> recipes = recipeRepository.findAll();

        Subscribe savedSubscribe = generateSubscribe();

        Order savedOrder = generateOrder(member, savedSubscribe);

        Dog savedDog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE,
                1, 1, SnackCountLevel.NORMAL, recipes.get(0));
        savedDog.setSubscribe(savedSubscribe);
    }


    private List<Long> getImageIdList() {
        List<Long> imageIdList = new ArrayList<>();

        IntStream.range(1,4).forEach(i -> {
            ReviewImage image = generateReviewImage(i);
            imageIdList.add(image.getId());
        });
        return imageIdList;
    }

    private ReviewImage generateReviewImage(int i) {
        ReviewImage reviewImage = ReviewImage.builder()
                .folder("folder" + i)
                .filename("filename" + i +".jpg")
                .build();
        return reviewImageRepository.save(reviewImage);
    }


    private void generateOrderItemsAndOrder(Member member, Item item1, Item item2) {
        Order order = GeneralOrder.builder()
                .member(member)
                .build();
        Order savedOrder = orderRepository.save(order);


        generateOrderItem(item1, (GeneralOrder) savedOrder);
        generateOrderItem(item2, (GeneralOrder) savedOrder);
    }

    private void generateOrderItem(Item item1, GeneralOrder savedOrder) {
        OrderItem orderItem = OrderItem.builder()
                .generalOrder(savedOrder)
                .item(item1)
                .status(OrderStatus.CONFIRM)
                .build();
        orderItemRepository.save(orderItem);
    }

    private Order generateOrder(Member member, Subscribe savedSubscribe) {
        Order order = SubscribeOrder.builder()
                .member(member)
                .subscribe(savedSubscribe)
                .build();
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    private Subscribe generateSubscribe() {
        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.SUBSCRIBING)
                .writeableReview(true)
                .build();
        return subscribeRepository.save(subscribe);
    }

    private Item generateItem(int i) {
        return generateItem(i, ItemType.RAW);
    }

    private Item generateItem(int i, ItemType itemType) {
        Item item = Item.builder()
                .itemType(itemType)
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


    private ItemImage generateItemImage(Item item, int i) {
        ItemImage itemImage = ItemImage.builder()
                .item(item)
                .leakOrder(i)
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        return itemImageRepository.save(itemImage);
    }



    private Dog generateDog(Member member, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel, Recipe recipe) {
        Dog dog = Dog.builder()
                .member(member)
                .name("구독강아지")
                .startAgeMonth(startAgeMonth)
                .gender(Gender.MALE)
                .oldDog(false)
                .dogSize(dogSize)
                .weight(new BigDecimal(weight))
                .dogActivity(new DogActivity(activitylevel, walkingCountPerWeek, walkingTimePerOneTime))
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(snackCountLevel)
                .recommendRecipe(recipe)
                .build();
        return dogRepository.save(dog);
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