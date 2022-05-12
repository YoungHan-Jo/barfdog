package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.MemberConditionPublishCoupon;
import com.bi.barfdog.api.memberDto.MemberUpdateRequestDto;
import com.bi.barfdog.api.memberDto.UpdatePasswordRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class MemberApiControllerTest extends BaseTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    AppProperties appProperties;


    @Test
    @DisplayName("정상적으로 회원정보 조회하는 테스트")
    public void queryMember() throws Exception {
       //Given

       //when & then
        mockMvc.perform(get("/api/members")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_member",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_member").description("회원정보 수정 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("email").description("회원 이메일(로그인 ID)"),
                                fieldWithPath("phoneNumber").description("휴대폰 번호"),
                                fieldWithPath("address.zipcode").description("우편 번호"),
                                fieldWithPath("address.city").description("시/도"),
                                fieldWithPath("address.street").description("도로명주소"),
                                fieldWithPath("address.detailAddress").description("상세주소"),
                                fieldWithPath("birthday").description("생년월일 'yyyymmdd' 길이 8 문자열 형식"),
                                fieldWithPath("gender").description("성별"),
                                fieldWithPath("provider").description("sns 로그인 제공사(네이버/카카오)"),
                                fieldWithPath("providerId").description("sns 로그인 제공사 고유 id"),
                                fieldWithPath("receiveSms").description("sms 수신 여부"),
                                fieldWithPath("receiveEmail").description("email 수신 여부"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_member.href").description("회원정보 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("토큰이 없을 경우 302 found 나오는 테스트")
    public void queryMember_not_found() throws Exception {
       //Given

       //when & then
        mockMvc.perform(get("/api/members")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    @DisplayName("정상적으로 유저 정보 수정하는 테스트")
    public void updateMember() throws Exception {
       //Given
        String phoneNumber = "01099990000";
        String birthday = "19901023";
        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
                .name("김바프")
                .password(appProperties.getUserPassword())
                .phoneNumber(phoneNumber)
                .address(new Address("12345", "서울특별시", "강남대로123", "강남빌딩 102호"))
                .birthday(birthday)
                .gender(Gender.NONE)
                .receiveSms(false)
                .receiveEmail(false)
                .build();
        //when & then
        mockMvc.perform(put("/api/members")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_member",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_member").description("회원 정보 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("password").description("회원 현재 비밀번호"),
                                fieldWithPath("phoneNumber").description("휴대폰 번호 '010xxxxxxxx' -없는 문자열"),
                                fieldWithPath("address.zipcode").description("우편 번호"),
                                fieldWithPath("address.city").description("시/도"),
                                fieldWithPath("address.street").description("도로명주소"),
                                fieldWithPath("address.detailAddress").description("상세주소"),
                                fieldWithPath("birthday").description("생년월일 'yyyymmdd' 길이 8 문자열"),
                                fieldWithPath("gender").description("성별 [MALE, FEMALE, NONE]"),
                                fieldWithPath("receiveSms").description("sms 수신 여부 [true/false]"),
                                fieldWithPath("receiveEmail").description("email 수신 여부 [true/false]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_member.href").description("회원 정보 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        Member findMember = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        assertThat(findMember.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(findMember.getBirthday()).isEqualTo(birthday);
        assertThat(findMember.getAgreement().isReceiveSms()).isFalse();
        assertThat(findMember.getAgreement().isReceiveEmail()).isFalse();
    }

    @Test
    @DisplayName("회원 정보 수정 시 요청 파라미터가 부족할 때 bad request 나오는 테스트")
    public void updateMember_bad_request() throws Exception {
        //Given
        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
                .build();
        //when & then
        mockMvc.perform(put("/api/members")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 정보 수정 시 비밀번호가 틀렸을 때 bad request 나오는 테스트 ")
    public void updateMember_wrong_password() throws Exception {
        //Given
        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
                .name("김바프")
                .password("asdfasdf")
                .phoneNumber("01099990000")
                .address(new Address("12345", "서울특별시", "강남대로123", "강남빌딩 102호"))
                .birthday("19901023")
                .gender(Gender.NONE)
                .receiveSms(false)
                .receiveEmail(false)
                .build();
        //when & then
        mockMvc.perform(put("/api/members")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원 정보 수정 시 휴대전화가 중복일 경우 conflict 나오는 테스트 ")
    public void updateMember_phone_conflict() throws Exception {
        //Given
        MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
                .name("김바프")
                .password(appProperties.getUserPassword())
                .phoneNumber("01056785678")
                .address(new Address("12345", "서울특별시", "강남대로123", "강남빌딩 102호"))
                .birthday("19901023")
                .gender(Gender.NONE)
                .receiveSms(false)
                .receiveEmail(false)
                .build();
        //when & then
        mockMvc.perform(put("/api/members")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isConflict());

    }
    
    @Test
    @DisplayName("정상적으로 비밀번호 변경하는 테스트")
    public void updatePassword() throws Exception {
       //Given
        UpdatePasswordRequestDto requestDto = UpdatePasswordRequestDto.builder()
                .password(appProperties.getUserPassword())
                .newPassword("1234")
                .newPasswordConfirm("1234")
                .build();
        //when & then
        mockMvc.perform(put("/api/members/password")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_password",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("password").description("회원 현재 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호"),
                                fieldWithPath("newPasswordConfirm").description("새  비밀번호 확인")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 변경 요청 값이 부족할 경우 bad request 나오는 테스트")
    public void updatePassword_bad_request() throws Exception {
        //Given
        UpdatePasswordRequestDto requestDto = UpdatePasswordRequestDto.builder()
                .password(appProperties.getUserPassword())
                .build();
        //when & then
        mockMvc.perform(put("/api/members/password")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("비밀번호가 다를 경우 bad request 나오는 테스트")
    public void updatePassword_wrong_password() throws Exception {
        //Given
        UpdatePasswordRequestDto requestDto = UpdatePasswordRequestDto.builder()
                .password("잘못된패스워드")
                .newPassword("1234")
                .newPasswordConfirm("1234")
                .build();
        //when & then
        mockMvc.perform(put("/api/members/password")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("새 비밀번호, 새 비밀번호 확인이 다르면 bad request 나오는 테스트")
    public void updatePassword_wrong_new_password_bad_request() throws Exception {
        //Given
        UpdatePasswordRequestDto requestDto = UpdatePasswordRequestDto.builder()
                .password(appProperties.getUserPassword())
                .newPassword("1234")
                .newPasswordConfirm("비밀번호확인이다름")
                .build();
        //when & then
        mockMvc.perform(put("/api/members/password")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("쿠폰 발행 시 이메일로 검색한 유저 조회하는 테스트")
    public void queryMembersInPublishCoupon() throws Exception {
       //given
        MemberConditionPublishCoupon condition = MemberConditionPublishCoupon.builder()
                .email(appProperties.getAdminEmail())
                .build();

        //when & then
        mockMvc.perform(get("/api/members/publicationCoupon")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(condition)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.memberPublishCouponResponseDtoList[0].dogName").value("대표견"))
                .andDo(document("query_members_in_publishCoupon",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("publish_coupon_personal").description("개인 쿠폰 발행 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestFields(
                                fieldWithPath("email").description("검색할 email"),
                                fieldWithPath("name").description("검색할 유저 이름 [email과 name 둘 중 하나만 입력 해야 함, 입력하지 않은 값은 null 로 ]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.memberPublishCouponResponseDtoList[0].memberId").description("회원 id"),
                                fieldWithPath("_embedded.memberPublishCouponResponseDtoList[0].grade").description("회원 등급"),
                                fieldWithPath("_embedded.memberPublishCouponResponseDtoList[0].name").description("회원 이름"),
                                fieldWithPath("_embedded.memberPublishCouponResponseDtoList[0].email").description("회원 email"),
                                fieldWithPath("_embedded.memberPublishCouponResponseDtoList[0].phoneNumber").description("휴대전화 번호"),
                                fieldWithPath("_embedded.memberPublishCouponResponseDtoList[0].dogName").description("대표견 이름"),
                                fieldWithPath("_embedded.memberPublishCouponResponseDtoList[0].accumulatedAmount").description("구매 누적 금액"),
                                fieldWithPath("_embedded.memberPublishCouponResponseDtoList[0].subscribe").description("구독 여부"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.publish_coupon_personal.href").description("개인 쿠폰 발행 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        ;
    }

    @Test
    @DisplayName("쿠폰 발행 시 이메일로 검색한 유저 대표견이 없으면 dog name에 null 나오는 테스트")
    public void queryMembersInPublishCoupon_dog_null() throws Exception {
        //given
        MemberConditionPublishCoupon condition = MemberConditionPublishCoupon.builder()
                .email(appProperties.getUserEmail())
                .build();

        //when & then
        mockMvc.perform(get("/api/members/publicationCoupon")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(condition)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.memberPublishCouponResponseDtoList[0].dogName").isEmpty())
        ;
    }

    @Test
    @DisplayName("쿠폰 발행 시 이름으로 검색한 유저 조회하는 테스트")
    public void queryMembersInPublishCoupon_search_by_name() throws Exception {
        //given
        MemberConditionPublishCoupon condition = MemberConditionPublishCoupon.builder()
                .name("관리자")
                .build();

        //when & then
        mockMvc.perform(get("/api/members/publicationCoupon")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(condition)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.memberPublishCouponResponseDtoList[0].dogName").value("대표견"))
                .andExpect(jsonPath("_embedded.memberPublishCouponResponseDtoList[0].email").value(appProperties.getAdminEmail()))
        ;
    }

    @Test
    @DisplayName("일반 유저가 쿠폰 발행 시 검색한 유저 조회 api 호출하면 forbidden 나오는 테스트")
    public void queryMembersInPublishCoupon_forbidden() throws Exception {
        //given
        MemberConditionPublishCoupon condition = MemberConditionPublishCoupon.builder()
                .email(appProperties.getAdminEmail())
                .build();

        //when & then
        mockMvc.perform(get("/api/members/publicationCoupon")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(condition)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("검색조건이 하나도 없으면 bad request 나오는 테스트")
    public void queryMembersInPublishCoupon_emptyCondition() throws Exception {
        //given
        MemberConditionPublishCoupon condition = MemberConditionPublishCoupon.builder()
                .email("")
                .name("")
                .build();

        //when & then
        mockMvc.perform(get("/api/members/publicationCoupon")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(condition)))
                .andDo(print())
                .andExpect(status().isBadRequest())
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