package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.*;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.bi.barfdog.api.couponDto.UpdateAutoCouponRequest.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class CouponAdminControllerTest extends BaseTest {

    @Autowired
    EntityManager em;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberCouponRepository memberCouponRepository;

    @Autowired
    AppProperties appProperties;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemImageRepository itemImageRepository;
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    SurveyReportRepository surveyReportRepository;
    @Autowired
    DogRepository dogRepository;

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
    @DisplayName("유저권한이 관리자가 아닐 경우 403 에러")
    public void queryCoupons_forbidden() throws Exception {
        //given
        int count = 3;
        IntStream.range(1,1+count).forEach(i ->{
            generateGeneralCoupon(i);
        });

        String keyword = "1";

        //when & then
        mockMvc.perform(get("/api/admin/coupons/direct")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword",keyword))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("만료된 토큰일 경우 401 에러")
    public void queryCoupons_expired() throws Exception {
        //given
        int count = 3;
        IntStream.range(1,1+count).forEach(i ->{
            generateGeneralCoupon(i);
        });

        String keyword = "1";

        String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiLthqDtgbAg7J2066aEIiwiaWQiOjUsImV4cCI6MTY1MTg5MjU3NiwiZW1haWwiOiJhZG1pbkBnbWFpbC5jb20ifQ.Wycm9ZmiiK-GwtsUkvMCHHeExDBtkveDbhKRealjmd8C4OZMp3SFqGFcFWudXMiL5Mxdj6FcTAV9OVsOYsn_Mw";

        //when & then
        mockMvc.perform(get("/api/admin/coupons/direct")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword",keyword))
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    @DisplayName("인증이 되지 않을 경우 401 에러")
    public void queryCoupons_unauthorized() throws Exception {
        //given
        int count = 3;
        IntStream.range(1,1+count).forEach(i ->{
            generateGeneralCoupon(i);
        });

        String keyword = "1";

        //when & then
        mockMvc.perform(get("/api/admin/coupons/direct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword",keyword))
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    @DisplayName("정상적으로 관리자발행 쿠폰을 등록하는 테스트")
    public void createCoupon_admin_published() throws Exception {
        //given
        String name = "관리자 발행 5% 할인";
        DiscountType fixedRate = DiscountType.FIXED_RATE;
        int discountDegree = 5;
        String description = "관리자 발행 쿠폰";
        int amount = 3;
        int availableMaxDiscount = 5000;
        int availableMinPrice = 10000;
        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .name(name)
                .code("")
                .description(description)
                .amount(amount)
                .discountType(fixedRate)
                .discountDegree(discountDegree)
                .availableMaxDiscount(availableMaxDiscount)
                .availableMinPrice(availableMinPrice)
                .couponTarget(CouponTarget.ALL)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create_coupon",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_direct_coupons").description("직접 만든 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("쿠폰 이름"),
                                fieldWithPath("code").description("쿠폰 코드 (값 있으면->쿠폰 타입으로 발행 // 값 null이거나 빈문자열이면 -> 일반쿠폰타입으로 발행"),
                                fieldWithPath("description").description("쿠폰 설명"),
                                fieldWithPath("amount").description("쿠폰 매수"),
                                fieldWithPath("discountType").description("할인 타입 [FIXED_RATE, FLAT_RATE]"),
                                fieldWithPath("discountDegree").description("할인 정도 (원 / %)"),
                                fieldWithPath("availableMaxDiscount").description("최대 할인 금액"),
                                fieldWithPath("availableMinPrice").description("쿠폰 사용 가능한 최소 금액"),
                                fieldWithPath("couponTarget").description("쿠폰 대상 품목 [ALL, GENERAL, SUBSCRIBE]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("headers location"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_direct_coupons.href").description("직접 만든 쿠폰 리스트 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        Coupon coupon = couponRepository.findByName(name).get();

        assertThat(coupon.getName()).isEqualTo(name);
        assertThat(coupon.getCode()).isEqualTo("");
        assertThat(coupon.getCouponType()).isEqualTo(CouponType.GENERAL_PUBLISHED);
        assertThat(coupon.getDescription()).isEqualTo(description);
        assertThat(coupon.getAmount()).isEqualTo(amount);
        assertThat(coupon.getDiscountType()).isEqualTo(fixedRate);
        assertThat(coupon.getDiscountDegree()).isEqualTo(discountDegree);
        assertThat(coupon.getAvailableMaxDiscount()).isEqualTo(availableMaxDiscount);
        assertThat(coupon.getAvailableMinPrice()).isEqualTo(availableMinPrice);
        assertThat(coupon.getCouponTarget()).isEqualTo(CouponTarget.ALL);
        assertThat(coupon.getCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
    }

    @Test
    @DisplayName("정상적으로 쿠폰코드 발행 쿠폰을 등록하는 테스트")
    public void createCoupon_code_published() throws Exception {
        //given
        String name = "쿠폰 코드 발행 구독 5000원 할인";
        DiscountType flatRate = DiscountType.FLAT_RATE;
        int discountDegree = 5000;
        String code = "BARF5000";
        String description = "코드 발행 쿠폰";
        int amount = 3;
        int availableMaxDiscount = 5000;
        int availableMinPrice = 10000;
        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .name(name)
                .code(code)
                .description(description)
                .amount(amount)
                .discountType(flatRate)
                .discountDegree(discountDegree)
                .availableMaxDiscount(availableMaxDiscount)
                .availableMinPrice(availableMinPrice)
                .couponTarget(CouponTarget.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        Coupon coupon = couponRepository.findByName(name).get();

        assertThat(coupon.getName()).isEqualTo(name);
        assertThat(coupon.getCouponType()).isEqualTo(CouponType.CODE_PUBLISHED);
        assertThat(coupon.getCode()).isEqualTo(code);
        assertThat(coupon.getDescription()).isEqualTo(description);
        assertThat(coupon.getAmount()).isEqualTo(amount);
        assertThat(coupon.getDiscountType()).isEqualTo(flatRate);
        assertThat(coupon.getDiscountDegree()).isEqualTo(discountDegree);
        assertThat(coupon.getAvailableMaxDiscount()).isEqualTo(availableMaxDiscount);
        assertThat(coupon.getAvailableMinPrice()).isEqualTo(availableMinPrice);
        assertThat(coupon.getCouponTarget()).isEqualTo(CouponTarget.SUBSCRIBE);
        assertThat(coupon.getCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
    }

    @Test
    @DisplayName("쿠폰 생성 중 파라미터 값 부족할 경우 bad request 나오는 테스트")
    public void createCoupon_bad_request() throws Exception {
        //given
        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("최대사용금액보다 할인 금액이 높을 경우 bad request 나오는 테스트")
    public void createCoupon_bad_request_validator() throws Exception {
        //given
        String name = "쿠폰 코드 발행 구독 5000원 할인";
        DiscountType flatRate = DiscountType.FLAT_RATE;
        int discountDegree = 10000;
        String code = "BARF5000";
        String description = "코드 발행 쿠폰";
        int amount = 3;
        int availableMaxDiscount = 5000;
        int availableMinPrice = 10000;
        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .name(name)
                .code(code)
                .description(description)
                .amount(amount)
                .discountType(flatRate)
                .discountDegree(discountDegree)
                .availableMaxDiscount(availableMaxDiscount)
                .availableMinPrice(availableMinPrice)
                .couponTarget(CouponTarget.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("쿠폰 개수가 0 이하일 경우 bad request 나오는 테스트")
    public void createCoupon_bad_request_amount_loe_0() throws Exception {
        //given
        String name = "쿠폰 코드 발행 구독 5000원 할인";
        DiscountType flatRate = DiscountType.FLAT_RATE;
        int discountDegree = 10000;
        String code = "BARF5000";
        String description = "코드 발행 쿠폰";
        int amount = 0;
        int availableMaxDiscount = 5000;
        int availableMinPrice = 10000;
        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .name(name)
                .code(code)
                .description(description)
                .amount(amount)
                .discountType(flatRate)
                .discountDegree(discountDegree)
                .availableMaxDiscount(availableMaxDiscount)
                .availableMinPrice(availableMinPrice)
                .couponTarget(CouponTarget.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("할인정도가 0 이하일 경우 bad request 나오는 테스트")
    public void createCoupon_bad_request_discountDegree_loe_0() throws Exception {
        //given
        String name = "쿠폰 코드 발행 구독 5000원 할인";
        DiscountType flatRate = DiscountType.FLAT_RATE;
        int discountDegree = 0;
        String code = "BARF5000";
        String description = "코드 발행 쿠폰";
        int amount = 5;
        int availableMaxDiscount = 5000;
        int availableMinPrice = 10000;
        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .name(name)
                .code(code)
                .description(description)
                .amount(amount)
                .discountType(flatRate)
                .discountDegree(discountDegree)
                .availableMaxDiscount(availableMaxDiscount)
                .availableMinPrice(availableMinPrice)
                .couponTarget(CouponTarget.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("할인률이 100 이상일 경우 bad request 나오는 테스트")
    public void createCoupon_bad_request_discount_percent_goe_0() throws Exception {
        //given
        String name = "쿠폰 코드 발행 구독 5000원 할인";
        DiscountType flatRate = DiscountType.FIXED_RATE;
        int discountDegree = 100;
        String code = "BARF5000";
        String description = "코드 발행 쿠폰";
        int amount = 1;
        int availableMaxDiscount = 5000;
        int availableMinPrice = 10000;
        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .name(name)
                .code(code)
                .description(description)
                .amount(amount)
                .discountType(flatRate)
                .discountDegree(discountDegree)
                .availableMaxDiscount(availableMaxDiscount)
                .availableMinPrice(availableMinPrice)
                .couponTarget(CouponTarget.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("이미 존재하는 쿠폰코드일 경우 conflict 나오는 테스트")
    public void createCoupon_code_conflict() throws Exception {
        //given
        String name = "쿠폰 코드 발행 구독 5000원 할인";
        DiscountType flatRate = DiscountType.FLAT_RATE;
        int discountDegree = 5000;
        String code = "BARF5000";
        String description = "코드 발행 쿠폰";
        int amount = 3;
        int availableMaxDiscount = 5000;
        int availableMinPrice = 10000;

        Coupon sampleCoupon = Coupon.builder()
                .code(code)
                .build();
        couponRepository.save(sampleCoupon);

        em.flush();
        em.clear();

        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .name(name)
                .code(code)
                .description(description)
                .amount(amount)
                .discountType(flatRate)
                .discountDegree(discountDegree)
                .availableMaxDiscount(availableMaxDiscount)
                .availableMinPrice(availableMinPrice)
                .couponTarget(CouponTarget.SUBSCRIBE)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict())
        ;
    }

    @Test
    @DisplayName("정상적으로 직접 발행 쿠폰 리스트 조회하는 테스트")
    public void queryCoupons_direct() throws Exception {
        //given
        int count = 15;
        IntStream.range(1,1+count).forEach(i ->{
            generateGeneralCoupon(i);
        });

        String keyword = "관리자";

        //when & then
        mockMvc.perform(get("/api/admin/coupons/direct")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword",keyword)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_direct_coupons",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("query_auto_coupons").description("자동 발행 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("keyword").description("제목 검색 키워드"),
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.couponListResponseDtoList[0].id").description("쿠폰 id"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].couponType").description("쿠폰 타입 [AUTO_PUBLISHED, GENERAL_PUBLISHED, CODE_PUBLISHED], 각 자동발행/일반발행/쿠폰발행"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].code").description("쿠폰 코드"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].description").description("쿠폰 설명"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].discount").description("쿠폰 할인금액"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].couponTarget").description("사용처"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].amount").description("쿠폰 사용 한도 회수"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].expiredDate").description("발급 이력 중 가장 긴 유효기간 날짜"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0]._links.inactive_coupon.href").description("쿠폰 삭제(비활성화) 링크 [유효기간이 지난 쿠폰일 경우에만 링크가 나타남]"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.query_auto_coupons.href").description("자동 발행 쿠폰 리스트 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("키워드 null 일 경우 bad request 나오는 테스트")
    public void queryCoupons_direct_keyword_null() throws Exception {
        //given
        int count = 3;
        IntStream.range(1,1+count).forEach(i ->{
            generateGeneralCoupon(i);
        });

        String keyword = null;

        //when & then
        mockMvc.perform(get("/api/admin/coupons/direct")
                                .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
//                        .param("keyword",keyword)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("키워드가 빈 문자열 일 경우 모든 직접발행 쿠폰 조회하는 테스트")
    public void queryCoupons_direct_keyword_emptyString() throws Exception {
        //given
        int count = 3;
        IntStream.range(1,1+count).forEach(i ->{
            generateGeneralCoupon(i);
        });

        String keyword = "";

        //when & then
        mockMvc.perform(get("/api/admin/coupons/direct")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword",keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.couponListResponseDtoList", hasSize(count)))
        ;
    }

    @Test
    @DisplayName("정상적으로 키워드로 자동발행 쿠폰 검색하는 테스트")
    public void queryCoupons_auto() throws Exception {
        //given
        couponRepository.deleteAll();

        int count = 15;
        IntStream.range(1,1+count).forEach(i ->{
            generateAutoCoupon(i);
        });

        String keyword = "생일";

        //when & then
        mockMvc.perform(get("/api/admin/coupons/auto")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword", keyword)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_auto_coupons",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("query_direct_coupons").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("keyword").description("제목 검색 키워드"),
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.couponListResponseDtoList[0].id").description("쿠폰 id"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].couponType").description("쿠폰 타입 [AUTO_PUBLISHED, GENERAL_PUBLISHED, CODE_PUBLISHED], 각 자동발행/일반발행/쿠폰발행"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].code").description("쿠폰 코드"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].description").description("쿠폰 설명"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].discount").description("쿠폰 할인금액"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].couponTarget").description("사용처"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].amount").description("쿠폰 사용 한도 회수"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].expiredDate").description("발급 이력 중 가장 긴 유효기간 날짜"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.query_direct_coupons.href").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 키워드가 빈 문자열인 자동발행 쿠폰을 조회하는 테스트")
    public void queryCoupons_auto_keyword_emptyString() throws Exception {
        //given

        IntStream.range(1,9).forEach(i -> {
            generateAutoCoupon(i);
        });

        String keyword = "";

        //when & then
        mockMvc.perform(get("/api/admin/coupons/auto")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword",keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.couponListResponseDtoList", hasSize(8)))
        ;
    }

    @Test
    @DisplayName("정상적으로 쿠폰을 비활성화(삭제) 시키는 테스트")
    public void inactive_coupon() throws Exception {
        //given

        Coupon findCoupon = generateGeneralCoupon(1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/coupons/{id}/inactive", findCoupon.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_coupon_inactive",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_direct_coupons").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("쿠폰 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_direct_coupons.href").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Coupon coupon = couponRepository.findById(findCoupon.getId()).get();

        assertThat(coupon.getCouponStatus()).isEqualTo(CouponStatus.INACTIVE);

    }

    @Test
    @DisplayName("비활성화 할 쿠폰이 존재하지 않을 경우 not found 나오는 테스트")
    public void inactive_coupon_not_found() throws Exception {
        //given

        //when & then
        mockMvc.perform(put("/api/admin/coupons/999999/inactive")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("비활성화 대상이 자동발행 쿠폰이면 bad request")
    public void inactive_coupon_autoCoupon_bad_request() throws Exception {
        //given

        Coupon coupon = generateAutoCoupon(1);

        //when & then
        mockMvc.perform(put("/api/admin/coupons/{id}/inactive", coupon.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("발행시 필요한 일반형 쿠폰 리스트 조회하는 테스트")
    public void queryCoupons_Admin() throws Exception {
        //given

        IntStream.range(1, 4).forEach(i ->{
            generateGeneralCoupon(i);
        });

        IntStream.range(1, 5).forEach(i ->{
            generateCodeCoupon(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/coupons/publication/general")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.publicationCouponDtoList",hasSize(3)))
                .andDo(document("query_general_coupons_in_publication",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.publicationCouponDtoList[0].couponId").description("쿠폰 id"),
                                fieldWithPath("_embedded.publicationCouponDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("_embedded.publicationCouponDtoList[0].discount").description("할인 ( % / 원)"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("발행시 필요한 코드형 쿠폰 리스트 조회하는 테스트")
    public void queryCoupons_code() throws Exception {
        //given
        IntStream.range(1, 4).forEach(i ->{
            generateGeneralCoupon(i);
        });

        IntStream.range(1, 5).forEach(i ->{
            generateCodeCoupon(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/coupons/publication/code")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.publicationCouponDtoList",hasSize(4)))
                .andDo(document("query_code_coupons_in_publication",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.publicationCouponDtoList[0].couponId").description("쿠폰 id"),
                                fieldWithPath("_embedded.publicationCouponDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("_embedded.publicationCouponDtoList[0].discount").description("할인 ( % / 원)"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 일반쿠폰 개인 발행하는 테스트")
    public void publishCoupon_personal() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

//        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
//        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Member member = Member.builder()
                .email("jyh@gmail.com")
                .name("조영한")
                .phoneNumber("01099038522")
                .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                .birthday("19991201")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .grade(Grade.브론즈)
                .reward(0)
                .accumulatedAmount(0)
                .firstReward(new FirstReward(false, false))
                .roles("USER")
                .build();
        memberRepository.save(member);

        List<Long> memberIdList = new ArrayList<>();
//        memberIdList.add(admin.getId());
//        memberIdList.add(user.getId());
        memberIdList.add(member.getId());

        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .expiredDate("2025-05-31")
                .couponType(CouponType.GENERAL_PUBLISHED)
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("publish_coupon_personal",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_direct_coupons").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("memberIdList").description("회원 id(인덱스) 리스트 ex [ 3, 24, 32 ,...]"),
                                fieldWithPath("expiredDate").description("발행할 쿠폰의 만료 기간 설정 'yyyy-MM-dd' String 형식"),
                                fieldWithPath("couponType").description("쿠폰 타입(일반,코드) [GENERAL_PUBLISHED, CODE_PUBLISHED]"),
                                fieldWithPath("couponId").description("쿠폰 id(인덱스)"),
                                fieldWithPath("alimTalk").description("알림톡 전송 여부 [true/false]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.LOCATION).description("Location 리다이렉트 링크")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_direct_coupons.href").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        List<MemberCoupon> all = memberCouponRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("정상적으로 코드 쿠폰 개인 발행하는 테스트")
    public void publishCoupon_personal_Code_Alim() throws Exception {
        //given
        Coupon coupon = generateCodeCoupon(1);

//        Member member = Member.builder()
//                .email("jyh@gmail.com")
//                .name("조영한")
//                .phoneNumber("01099038522")
//                .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
//                .birthday("19991201")
//                .gender(Gender.MALE)
//                .agreement(new Agreement(true,true,true,true,true))
//                .myRecommendationCode(BarfUtils.generateRandomCode())
//                .grade(Grade.브론즈)
//                .reward(0)
//                .accumulatedAmount(0)
//                .firstReward(new FirstReward(false, false))
//                .roles("USER")
//                .build();
//        memberRepository.save(member);

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");

        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(member.getId());

        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .expiredDate("2025-05-31")
                .couponType(CouponType.CODE_PUBLISHED)
                .couponId(coupon.getId())
                .alimTalk(true)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        List<MemberCoupon> all = memberCouponRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

        em.clear();
        em.flush();

        Coupon findCoupon = couponRepository.findById(coupon.getId()).get();

        assertThat(findCoupon.getLastExpiredDate()).isEqualTo(LocalDateTime.of(2025, 5, 31, 23, 59, 59));

    }

    private Dog generateDog(Member member, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel, boolean representative, String name) {
        Dog dog = Dog.builder()
                .member(member)
                .representative(representative)
                .name(name)
                .startAgeMonth(startAgeMonth)
                .gender(Gender.MALE)
                .oldDog(false)
                .dogSize(dogSize)
                .weight(new BigDecimal(weight))
                .dogActivity(new DogActivity(activitylevel, walkingCountPerWeek, walkingTimePerOneTime))
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(snackCountLevel)
                .build();
        return dogRepository.save(dog);
    }



    @Test
    @DisplayName("정상적으로 100명에게 코드 쿠폰 개인 발행하는 테스트")
    public void publishCoupon_personal_Code_Alim_100() throws Exception {
        //given
        Coupon coupon = generateCodeCoupon(1);

        List<Long> memberIdList = new ArrayList<>();

        IntStream.range(1, 101).forEach(i ->{
            Member member = generateMember(i, "0109903" + i);
            memberIdList.add(member.getId());
        });


        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .expiredDate("2025-05-31")
                .couponType(CouponType.CODE_PUBLISHED)
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;
        em.clear();
        em.flush();

        List<MemberCoupon> all = memberCouponRepository.findAll();
        assertThat(all.size()).isEqualTo(100);


        Coupon findCoupon = couponRepository.findById(coupon.getId()).get();

        assertThat(findCoupon.getLastExpiredDate()).isEqualTo(LocalDateTime.of(2025, 5, 31, 23, 59, 59));

    }

    private Member generateMember(String email, String name, String password, String phoneNumber, Gender gender, Grade grade, int reward, boolean recommend, String roles, boolean isSubscribe) {
        Member member = Member.builder()
                .email(email)
                .name(name)
                .password(bCryptPasswordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                .birthday("19991201")
                .gender(gender)
                .agreement(new Agreement(true,true,true,true,true))
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .grade(grade)
                .reward(reward)
                .accumulatedAmount(1000000)
                .accumulatedSubscribe(3)
                .isSubscribe(isSubscribe)
                .firstReward(new FirstReward(recommend, recommend))
                .roles(roles)
                .build();

        return memberRepository.save(member);
    }

    private Member generateMember(int i, String phoneNumber) {
        Member member = Member.builder()
                .email("jyh@gmail.com" + i)
                .name("회원" + i)
                .phoneNumber(phoneNumber)
                .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                .birthday("19991201")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .grade(Grade.브론즈)
                .reward(0)
                .accumulatedAmount(0)
                .firstReward(new FirstReward(false, false))
                .roles("USER")
                .build();
        memberRepository.save(member);
        return member;
    }

    @Test
    @DisplayName("발행하는 쿠폰이 비활성화쿠폰일 경우 badrequest")
    public void publishCoupon_personal_inactive_coupon() throws Exception {
        //given
        Coupon coupon = generateCodeCoupon(1);
        coupon.inactive();

        Member member = Member.builder()
                .email("jyh@gmail.com")
                .name("조영한")
                .phoneNumber("01099038522")
                .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                .birthday("19991201")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .grade(Grade.브론즈)
                .reward(0)
                .accumulatedAmount(0)
                .firstReward(new FirstReward(false, false))
                .roles("USER")
                .build();
        memberRepository.save(member);

        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(member.getId());

        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .expiredDate("2025-05-31")
                .couponType(CouponType.CODE_PUBLISHED)
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("쿠폰 개인 발행시 존재하지 않는 쿠폰일 경우 not found 나오는 테스트")
    public void publishCoupon_personal_coupon_notFound() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(admin.getId());
        memberIdList.add(user.getId());

        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .expiredDate("2025-05-31")
                .couponType(CouponType.CODE_PUBLISHED)
                .couponId(999999L)
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("쿠폰 개인 발행시 쿠폰과 쿠폰타입이 일치하지 않을 경우 bad request 나오는 테스트")
    public void publishCoupon_personal_wrongCouponType() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(admin.getId());
        memberIdList.add(user.getId());

        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .expiredDate("2025-05-31")
                .couponType(CouponType.CODE_PUBLISHED)
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("정상적으로 해당 그룹에게 코드 발행 쿠폰을 발행하는 테스트")
    public void publish_coupons_code_group() throws Exception {
        //given
        Coupon coupon = generateCodeCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Member member2 = generateMember("ntm723@gmail.com", "나회원", appProperties.getUserPassword(), "01056862723", Gender.MALE, Grade.브론즈, 50000, false, "USER,SUBSCRIBER", true);

        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");
        generateDog(member2, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "댕댕이");

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);
        gradeList.add(Grade.실버);

        GroupPublishRequestDto requestDto = GroupPublishRequestDto.builder()
                .subscribe(true)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1990")
                .birthYearTo("1999")
                .expiredDate("2025-05-31")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(true)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("publish_coupon_group",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_direct_coupons").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("subscribe").description("구독 여부 [true/false]"),
                                fieldWithPath("longUnconnected").description("장기(1년) 미접속 여부 [true/false]"),
                                fieldWithPath("gradeList").description("등급 리스트 String 배열 형식 [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]"),
                                fieldWithPath("area").description("지역 선택 [ALL, METRO, NON_METRO]"),
                                fieldWithPath("birthYearFrom").description("시작하는 출생년도 'yyyy' String 타입"),
                                fieldWithPath("birthYearTo").description("끝나는 출생년도 'yyyy' String 타입"),
                                fieldWithPath("expiredDate").description("발행할 쿠폰의 만료 기간 설정 'yyyy-MM-dd' String 타입"),
                                fieldWithPath("couponType").description("쿠폰 타입(일반,코드) [GENERAL_PUBLISHED, CODE_PUBLISHED]"),
                                fieldWithPath("couponId").description("쿠폰 id(인덱스)"),
                                fieldWithPath("alimTalk").description("알림톡 전송 여부 [true/false]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.LOCATION).description("Location 리다이렉트 링크")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_direct_coupons.href").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        List<Member> findMembers = memberRepository.findByGrades(gradeList);

        List<MemberCoupon> all = memberCouponRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("정상적으로 해당 그룹에게 일반 발행 쿠폰을 발행하는 테스트_ 실버에게만")
    public void publish_coupons_group_실버() throws Exception {
        //given
        Coupon coupon = generateCodeCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.실버);

        GroupPublishRequestDto requestDto = GroupPublishRequestDto.builder()
                .subscribe(true)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1990")
                .birthYearTo("1999")
                .expiredDate("2025-05-31")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(true)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        List<Member> findMembers = memberRepository.findByGrades(gradeList);

        List<MemberCoupon> all = memberCouponRepository.findAll();
        assertThat(all.size()).isEqualTo(0);

    }


    @Test
    @DisplayName("비활성화인 쿠폰을 발행할 경우 bad request")
    public void publish_coupons_group_inactive_coupon() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);
        coupon.inactive();

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);
        gradeList.add(Grade.실버);

        GroupPublishRequestDto requestDto = GroupPublishRequestDto.builder()
                .subscribe(false)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1990")
                .birthYearTo("1999")
                .expiredDate("2025-05-31")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("선택한 등급이 없을 경우 bad request")
    public void publish_coupons_group_grade_0() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Grade> gradeList = new ArrayList<>();

        GroupPublishRequestDto requestDto = GroupPublishRequestDto.builder()
                .subscribe(false)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1990")
                .birthYearTo("1999")
                .expiredDate("2025-05-31")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("년생 입력정보 순서가 잘못 되었을 경우 bad request")
    public void publish_coupons_group_birth_wrong() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);

        GroupPublishRequestDto requestDto = GroupPublishRequestDto.builder()
                .subscribe(false)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1998")
                .birthYearTo("1997")
                .expiredDate("2025-05-31")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("유효기간이 이미 지난 날짜일 경우 bad request")
    public void publish_coupons_group_expiredDate_wrong() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);

        GroupPublishRequestDto requestDto = GroupPublishRequestDto.builder()
                .subscribe(false)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1990")
                .birthYearTo("1997")
                .expiredDate("2022-04-28")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("발급할 쿠폰이 없을 경우 not found")
    public void publish_coupons_group_empty_coupon_notfound() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);

        GroupPublishRequestDto requestDto = GroupPublishRequestDto.builder()
                .subscribe(false)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1990")
                .birthYearTo("1997")
                .expiredDate("2025-04-28")
                .couponType(coupon.getCouponType())
                .couponId(9999L)
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("전체 유저에게 일반 쿠폰 보내는 테스트")
    public void publish_coupons_all_general_coupon() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        AllPublishRequestDto requestDto = AllPublishRequestDto.builder()
                .expiredDate("2025-05-28")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("publish_coupon_all",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_direct_coupons").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("expiredDate").description("발행할 쿠폰의 만료 기간 설정 'yyyy-MM-dd' String 타입"),
                                fieldWithPath("couponType").description("쿠폰 타입(일반,코드) [GENERAL_PUBLISHED, CODE_PUBLISHED]"),
                                fieldWithPath("couponId").description("쿠폰 id(인덱스)"),
                                fieldWithPath("alimTalk").description("알림톡 전송 여부 [true/false]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.LOCATION).description("Location 리다이렉트 링크")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_direct_coupons.href").description("직접 발행 쿠폰 리스트 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("전체 유저에게 코드형 쿠폰 보내는 테스트")
    public void publish_coupons_all_code_coupon() throws Exception {
        //given
        memberRepository.deleteAll();

        em.flush();
        em.clear();

        Coupon coupon = generateCodeCoupon(1);

        Member admin = generateMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01056862723", Gender.FEMALE, Grade.더바프, 100000, true, "ADMIN,SUBSCRIBER,USER", true);
        Member member = generateMember("jyh@gmail.com", "김회원", appProperties.getUserPassword(), "01099038544", Gender.MALE, Grade.브론즈, 50000, false, "USER,SUBSCRIBER", true);

        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");
        generateDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "댕댕이");

        AllPublishRequestDto requestDto = AllPublishRequestDto.builder()
                .expiredDate("2025-05-28")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        em.flush();
        em.clear();

        List<Member> allMembers = memberRepository.findAll();

        List<MemberCoupon> all = memberCouponRepository.findAll();
        assertThat(all.size()).isEqualTo(allMembers.size());

    }

    @Test
    @DisplayName("전체유저에게 보낼 때 요청값이 부족할 경우 badrequest")
    public void publish_coupons_all_bad_request() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        AllPublishRequestDto requestDto = AllPublishRequestDto.builder()
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("전체유저에게 보낼 때 존재하지 않는 쿠폰일 경우 not found")
    public void publish_coupons_all_coupon_notFound() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        AllPublishRequestDto requestDto = AllPublishRequestDto.builder()
                .expiredDate("2021-05-28")
                .couponType(coupon.getCouponType())
                .couponId(9999L)
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("전체 유저에게 쿠폰 발행시 선택한 쿠폰타입과 쿠폰의 타입이 다를 경우 bad request")
    public void publish_coupons_all_wrong_couponType() throws Exception {
        //given
        Coupon coupon = generateCodeCoupon(1);

        AllPublishRequestDto requestDto = AllPublishRequestDto.builder()
                .expiredDate("2025-05-28")
                .couponType(CouponType.GENERAL_PUBLISHED)
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("전체유저에게 보낼 때 유효기간이 이미 지나갔을 경우 badrequest")
    public void publish_coupons_all_expiredDate_wrong() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);

        AllPublishRequestDto requestDto = AllPublishRequestDto.builder()
                .expiredDate("2021-05-28")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("전체유저에게 보낼 때 쿠폰이 비활성화 쿠폰일 경우 badrequest")
    public void publish_coupons_all_inactive_coupon() throws Exception {
        //given
        Coupon coupon = generateGeneralCoupon(1);
        coupon.inactive();

        AllPublishRequestDto requestDto = AllPublishRequestDto.builder()
                .expiredDate("2025-05-28")
                .couponType(coupon.getCouponType())
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/coupons/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("쿠폰 수정 페이지 리스트 조회 테스트")
    public void queryAutoCouponsForUpdate() throws Exception {
        //given

        IntStream.range(1,9).forEach(i -> {
            generateAutoCoupon(i);
        });

        //when & then
        mockMvc.perform(get("/api/admin/coupons/auto/modification")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_auto_coupons_modification",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_auto_coupons").description("자동발행 쿠폰 수정하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.autoCouponsForUpdateDtoList[0].id").description("쿠폰 id"),
                                fieldWithPath("_embedded.autoCouponsForUpdateDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("_embedded.autoCouponsForUpdateDtoList[0].discountType").description("할인 유형 [FIXED_RATE, FLAT_RATE]"),
                                fieldWithPath("_embedded.autoCouponsForUpdateDtoList[0].discountDegree").description("할인 정도"),
                                fieldWithPath("_embedded.autoCouponsForUpdateDtoList[0].availableMinPrice").description("쿠폰 적용 가능한 최소 결제금액"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_auto_coupons.href").description("자동발행 쿠폰 수정하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        List<AutoCouponsForUpdateDto> dtoList = couponRepository.findAutoCouponDtosForUpdate();
        assertThat(dtoList.size()).isEqualTo(8);

    }

    @Test
    @DisplayName("정상적으로 자동 쿠폰 수정하는 테스트")
    public void updateAutoCoupons() throws Exception {
        //given

        IntStream.range(1,5).forEach(i -> {
            generateAutoCoupon(i);
        });

        List<AutoCouponsForUpdateDto> autoCouponDtosForUpdate = couponRepository.findAutoCouponDtosForUpdate();

        List<UpdateAutoCouponRequest.UpdateAutoCouponRequestDto> dtoList = new ArrayList<>();

        for (AutoCouponsForUpdateDto coupon : autoCouponDtosForUpdate) {
            UpdateAutoCouponRequest.UpdateAutoCouponRequestDto build = UpdateAutoCouponRequest.UpdateAutoCouponRequestDto.builder()
                    .id(coupon.getId())
                    .discountDegree(
                            coupon.getDiscountType() == DiscountType.FIXED_RATE ?
                                    coupon.getDiscountDegree() + 10 : coupon.getDiscountDegree() + 3000)
                    .availableMinPrice(coupon.getAvailableMinPrice() + 1000)
                    .build();
            dtoList.add(build);
        }

        UpdateAutoCouponRequest requestDto = builder()
                .updateAutoCouponRequestDtoList(dtoList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/coupons/auto/modification")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_auto_coupons",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_auto_coupons_modification").description("자동발행 쿠폰 수정하기 위한 기본값 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("updateAutoCouponRequestDtoList[0].id").description("쿠폰 id"),
                                fieldWithPath("updateAutoCouponRequestDtoList[0].discountDegree").description("쿠폰 할인 정도"),
                                fieldWithPath("updateAutoCouponRequestDtoList[0].availableMinPrice").description("쿠폰 적용 가능한 최소 결제금액")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_auto_coupons_modification.href").description("자동발행 쿠폰 수정하기 위한 기본값 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Coupon findCoupon = couponRepository.findAll().get(0);
        assertThat(findCoupon.getDiscountDegree()).isEqualTo(20);
        assertThat(findCoupon.getAvailableMinPrice()).isEqualTo(51000);

    }

    @Test
    @DisplayName("자동쿠폰 수정 요청 시 파라미터 값이 부족하면 bad request 나오는 테스트")
    public void updateAutoCoupons_badRequest() throws Exception {
        //given
        List<AutoCouponsForUpdateDto> autoCouponDtosForUpdate = couponRepository.findAutoCouponDtosForUpdate();

        List<UpdateAutoCouponRequest.UpdateAutoCouponRequestDto> dtoList = new ArrayList<>();

        for (AutoCouponsForUpdateDto coupon : autoCouponDtosForUpdate) {
            UpdateAutoCouponRequest.UpdateAutoCouponRequestDto build = UpdateAutoCouponRequest.UpdateAutoCouponRequestDto.builder()
                    .id(coupon.getId())
                    .discountDegree(
                            coupon.getDiscountType() == DiscountType.FIXED_RATE ?
                                    coupon.getDiscountDegree() + 10 : coupon.getDiscountDegree() + 3000)
                    .availableMinPrice(-1)
                    .build();
            dtoList.add(build);
        }

        UpdateAutoCouponRequest requestDto = builder()
//                .updateAutoCouponRequestDtoList(dtoList)
                .build();

        //when & then
        mockMvc.perform(put("/api/admin/coupons/auto/modification")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }


























    private Coupon generateAutoCoupon(int i) {
        Coupon coupon = Coupon.builder()
                .name("생일 쿠폰" + i)
                .couponType(CouponType.AUTO_PUBLISHED)
                .code("")
                .description("설명")
                .amount(1)
                .discountType(DiscountType.FIXED_RATE)
                .discountDegree(10)
                .availableMaxDiscount(10000)
                .availableMinPrice(50000)
                .couponTarget(CouponTarget.ALL)
                .couponStatus(CouponStatus.ACTIVE)
                .build();

        return couponRepository.save(coupon);
    }





    private Coupon generateGeneralCoupon(int i) {
        Coupon coupon = Coupon.builder()
                .name("관리자 직접 발행 쿠폰" + i)
                .couponType(CouponType.GENERAL_PUBLISHED)
                .code("")
                .description("설명")
                .amount(1)
                .discountType(DiscountType.FIXED_RATE)
                .discountDegree(10)
                .availableMaxDiscount(10000)
                .availableMinPrice(5000)
                .lastExpiredDate(LocalDateTime.now().minusDays(1))
                .couponTarget(CouponTarget.ALL)
                .couponStatus(CouponStatus.ACTIVE)
                .build();

        return couponRepository.save(coupon);
    }

    private Coupon generateCodeCoupon(int i) {
        Coupon coupon = Coupon.builder()
                .name("코드 발행 쿠폰" + i)
                .couponType(CouponType.CODE_PUBLISHED)
                .code("Barf100"+i)
                .description("설명")
                .amount(1)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(5000)
                .availableMaxDiscount(10000)
                .availableMinPrice(5000)
                .couponTarget(CouponTarget.ALL)
                .couponStatus(CouponStatus.ACTIVE)
                .build();

        return couponRepository.save(coupon);
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