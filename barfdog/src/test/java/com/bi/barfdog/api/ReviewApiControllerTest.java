package com.bi.barfdog.api;

import com.bi.barfdog.api.reviewDto.ReviewType;
import com.bi.barfdog.api.reviewDto.UpdateReviewDto;
import com.bi.barfdog.api.reviewDto.WriteReviewDto;
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
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ReviewApiControllerTest extends BaseTest {

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
    BestReviewRepository bestReviewRepository;


    @Test
    @DisplayName("작성가능한 후기 리스트 조회")
    public void queryWriteableReviews() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        em.flush();
        em.clear();

        //when & then
        mockMvc.perform(get("/api/reviews/writeable")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page","1")
                        .param("size","5")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(12))
                .andDo(document("query_writeable_reviews",
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
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryWriteableReviewsDtoList[0].id").description("주문한 아이템 id or 구독 id"),
                                fieldWithPath("_embedded.queryWriteableReviewsDtoList[0].targetId").description("리뷰 대상(아이템 id or 레시피 id)"),
                                fieldWithPath("_embedded.queryWriteableReviewsDtoList[0].reviewType").description("리뷰 타입 [ITEM,SUBSCRIBE]"),
                                fieldWithPath("_embedded.queryWriteableReviewsDtoList[0].imageUrl").description("이미지 url"),
                                fieldWithPath("_embedded.queryWriteableReviewsDtoList[0].title").description("제목"),
                                fieldWithPath("_embedded.queryWriteableReviewsDtoList[0].orderedDate").description("주문 날짜"),
                                fieldWithPath("_embedded.queryWriteableReviewsDtoList[0]._links.write_review.href").description("리뷰 쓰기 링크"),
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
    @DisplayName("페이지 정보 없으면 0페이지 20개 조회")
    public void queryWriteableReviews_noPage() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        em.flush();
        em.clear();

        //when & then
        mockMvc.perform(get("/api/reviews/writeable")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryWriteableReviewsDtoList", hasSize(12)))
                .andExpect(jsonPath("page.totalElements").value(12));
    }

    @Test
    @DisplayName("정상적으로 리뷰 이미지 업로드")
    public void uploadImage() throws Exception {
       //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));


        //when & then
        mockMvc.perform(multipart("/api/reviews/upload")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("url").exists())
                .andDo(document("upload_reviewImage",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParts(
                                partWithName("file").description("업로드할 리뷰 이미지 파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("리뷰 이미지 인덱스 id"),
                                fieldWithPath("url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<ReviewImage> imageALl = reviewImageRepository.findAll();
        assertThat(imageALl.size()).isEqualTo(1);


    }

    @Test
    @DisplayName("업로드할 리뷰 이미지 파일 없을 경우 400")
    public void uploadImage_emptyFile() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));


        //when & then
        mockMvc.perform(multipart("/api/reviews/upload")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 일반상품 리뷰 등록")
    public void writeReview() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<OrderItem> orderItems = orderItemRepository.findWriteableByMember(member);
        assertThat(orderItems.size()).isEqualTo(6);
        OrderItem orderItem = orderItems.get(0);

        List<Long> imageIdList = getImageIdList();

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.ITEM)
                .id(orderItem.getId())
                .targetId(orderItem.getItem().getId())
                .star(5)
                .contents("열글자 이상의 글자")
                .reviewImageIdList(imageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("write_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_reviews").description("작성한 리뷰 리스트 호출 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("reviewType").description("리뷰 타입 [ITEM or SUBSCRIBE]"),
                                fieldWithPath("id").description("주문한 상품id or 구독id [orderItemId or subscribeId]"),
                                fieldWithPath("targetId").description("리뷰 대상 id [itemId or recipeId]"),
                                fieldWithPath("star").description("별 개수 1~5 INT"),
                                fieldWithPath("contents").description("내용 10글자 이상"),
                                fieldWithPath("reviewImageIdList").description("리뷰 이미지 id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_reviews.href").description("작성한 리뷰 리스트 호출 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        List<ItemReview> itemReviews = itemReviewRepository.findAll();
        assertThat(itemReviews.size()).isEqualTo(1);
        ItemReview itemReview = itemReviews.get(0);
        assertThat(itemReview.getStatus()).isEqualTo(ReviewStatus.REQUEST);
        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(itemReview);
        assertThat(reviewImages.size()).isEqualTo(imageIdList.size());

        OrderItem findOrderItem = orderItemRepository.findById(orderItem.getId()).get();
        assertThat(findOrderItem.isWriteableReview()).isFalse();

    }

    @Test
    @DisplayName("정상적으로 구독 리뷰 등록")
    public void writeReview_subscribe() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<Subscribe> subscribes = subscribeRepository.findWriteableByMember(member);
        assertThat(subscribes.size()).isEqualTo(6);
        Subscribe subscribe = subscribes.get(0);
        Recipe recipe = subscribe.getDog().getRecommendRecipe();

        List<Long> imageIdList = getImageIdList();

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.SUBSCRIBE)
                .id(subscribe.getId())
                .targetId(recipe.getId())
                .star(5)
                .contents("열글자 이상의 글자")
                .reviewImageIdList(imageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        em.flush();
        em.clear();

        List<SubscribeReview> subscribeReviews = subscribeReviewRepository.findAll();
        assertThat(subscribeReviews.size()).isEqualTo(1);
        SubscribeReview subscribeReview = subscribeReviews.get(0);
        assertThat(subscribeReview.getStatus()).isEqualTo(ReviewStatus.REQUEST);

        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(subscribeReview);
        assertThat(reviewImages.size()).isEqualTo(imageIdList.size());

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        assertThat(findSubscribe.isWriteableReview()).isFalse();
    }

    @Test
    @DisplayName("이미지 없어도 리뷰 작성 성공")
    public void writeReview_noImage() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<OrderItem> orderItems = orderItemRepository.findWriteableByMember(member);
        assertThat(orderItems.size()).isEqualTo(6);
        OrderItem orderItem = orderItems.get(0);

        List<Long> imageIdList = getImageIdList();

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.ITEM)
                .id(orderItem.getId())
                .targetId(orderItem.getItem().getId())
                .star(5)
                .contents("열글자 이상의 글자")
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        em.flush();
        em.clear();

        List<ItemReview> itemReviews = itemReviewRepository.findAll();
        assertThat(itemReviews.size()).isEqualTo(1);
        ItemReview itemReview = itemReviews.get(0);
        assertThat(itemReview.getStatus()).isEqualTo(ReviewStatus.REQUEST);

        OrderItem findOrderItem = orderItemRepository.findById(orderItem.getId()).get();
        assertThat(findOrderItem.isWriteableReview()).isFalse();
    }

    @Test
    @DisplayName("리뷰 작성 시 10글자 이하일 경우 400")
    public void writeReview_wrongContents() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<OrderItem> orderItems = orderItemRepository.findWriteableByMember(member);
        assertThat(orderItems.size()).isEqualTo(6);
        OrderItem orderItem = orderItems.get(0);

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.ITEM)
                .id(orderItem.getId())
                .targetId(orderItem.getItem().getId())
                .star(5)
                .contents("열글자 이하")
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 작성 요청 값 부족 시 400")
    public void writeReview_badRequest() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<OrderItem> orderItems = orderItemRepository.findWriteableByMember(member);
        assertThat(orderItems.size()).isEqualTo(6);
        OrderItem orderItem = orderItems.get(0);

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.ITEM)
                .id(orderItem.getId())
                .contents("열글자 이하")
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 등록시 주문아이템 id 잘못됨 400")
    public void writeReview_wrongOrderItemId() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<OrderItem> orderItems = orderItemRepository.findWriteableByMember(member);
        assertThat(orderItems.size()).isEqualTo(6);
        OrderItem orderItem = orderItems.get(0);

        List<Long> imageIdList = getImageIdList();

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.ITEM)
                .id(9999L)
                .targetId(orderItem.getItem().getId())
                .star(5)
                .contents("열글자 이상의 글자")
                .reviewImageIdList(imageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 등록시 구독 id 잘못됨 400")
    public void writeReview_wrongSubscribeId() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<Subscribe> subscribes = subscribeRepository.findWriteableByMember(member);
        assertThat(subscribes.size()).isEqualTo(6);
        Subscribe subscribe = subscribes.get(0);
        Recipe recipe = subscribe.getDog().getRecommendRecipe();

        List<Long> imageIdList = getImageIdList();

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.SUBSCRIBE)
                .id(9999L)
                .targetId(recipe.getId())
                .star(5)
                .contents("열글자 이상의 글자")
                .reviewImageIdList(imageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 등록시 item id 잘못됨 400")
    public void writeReview_wrongItemId() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<OrderItem> orderItems = orderItemRepository.findWriteableByMember(member);
        assertThat(orderItems.size()).isEqualTo(6);
        OrderItem orderItem = orderItems.get(0);

        List<Long> imageIdList = getImageIdList();

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.SUBSCRIBE)
                .id(orderItem.getId())
                .targetId(9999L)
                .star(5)
                .contents("열글자 이상의 글자")
                .reviewImageIdList(imageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("리뷰 등록시 레시피 id 잘못됨 400")
    public void writeReview_wrongRecipeId() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        Item item2 = generateItem(2);
        Item item3 = generateItem(3);
        Item item4 = generateItem(4);

        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item1, i);
            generateItemImage(item2, i);
            generateItemImage(item3, i);
            generateItemImage(item4, i);
        });

        IntStream.range(1,4).forEach(i -> {
            generateOrderItemsAndOrder(member, item3, item4);
            generateOrderItemsAndOrder(admin, item1, item2);
        });

        IntStream.range(1,7).forEach(i -> {
            generateWriteableReviewSubscribe(member);
        });

        List<Subscribe> subscribes = subscribeRepository.findWriteableByMember(member);
        assertThat(subscribes.size()).isEqualTo(6);
        Subscribe subscribe = subscribes.get(0);
        Recipe recipe = subscribe.getDog().getRecommendRecipe();

        List<Long> imageIdList = getImageIdList();

        WriteReviewDto requestDto = WriteReviewDto.builder()
                .reviewType(ReviewType.SUBSCRIBE)
                .id(subscribe.getId())
                .targetId(9999L)
                .star(5)
                .contents("열글자 이상의 글자")
                .reviewImageIdList(imageIdList)
                .build();

        //when & then
        mockMvc.perform(post("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("작성한 후기 리스트 조회")
    public void queryReviews() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,5).forEach(i -> {
            generateItemReview(member, item, i);
            generateItemReview(admin, item, i);
        });

        IntStream.range(1,11).forEach(i -> {
            generateSubscribeReview(member, i);
            generateSubscribeReview(admin, i);
        });

        //when & then
        mockMvc.perform(get("/api/reviews")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(14))
                .andDo(document("query_reviews",
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
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryReviewsDtoList[0].id").description("리뷰 id"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].thumbnailUrl").description("상품 썸네일 url"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].title").description("상품 제목"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].star").description("평점 1~5 int"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].contents").description("리뷰 내용"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].createdDate").description("리뷰 작성일"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].imageUrl").description("리뷰 대표 이미지 url"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].imageCount").description("리뷰 이미지 개수"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].status").description("리뷰 상태 [REQUEST,RETURN,APPROVAL,ADMIN]"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0].returnReason").description("반려 사유"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0]._links.query_review_images.href").description("리뷰 이미지 리스트 조회 링크"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0]._links.query_review.href").description("수정할 리뷰 하나 조회 링크"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0]._links.delete_review.href").description("리뷰 삭제 링크"),
                                fieldWithPath("_embedded.queryReviewsDtoList[0]._links.update_review.href").description("리뷰 수정 링크"),
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
                ));
    }


    @Test
    @DisplayName("정상적으로 리뷰 이미지 조회하는 테스트")
    public void queryReviewImages() throws Exception {
       //given
        ItemReview review = ItemReview.builder()
                .build();
        reviewRepository.save(review);

        IntStream.range(1,4).forEach(i -> {
            generateReviewImage(i, review);
        });

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/{id}/images", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_review_images",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        pathParameters(
                                parameterWithName("id").description("리뷰 id")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryReviewImagesDtoList[0].filename").description("파일 이름"),
                                fieldWithPath("_embedded.queryReviewImagesDtoList[0].url").description("이미지 url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @DisplayName("리뷰이미지 리스트를 조회할 리뷰가 없을 경우 404")
    public void queryReviewImages_404() throws Exception {
        //given
        ItemReview review = ItemReview.builder()
                .build();
        reviewRepository.save(review);

        IntStream.range(1,4).forEach(i -> {
            generateReviewImage(i, review);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/999999/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("정상적으로 수정할 아이템 리뷰 하나 조회")
    public void queryReview_item() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
        });

        ItemReview review = ItemReview.builder()
                .username(member.getName())
                .item(item)
                .writtenDate(LocalDate.now())
                .contents("열글자 이상의 내용 작성")
                .build();
        reviewRepository.save(review);

        IntStream.range(1,4).forEach(i -> {
            generateReviewImage(i, review);
        });

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_review").description("리뷰 수정 링크"),
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
                        pathParameters(
                                parameterWithName("id").description("리뷰 id")
                        ),
                        responseFields(
                                fieldWithPath("reviewDto.id").description("리뷰 id"),
                                fieldWithPath("reviewDto.title").description("리뷰 대상 상품 이름"),
                                fieldWithPath("reviewDto.name").description("리뷰 작성자 이름"),
                                fieldWithPath("reviewDto.thumbnailUrl").description("리뷰 대상 상품 썸네일"),
                                fieldWithPath("reviewDto.writtenDate").description("리뷰 작성일"),
                                fieldWithPath("reviewDto.star").description("리뷰 평점 1~5 int"),
                                fieldWithPath("reviewDto.contents").description("리뷰 내용 10 글자 이상"),
                                fieldWithPath("reviewImageDtoList[0].id").description("리뷰 이미지 id"),
                                fieldWithPath("reviewImageDtoList[0].filename").description("리뷰 이미지 파일 이름"),
                                fieldWithPath("reviewImageDtoList[0].url").description("리뷰 이미지 url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_review.href").description("리뷰 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @DisplayName("정상적으로 수정할 구독 리뷰 하나 조회")
    public void queryReview_subscribe() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Review review = generateSubscribeReview(member,1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;

    }

    @Test
    @DisplayName("수정할 리뷰 존재하지않음 404")
    public void queryReview_404() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Review review = generateSubscribeReview(member,1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/999999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }
    
    @Test
    @DisplayName("정상적으로 리뷰 하나 삭제")
    public void deleteReview() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Review review = generateSubscribeReview(member,1);
       
       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_reviews").description("작성한 리뷰 리스트 조회 링크"),
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
                        pathParameters(
                                parameterWithName("id").description("리뷰 id")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_reviews.href").description("작성한 리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<ReviewImage> reviewImages = reviewImageRepository.findAll();
        assertThat(reviewImages.size()).isEqualTo(0);

        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews.size()).isEqualTo(0);


    }

    @Test
    @DisplayName("본인이 작성하지 않은 리뷰 삭제 시 403")
    public void deleteReview_forbidden() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Review review = generateSubscribeReview(member,1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Review subscribeReview = generateSubscribeReview(admin, 1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/reviews/{id}", subscribeReview.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;

    }

    @Test
    @DisplayName("삭제하려는 리뷰가 존재하지 않음")
    public void deleteReview_notFound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Review review = generateSubscribeReview(member,1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/reviews/999999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("정상적으로 리뷰 수정 완료")
    public void updateReview() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
        });

        ItemReview review = ItemReview.builder()
                .member(member)
                .username(member.getName())
                .item(item)
                .writtenDate(LocalDate.now())
                .contents("열글자 이상의 내용 작성")
                .status(ReviewStatus.RETURN)
                .build();
        reviewRepository.save(review);

        generateReviewImage(1, review);
        generateReviewImage(2, review);
        ReviewImage deleteImage1 = generateReviewImage(3, review);
        ReviewImage deleteImage2 = generateReviewImage(4, review);

        List<Long> deleteImageIdList = new ArrayList<>();

        deleteImageIdList.add(deleteImage1.getId());
        deleteImageIdList.add(deleteImage2.getId());


        List<Long> addImageIdList = new ArrayList<>();
        ReviewImage addImage1 = generateReviewImage(5);
        ReviewImage addImage2 = generateReviewImage(6);
        ReviewImage addImage3 = generateReviewImage(7);
        addImageIdList.add(addImage1.getId());
        addImageIdList.add(addImage2.getId());
        addImageIdList.add(addImage3.getId());

        int star = 4;
        String contents = "수정할 내용 열글자 이상";

        UpdateReviewDto requestDto = UpdateReviewDto.builder()
                .star(star)
                .contents(contents)
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_review",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_reviews").description("작성한 리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("리뷰 id")
                        ),
                        requestFields(
                                fieldWithPath("star").description("리뷰 평점 1~5 int"),
                                fieldWithPath("contents").description("리뷰 내용 10글자 이상"),
                                fieldWithPath("addImageIdList").description("추가할 리뷰 이미지 id 리스트"),
                                fieldWithPath("deleteImageIdList").description("삭제할 리뷰 이미지 id 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                                ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_reviews.href").description("작성한 리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        assertThat(reviewImages.size()).isEqualTo(5);

        Review findReview = reviewRepository.findById(review.getId()).get();
        assertThat(findReview.getStar()).isEqualTo(star);
        assertThat(findReview.getContents()).isEqualTo(contents);
        assertThat(findReview.getStatus()).isEqualTo(ReviewStatus.REQUEST);
    }

    @Test
    @DisplayName("수정할 리뷰 존재하지 않음 404")
    public void updateReview_notFound() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
        });

        ItemReview review = ItemReview.builder()
                .member(member)
                .username(member.getName())
                .item(item)
                .writtenDate(LocalDate.now())
                .contents("열글자 이상의 내용 작성")
                .build();
        reviewRepository.save(review);

        generateReviewImage(1, review);
        generateReviewImage(2, review);
        ReviewImage deleteImage1 = generateReviewImage(3, review);
        ReviewImage deleteImage2 = generateReviewImage(4, review);

        List<Long> deleteImageIdList = new ArrayList<>();

        deleteImageIdList.add(deleteImage1.getId());
        deleteImageIdList.add(deleteImage2.getId());


        List<Long> addImageIdList = new ArrayList<>();
        ReviewImage addImage1 = generateReviewImage(5);
        ReviewImage addImage2 = generateReviewImage(6);
        ReviewImage addImage3 = generateReviewImage(7);
        addImageIdList.add(addImage1.getId());
        addImageIdList.add(addImage2.getId());
        addImageIdList.add(addImage3.getId());

        int star = 4;
        String contents = "수정할 내용 열글자 이상";

        UpdateReviewDto requestDto = UpdateReviewDto.builder()
                .star(star)
                .contents(contents)
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/reviews/999999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());


    }

    @Test
    @DisplayName("리뷰 수정 시 값 부족할 경우 400")
    public void updateReview_badRequest() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
        });

        ItemReview review = ItemReview.builder()
                .member(member)
                .username(member.getName())
                .item(item)
                .writtenDate(LocalDate.now())
                .contents("열글자 이상의 내용 작성")
                .build();
        reviewRepository.save(review);

        generateReviewImage(1, review);
        generateReviewImage(2, review);
        ReviewImage deleteImage1 = generateReviewImage(3, review);
        ReviewImage deleteImage2 = generateReviewImage(4, review);

        List<Long> deleteImageIdList = new ArrayList<>();

        deleteImageIdList.add(deleteImage1.getId());
        deleteImageIdList.add(deleteImage2.getId());


        List<Long> addImageIdList = new ArrayList<>();
        ReviewImage addImage1 = generateReviewImage(5);
        ReviewImage addImage2 = generateReviewImage(6);
        ReviewImage addImage3 = generateReviewImage(7);
        addImageIdList.add(addImage1.getId());
        addImageIdList.add(addImage2.getId());
        addImageIdList.add(addImage3.getId());

        int star = 4;
        String contents = "수정할 내용 열글자 이상";

        UpdateReviewDto requestDto = UpdateReviewDto.builder()
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수정하려는 리뷰가 내가 쓴 리뷰가 아닐 경우 forbidden")
    public void updateReview_forbidden() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, i);
        });

        ItemReview review = ItemReview.builder()
                .member(admin)
                .username(admin.getName())
                .item(item)
                .writtenDate(LocalDate.now())
                .contents("열글자 이상의 내용 작성")
                .build();
        reviewRepository.save(review);

        generateReviewImage(1, review);
        generateReviewImage(2, review);
        ReviewImage deleteImage1 = generateReviewImage(3, review);
        ReviewImage deleteImage2 = generateReviewImage(4, review);

        List<Long> deleteImageIdList = new ArrayList<>();

        deleteImageIdList.add(deleteImage1.getId());
        deleteImageIdList.add(deleteImage2.getId());


        List<Long> addImageIdList = new ArrayList<>();
        ReviewImage addImage1 = generateReviewImage(5);
        ReviewImage addImage2 = generateReviewImage(6);
        ReviewImage addImage3 = generateReviewImage(7);
        addImageIdList.add(addImage1.getId());
        addImageIdList.add(addImage2.getId());
        addImageIdList.add(addImage3.getId());

        int star = 4;
        String contents = "수정할 내용 열글자 이상";

        UpdateReviewDto requestDto = UpdateReviewDto.builder()
                .star(star)
                .contents(contents)
                .addImageIdList(addImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/reviews/{id}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status(). isForbidden());
    }

    @Test
    @DisplayName("정상적으로 커뮤니티 베스트리뷰 리스트 조회")
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
            ItemReview adminReview = generateItemReview(admin, item, i, ReviewStatus.ADMIN);
            generateBestReview(adminReview, i);
        });

       //when & then
        mockMvc.perform(get("/api/reviews/best")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryBestReviewsDtoList", hasSize(10)))
                .andDo(document("query_best_reviews",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_reviews_community").description("커뮤니티 리뷰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryBestReviewsDtoList[0].id").description("리뷰 id"),
                                fieldWithPath("_embedded.queryBestReviewsDtoList[0].imageUrl").description("리뷰 이미지 url"),
                                fieldWithPath("_embedded.queryBestReviewsDtoList[0].leakedOrder").description("베스트 리뷰 노출 순서"),
                                fieldWithPath("_embedded.queryBestReviewsDtoList[0].contents").description("리뷰 내용"),
                                fieldWithPath("_embedded.queryBestReviewsDtoList[0]._links.query_review_community.href").description("리뷰 하나 조회 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_reviews_community.href").description("커뮤니티 리뷰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 커뮤니티 리뷰 리스트 조회")
    public void queryReviewsCommunity() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        IntStream.range(1,8).forEach(i -> {
            generateSubscribeReview(member, i, ReviewStatus.APPROVAL);
            generateItemReview(admin, item, i, ReviewStatus.ADMIN);
        });
        IntStream.range(1,3).forEach(i -> {
            generateSubscribeReview(member, i, ReviewStatus.REQUEST);
            generateSubscribeReview(member, i, ReviewStatus.RETURN);
            generateItemReview(admin, item, i, ReviewStatus.REQUEST);
            generateItemReview(admin, item, i, ReviewStatus.RETURN);
        });


       //when & then
        mockMvc.perform(get("/api/reviews/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(14))
                .andDo(document("query_community_reviews",
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
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryCommunityReviewsDtoList[0].reviewDto.id").description("리뷰 id"),
                                fieldWithPath("_embedded.queryCommunityReviewsDtoList[0].reviewDto.thumbnailUrl").description("상품 썸네일 url"),
                                fieldWithPath("_embedded.queryCommunityReviewsDtoList[0].reviewDto.star").description("리뷰 평점 1~5 int"),
                                fieldWithPath("_embedded.queryCommunityReviewsDtoList[0].reviewDto.contents").description("리뷰 내용"),
                                fieldWithPath("_embedded.queryCommunityReviewsDtoList[0].reviewDto.username").description("리뷰 작성자 이름"),
                                fieldWithPath("_embedded.queryCommunityReviewsDtoList[0].reviewDto.writtenDate").description("리뷰 작성일 'yyyy-MM-dd"),
                                fieldWithPath("_embedded.queryCommunityReviewsDtoList[0].reviewImageDtoList[0].filename").description("리뷰이미지 파일이름"),
                                fieldWithPath("_embedded.queryCommunityReviewsDtoList[0].reviewImageDtoList[0].url").description("리뷰이미지 url"),
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
    @DisplayName("정상적으로 커뮤니티에서 베스트 리뷰 하나 조회")
    public void queryReviewCommunity() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        ItemReview review = generateItemReview(admin, item, 1, ReviewStatus.ADMIN);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/{id}/community", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("reviewDto.id").value(review.getId()))
                .andDo(document("query_review_community",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        pathParameters(
                                parameterWithName("id").description("리뷰 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                                ),
                        responseFields(
                                fieldWithPath("reviewDto.id").description("리뷰 id"),
                                fieldWithPath("reviewDto.writtenDate").description("리뷰 작성일"),
                                fieldWithPath("reviewDto.star").description("리뷰 평점 1~5 int"),
                                fieldWithPath("reviewDto.username").description("리뷰 작성자 이름"),
                                fieldWithPath("reviewDto.contents").description("리뷰 내용"),
                                fieldWithPath("reviewImageDtoList[0].filename").description("리뷰 이미지 파일 이름"),
                                fieldWithPath("reviewImageDtoList[0].url").description("리뷰 이미지 url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("조회할 커뮤니티 베스트 리뷰가 존재하지않음 404")
    public void queryReviewCommunity_notFound() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item = generateItem(1);
        IntStream.range(1,4).forEach(i -> {
            generateItemImage(item, 1);
        });

        ItemReview review = generateItemReview(admin, item, 1, ReviewStatus.ADMIN);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/reviews/999999/community")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }













    private BestReview generateBestReview(Review review, int i) {
        BestReview bestReview = BestReview.builder()
                .review(review)
                .leakedOrder(i)
                .build();
        return bestReviewRepository.save(bestReview);
    }


    private Review generateSubscribeReview(Member member,int i) {
        Review subscribeReview = generateSubscribeReview(member, i, ReviewStatus.RETURN);

        return subscribeReview;
    }

    private Review generateSubscribeReview(Member member, int i, ReviewStatus status) {
        List<Recipe> recipes = recipeRepository.findAll();

        Subscribe subscribe = Subscribe.builder()
                .build();
        subscribeRepository.save(subscribe);

        Dog dog = generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE,
                1, 1, SnackCountLevel.NORMAL, recipes.get(0));
        dog.setSubscribe(subscribe);

        SubscribeReview subscribeReview = SubscribeReview.builder()
                .member(member)
                .writtenDate(LocalDate.now())
                .username(member.getName())
                .star(3)
                .contents("열글자 이상의 구독 리뷰"+i)
                .status(status)
                .returnReason("상품에 맞지 않은 리뷰 내용"+i)
                .subscribe(subscribe)
                .build();
        reviewRepository.save(subscribeReview);

        IntStream.range(1,4).forEach(j -> {
            generateReviewImage(j, subscribeReview);
        });

        return subscribeReview;
    }

    private ItemReview generateItemReview(Member member, Item item,int i) {
        ItemReview itemReview = generateItemReview(member, item, i, ReviewStatus.REQUEST);

        return itemReview;
    }

    private ItemReview generateItemReview(Member member, Item item, int i, ReviewStatus status) {
        ItemReview itemReview = ItemReview.builder()
                .member(member)
                .writtenDate(LocalDate.now().minusDays(30L))
                .username(member.getName())
                .star((i + 5) % 5)
                .contents("열글자 이상의 내용"+i)
                .status(status)
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