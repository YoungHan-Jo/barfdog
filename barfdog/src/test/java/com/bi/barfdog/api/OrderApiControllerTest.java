package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.jwt.JwtLoginDto;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class OrderApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Test
    @DisplayName("구독 주문서 조회하기")
    public void getOrderSheetDto_Subscribe() throws Exception {
       //given

       //when & then
        mockMvc.perform(get("/api/orders/sheet/subscribe")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_orderSheet_subscribe",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("order_subscribe").description("구독 주문하는 링크"),
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
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("email").description("회원 이메일 주소"),
                                fieldWithPath("phoneNumber").description("휴대전화 번호"),
                                fieldWithPath("address.zipcode").description("우편번호"),
                                fieldWithPath("address.city").description("시/도"),
                                fieldWithPath("address.street").description("도로명 주소"),
                                fieldWithPath("address.detailAddress").description("상세 주소"),
                                fieldWithPath("coupons[0].memberCouponId").description("멤버가 보유한 쿠폰 id"),
                                fieldWithPath("coupons[0].name").description("쿠폰 이름"),
                                fieldWithPath("coupons[0].discountType").description("할인 타입 ['FIXED_RATE' / 'FLAT_RATE']"),
                                fieldWithPath("coupons[0].discountDegree").description("할인 정도 ( 원 / % )"),
                                fieldWithPath("coupons[0].availableMaxDiscount").description("적용가능 최대 할인 금액"),
                                fieldWithPath("coupons[0].availableMinPrice").description("사용가능한 최소 물품 가격"),
                                fieldWithPath("coupons[0].remaining").description("쿠폰 남은 개수"),
                                fieldWithPath("coupons[0].expiredDate").description("쿠폰 유효 기한"),
                                fieldWithPath("reward").description("회원이 보유 적립금"),
                                fieldWithPath("brochure").description("브로슈어 받은 적 있는지 여부 true/false"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.order_subscribe.href").description("구독 주문하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

//    @Test
//    public void validateOrderSheetSubscribe() throws Exception {
//       //given
//
//       //when & then
//        mockMvc.perform(get("/api/orders/sheet/subscribe/validate")
//                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaTypes.HAL_JSON)
//                        .content(objectMapper.writeValueAsString(null)))
//                .andDo(print())
//                .andExpect(status().isOk())
//        ;
//
//
//    }









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