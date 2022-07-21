package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.CodeCouponRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import org.junit.Before;
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
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
    MemberCouponRepository memberCouponRepository;

    @Autowired
    AppProperties appProperties;

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

    @Before
    public void setUp() {
        memberCouponRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemImageRepository.deleteAll();
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
        deliveryRepository.deleteAll();
    }
    @Test
    @DisplayName("정상적으로 쿠폰함 리스트 조회")
    public void queryCoupons() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        IntStream.range(1,14).forEach(i -> {
            Coupon coupon = generateCodeCoupon(i);
            generateMemberCoupon(member, coupon, i, CouponStatus.ACTIVE);
        });

        IntStream.range(1,3).forEach(i -> {
            Coupon coupon = generateGeneralCoupon(i);
            generateMemberCoupon(member, coupon, i, CouponStatus.INACTIVE);
        });

        IntStream.range(1,4).forEach(i -> {
            Coupon coupon = generateCodeCoupon(i);
            generateMemberCoupon(admin, coupon, i, CouponStatus.ACTIVE);
        });


        //when & then
        mockMvc.perform(get("/api/coupons")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalCount").value(15))
                .andExpect(jsonPath("availableCount").value(13))
                .andExpect(jsonPath("couponsPageDto.page.totalElements").value(15))
                .andDo(document("query_coupons",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("get_code_coupon").description("코드로 쿠폰 등록하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("totalCount").description("쿠폰 전체 개수"),
                                fieldWithPath("availableCount").description("사용 가능한 쿠폰 개수"),
                                fieldWithPath("couponsPageDto._embedded.queryCouponsDtoList[0].id").description("보유 쿠폰 id"),
                                fieldWithPath("couponsPageDto._embedded.queryCouponsDtoList[0].status").description("보유 쿠폰 상태 [ACTIVE/INACTIVE] 각, 사용가능/사용불가"),
                                fieldWithPath("couponsPageDto._embedded.queryCouponsDtoList[0].name").description("쿠폰 이름"),
                                fieldWithPath("couponsPageDto._embedded.queryCouponsDtoList[0].description").description("쿠폰 설명"),
                                fieldWithPath("couponsPageDto._embedded.queryCouponsDtoList[0].expiredDate").description("쿠폰 만료기한"),
                                fieldWithPath("couponsPageDto._embedded.queryCouponsDtoList[0].discount").description("쿠폰 할인 [ 'xxxx원' or 'xx%' ]"),
                                fieldWithPath("couponsPageDto._embedded.queryCouponsDtoList[0].remaining").description("쿠폰 남은수량"),
                                fieldWithPath("couponsPageDto.page.size").description("한 페이지 당 개수"),
                                fieldWithPath("couponsPageDto.page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("couponsPageDto.page.totalPages").description("총 페이지 수"),
                                fieldWithPath("couponsPageDto.page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("couponsPageDto._links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("couponsPageDto._links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("couponsPageDto._links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("couponsPageDto._links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("couponsPageDto._links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.get_code_coupon.href").description("코드로 쿠폰 등록하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 쿠폰 등록")
    public void getCodeCoupon() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Coupon coupon = generateCodeCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.INACTIVE);

        CodeCouponRequestDto requestDto = CodeCouponRequestDto.builder()
                .code(coupon.getCode())
                .password(appProperties.getUserPassword())
                .build();

        //when & then
        mockMvc.perform(put("/api/coupons/code")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get_code_coupon",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_coupons").description("보유 쿠폰 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("code").description("쿠폰 코드"),
                                fieldWithPath("password").description("유저 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_coupons.href").description("보유 쿠폰 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCoupon.getId()).get();
        assertThat(findMemberCoupon.getMemberCouponStatus()).isEqualTo(CouponStatus.ACTIVE);

    }

    @Test
    @DisplayName("이미 사용된 쿠폰입니다.")
    public void getCodeCoupon_already_used() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Coupon coupon = generateCodeCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.ACTIVE);

        CodeCouponRequestDto requestDto = CodeCouponRequestDto.builder()
                .code(coupon.getCode())
                .password(appProperties.getUserPassword())
                .build();

        //when & then
        mockMvc.perform(put("/api/coupons/code")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("쿠폰 등록 시 비밀번호가 잘못된 경우")
    public void getCodeCoupon_wrong_password() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Coupon coupon = generateCodeCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.INACTIVE);

        CodeCouponRequestDto requestDto = CodeCouponRequestDto.builder()
                .code(coupon.getCode())
                .password(appProperties.getAdminPassword())
                .build();

        //when & then
        mockMvc.perform(put("/api/coupons/code")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("보유한 쿠폰이 아닐 경우")
    public void getCodeCoupon_have_no_coupon() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Coupon coupon = generateCodeCoupon(1);

        CodeCouponRequestDto requestDto = CodeCouponRequestDto.builder()
                .code(coupon.getCode())
                .password(appProperties.getAdminPassword())
                .build();

        //when & then
        mockMvc.perform(put("/api/coupons/code")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }





    private MemberCoupon generateMemberCoupon(Member member, Coupon coupon, int remaining, CouponStatus status) {
        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .expiredDate(LocalDateTime.now().plusDays(remaining))
                .remaining(remaining)
                .memberCouponStatus(status)
                .build();
        return memberCouponRepository.save(memberCoupon);
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