package com.bi.barfdog.api;

import com.bi.barfdog.api.reviewDto.ApprovalReviewsRequestDto;
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
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.orderItem.OrderItemStatus;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.review.*;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.RecipeRepository;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.review.ItemReviewRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.repository.review.SubscribeReviewRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
            generateItemReview(member, item, i, ReviewStatus.REQUEST);
            generateItemReview(admin, item, i, ReviewStatus.ADMIN);
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
                        .param("status", "ALL")
                        .param("order", "desc"))
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
                                linkWithRel("approval_reviews").description("리뷰 승인 링크"),
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
                                parameterWithName("order").description("정렬 옵션 ['desc' or 'asc'] desc=최신순")
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
                                fieldWithPath("_links.approval_reviews.href").description("리뷰 승인 링크"),
                                fieldWithPath("_links.create_best_reviews.href").description("베스트 리뷰 선정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

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
            generateItemReview(member, item, i, ReviewStatus.REQUEST);
            generateItemReview(admin, item, i, ReviewStatus.ADMIN);
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
            generateItemReview(member, item, i, ReviewStatus.REQUEST);
            generateItemReview(admin, item, i, ReviewStatus.ADMIN);
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
            generateItemReview(member, item, i, ReviewStatus.REQUEST);
            generateItemReview(admin, item, i, ReviewStatus.ADMIN);
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
            generateItemReview(member, item, i, ReviewStatus.REQUEST);
            generateItemReview(admin, item, i, ReviewStatus.ADMIN);
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
            ItemReview itemReview = generateItemReview(member, item, i, ReviewStatus.REQUEST);
            SubscribeReview subscribeReview = (SubscribeReview) generateSubscribeReview(member, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
            reviewIdList.add(subscribeReview.getId());
        });

        ApprovalReviewsRequestDto requestDto = ApprovalReviewsRequestDto.builder()
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

    }

    @Test
    @DisplayName("리뷰 승인 50자 이상 적립금 300")
    public void approvalReviews_over50() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<Long> reviewIdList = new ArrayList<>();
        IntStream.range(1,3).forEach(i -> {
            ItemReview itemReview = generateItemReview(member, item, i, ReviewStatus.REQUEST);
            SubscribeReview subscribeReview = (SubscribeReview) generateSubscribeReview(member, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
            reviewIdList.add(subscribeReview.getId());
        });

        ApprovalReviewsRequestDto requestDto = ApprovalReviewsRequestDto.builder()
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

    }

    @Test
    @DisplayName("리뷰 승인 이미지 삽입 적립금 500")
    public void approvalReviews_Image() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<Long> reviewIdList = new ArrayList<>();
        IntStream.range(1,3).forEach(i -> {
            ItemReview itemReview = generateItemReview(member, item, i, ReviewStatus.REQUEST);
            SubscribeReview subscribeReview = (SubscribeReview) generateSubscribeReview(member, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
            reviewIdList.add(subscribeReview.getId());
        });

        ApprovalReviewsRequestDto requestDto = ApprovalReviewsRequestDto.builder()
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

    }

    @Test
    @DisplayName("리뷰 승인 이미지 삽입 50글자 이상 적립금 800")
    public void approvalReviews_Image() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        List<Long> reviewIdList = new ArrayList<>();
        IntStream.range(1,3).forEach(i -> {
            ItemReview itemReview = generateItemReview(member, item, i, ReviewStatus.REQUEST);
            SubscribeReview subscribeReview = (SubscribeReview) generateSubscribeReview(member, i, ReviewStatus.REQUEST);
            reviewIdList.add(itemReview.getId());
            reviewIdList.add(subscribeReview.getId());
        });

        ApprovalReviewsRequestDto requestDto = ApprovalReviewsRequestDto.builder()
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
                .build();
        reviewRepository.save(subscribeReview);

        IntStream.range(1,4).forEach(j -> {
            generateReviewImage(j, subscribeReview);
        });

        return subscribeReview;
    }

    private ItemReview generateItemReview(Member member, Item item, int i, ReviewStatus reviewStatus) {
        ItemReview itemReview = ItemReview.builder()
                .member(member)
                .writtenDate(LocalDate.now().minusDays(30L))
                .username(member.getName())
                .star((i + 5) % 5)
                .contents("열글자 이상의 내용"+i)
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
        savedSubscribe.setOrder((SubscribeOrder) savedOrder);

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
                .status(OrderItemStatus.CONFIRM)
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