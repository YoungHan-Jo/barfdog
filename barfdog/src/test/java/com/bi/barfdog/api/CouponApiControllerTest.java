package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.CouponRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class CouponApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    AppProperties appProperties;

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
        mockMvc.perform(post("/api/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create_coupon",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-direct-coupons").description("직접 만든 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("쿠폰 이름"),
                                fieldWithPath("code").description("쿠폰 코드 (값 있으면 -> 쿠폰발행/ 값 null이거나 빈문자열이면 -> 관리자발행"),
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
                                fieldWithPath("_links.query-direct-coupons.href").description("직접 만든 쿠폰 리스트 조회하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        Coupon coupon = couponRepository.findByName(name).get();

        assertThat(coupon.getName()).isEqualTo(name);
        assertThat(coupon.getCode()).isEqualTo("");
        assertThat(coupon.getCouponType()).isEqualTo(CouponType.ADMIN_PUBLISHED);
        assertThat(coupon.getDescription()).isEqualTo(description);
        assertThat(coupon.getAmount()).isEqualTo(amount);
        assertThat(coupon.getDiscountType()).isEqualTo(fixedRate);
        assertThat(coupon.getDiscountDegree()).isEqualTo(discountDegree);
        assertThat(coupon.getAvailableMaxDiscount()).isEqualTo(availableMaxDiscount);
        assertThat(coupon.getAvailableMinPrice()).isEqualTo(availableMinPrice);
        assertThat(coupon.getCouponTarget()).isEqualTo(CouponTarget.ALL);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
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
        mockMvc.perform(post("/api/coupons")
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
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
    }

    @Test
    @DisplayName("쿠폰 생성 중 파라미터 값 부족할 경우 bad request 나오는 테스트")
    public void createCoupon_bad_request() throws Exception {
        //given
        CouponSaveRequestDto requestDto = CouponSaveRequestDto.builder()
                .build();

        //when & then
        mockMvc.perform(post("/api/coupons")
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
        mockMvc.perform(post("/api/coupons")
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
        mockMvc.perform(post("/api/coupons")
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
        mockMvc.perform(post("/api/coupons")
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
        mockMvc.perform(post("/api/coupons")
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
        mockMvc.perform(post("/api/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict())
        ;
    }
    
    @Test
    @DisplayName("직접 발행 쿠폰 리스트 조회하는 테스트")
    public void queryCoupons_direct() throws Exception {
       //given
       
       //when & then
        mockMvc.perform(get("/api/coupons/direct")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
      
    }
    











    private String getAdminToken() throws Exception {
        return getBearerToken(appProperties.getAdminEmail(), appProperties.getAdminPassword());
    }

    private String getUserToken() throws Exception {
        return getBearerToken(appProperties.getUserEmail(), appProperties.getUserPassword());
    }

    private String getBearerToken(String appProperties, String appProperties1) throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .username(appProperties)
                .password(appProperties1)
                .build();

        //when & then
        ResultActions perform = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        MockHttpServletResponse response = perform.andReturn().getResponse();
        return response.getHeaders("Authorization").get(0);
    }


}