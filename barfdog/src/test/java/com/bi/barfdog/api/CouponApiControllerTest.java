package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.CouponSaveRequestDto;
import com.bi.barfdog.api.couponDto.PersonalPublishRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.CouponRepository;
import com.bi.barfdog.repository.MemberRepository;
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
import java.util.ArrayList;
import java.util.List;
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
public class CouponApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    MemberRepository memberRepository;

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
        assertThat(coupon.getCouponStatus()).isEqualTo(CouponStatus.ACTIVE);
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
    @DisplayName("정상적으로 직접 발행 쿠폰 리스트 조회하는 테스트")
    public void queryCoupons_direct() throws Exception {
       //given
        int count = 3;
        IntStream.range(1,1+count).forEach(i ->{
            generateAdminCoupon(i);
        });

        String keyword = "1";

        //when & then
        mockMvc.perform(get("/api/coupons/direct")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword",keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_direct_coupons",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_auto_coupons").description("자동 발행 쿠폰 리스트 조회하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("keyword").description("제목 검색 키워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.couponListResponseDtoList[0].id").description("쿠폰 id"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].code").description("쿠폰 코드"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].description").description("쿠폰 설명"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].discount").description("쿠폰 할인금액"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].couponTarget").description("사용처"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].amount").description("쿠폰 사용 한도 회수"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].expiredDate").description("발급 이력 중 가장 긴 유효기간 날짜"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0]._links.inactive_coupon.href").description("쿠폰 삭제(비활성화) 링크 [유효기간이 지난 쿠폰일 경우에만 링크가 나타남]"),
                                fieldWithPath("_links.self.href").description("self 링크"),
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
            generateAdminCoupon(i);
        });

        String keyword = null;

        //when & then
        mockMvc.perform(get("/api/coupons/direct")
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
            generateAdminCoupon(i);
        });

        String keyword = "";

        //when & then
        mockMvc.perform(get("/api/coupons/direct")
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

        String keyword = "생일";

        //when & then
        mockMvc.perform(get("/api/coupons/auto")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.couponListResponseDtoList", hasSize(2)))
                .andDo(document("query_auto_coupons",
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
                        requestParameters(
                                parameterWithName("keyword").description("제목 검색 키워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.couponListResponseDtoList[0].id").description("쿠폰 id"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].code").description("쿠폰 코드"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].description").description("쿠폰 설명"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].discount").description("쿠폰 할인금액"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].couponTarget").description("사용처"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].amount").description("쿠폰 사용 한도 회수"),
                                fieldWithPath("_embedded.couponListResponseDtoList[0].expiredDate").description("발급 이력 중 가장 긴 유효기간 날짜"),
                                fieldWithPath("_links.self.href").description("self 링크"),
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

        String keyword = "";

       //when & then
        mockMvc.perform(get("/api/coupons/auto")
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

        Coupon findCoupon = generateAdminCoupon(1);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/coupons/{id}/inactive", findCoupon.getId())
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
        mockMvc.perform(put("/api/coupons/999999/inactive")
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

        Coupon findCoupon = couponRepository.findByName("실버 쿠폰").get();

        //when & then
        mockMvc.perform(put("/api/coupons/{id}/inactive", findCoupon.getId())
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
            generateAdminCoupon(i);
        });

        IntStream.range(1, 5).forEach(i ->{
            generateCodeCoupon(i);
        });

       //when & then
        mockMvc.perform(get("/api/coupons/publication/general")
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
            generateAdminCoupon(i);
        });

        IntStream.range(1, 5).forEach(i ->{
            generateCodeCoupon(i);
        });

        //when & then
        mockMvc.perform(get("/api/coupons/publication/code")
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
    @DisplayName("정상적으로 쿠폰 개인 발행하는 테스트")
    public void publishCoupon_personal() throws Exception {
       //given
        Coupon coupon = generateAdminCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(admin.getId());
        memberIdList.add(user.getId());

        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .couponLife(31)
                .couponType(CouponType.GENERAL_PUBLISHED)
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/coupons/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;
    }

    @Test
    @DisplayName("쿠폰 개인 발행시 존재하지 않는 쿠폰일 경우 not found 나오는 테스트")
    public void publishCoupon_personal_coupon_notFound() throws Exception {
        //given
        Coupon coupon = generateAdminCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(admin.getId());
        memberIdList.add(user.getId());

        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .couponLife(31)
                .couponType(CouponType.CODE_PUBLISHED)
                .couponId(999999L)
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/coupons/personal")
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
        Coupon coupon = generateAdminCoupon(1);

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member user = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(admin.getId());
        memberIdList.add(user.getId());

        PersonalPublishRequestDto requestDto = PersonalPublishRequestDto.builder()
                .memberIdList(memberIdList)
                .couponLife(31)
                .couponType(CouponType.CODE_PUBLISHED)
                .couponId(coupon.getId())
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/coupons/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }


























    private Coupon generateAdminCoupon(int i) {
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