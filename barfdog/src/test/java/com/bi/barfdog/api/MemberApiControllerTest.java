package com.bi.barfdog.api;

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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_member",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update-member").description("회원정보 수정 요청 링크"),
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
                                fieldWithPath("_links.update-member.href").description("회원정보 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("로그인이 되어있지 않을 경우 302 found 나오는 테스트")
    public void queryMember_not_found() throws Exception {
       //Given

       //when & then
        mockMvc.perform(get("/api/members")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isFound())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_member",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query-member").description("회원 정보 조회 링크"),
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
                                fieldWithPath("_links.query-member.href").description("회원 정보 조회 링크"),
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
    







    private String getBearerToken() throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .username(appProperties.getUserEmail())
                .password(appProperties.getUserPassword())
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