package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.DeleteMemberDto;
import com.bi.barfdog.api.memberDto.MemberUpdateRequestDto;
import com.bi.barfdog.api.memberDto.UpdatePasswordRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.repository.member.MemberRepository;
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

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
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
public class MemberApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;

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
    @DisplayName("정상적으로 회원 탈퇴하는 테스트")
    public void deleteMember() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        String provider = "naver";
        String providerId = "sldkfjwoigjlxkcjcfoisedjfl";

        member.connectSns(provider, providerId);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.isWithdrawal()).isFalse();
        assertThat(findMember.getProvider()).isEqualTo(provider);
        assertThat(findMember.getProviderId()).isEqualTo(providerId);

        DeleteMemberDto requestDto = DeleteMemberDto.builder()
                .password(appProperties.getUserPassword())
                .build();

        //when & then
        mockMvc.perform(delete("/api/members")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("withdrawal",
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
                                fieldWithPath("password").description("회원 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member member1 = memberRepository.findById(member.getId()).get();

        assertThat(member1.isWithdrawal()).isTrue();
        assertThat(member1.getProvider()).isEqualTo("");
        assertThat(member1.getProviderId()).isEqualTo("");
    }

    @Test
    @DisplayName("회원 탈퇴시 비밀번호 잘못된 경우 400")
    public void deleteMember_wrongPassword() throws Exception {
        //given
        DeleteMemberDto requestDto = DeleteMemberDto.builder()
                .password("wrongPassword")
                .build();

        //when & then
        mockMvc.perform(delete("/api/members")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("정상적으로 비밀번호 변경하는 테스트")
    public void updatePassword() throws Exception {
       //Given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        member.temporaryPassword(member.getPassword());

        assertThat(member.isTemporaryPassword()).isTrue();

        String newPassword = "1234";
        UpdatePasswordRequestDto requestDto = UpdatePasswordRequestDto.builder()
                .password(appProperties.getUserPassword())
                .newPassword(newPassword)
                .newPasswordConfirm(newPassword)
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

        em.flush();
        em.clear();

        Member findMember = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        assertThat(bCryptPasswordEncoder.matches(newPassword, findMember.getPassword())).isTrue();
        assertThat(findMember.isTemporaryPassword()).isFalse();


    }

    @Test
    @DisplayName("비밀번호 변경시 토큰 없음 401")
    public void updatePassword_no_token() throws Exception {
        //Given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        member.temporaryPassword(member.getPassword());

        assertThat(member.isTemporaryPassword()).isTrue();

        String newPassword = "1234";
        UpdatePasswordRequestDto requestDto = UpdatePasswordRequestDto.builder()
                .password(appProperties.getUserPassword())
                .newPassword(newPassword)
                .newPasswordConfirm(newPassword)
                .build();
        //when & then
        mockMvc.perform(put("/api/members/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

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
    @DisplayName("정상적으로 연동왼 sns 검색하는 테스트")
    public void querySns() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        String provider = "naver";
        member.connectSns(provider,"aslsdjfklweigjksldf");

        //when & then
        mockMvc.perform(get("/api/members/sns")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("provider").value(provider))
                .andDo(document("query_snsProvider",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("unconnect_sns").description("sns 연동해제 링크"),
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
                                fieldWithPath("provider").description("연동된 sns 제공사 ['naver','kakao'] 연동된 sns가 없으면 [null or '' 빈문자열] "),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.unconnect_sns.href").description("sns 연동해제 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 sns 연결해제")
    public void unconnectSns() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        String provider = "naver";
        member.connectSns(provider,"alsdkjfsdlkfjsldkfj");

        assertThat(member.getProvider()).isEqualTo(provider);

        //when & then
        mockMvc.perform(delete("/api/members/sns")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("unconnect_sns",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_sns").description("sns 연동해제 링크"),
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
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_sns.href").description("연동된 sns 제공사 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getProvider()).isEqualTo("");
        assertThat(findMember.getProviderId()).isEqualTo("");


    }






    @Test
    @DisplayName("쿠폰 발행 시 이메일로 검색한 유저 조회하는 테스트")
    public void queryMembersInPublishCoupon() throws Exception {
       //given

        //when & then
        String adminEmail = appProperties.getUserEmail();
        mockMvc.perform(get("/api/members/publication")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("email", appProperties.getAdminEmail())
                        .param("name",""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.memberPublishResponseDtoList[0].dogName").value("대표견"))
                .andDo(document("query_members_in_publication",
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
                        requestParameters(
                                parameterWithName("email").description("검색할 email"),
                                parameterWithName("name").description("검색할 유저 이름 [email과 name 둘 중 하나만 입력 해야 함, 입력하지 않을 값은 빈 문자열로 ]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.memberPublishResponseDtoList[0].memberId").description("회원 id"),
                                fieldWithPath("_embedded.memberPublishResponseDtoList[0].grade").description("회원 등급"),
                                fieldWithPath("_embedded.memberPublishResponseDtoList[0].name").description("회원 이름"),
                                fieldWithPath("_embedded.memberPublishResponseDtoList[0].email").description("회원 email"),
                                fieldWithPath("_embedded.memberPublishResponseDtoList[0].phoneNumber").description("휴대전화 번호"),
                                fieldWithPath("_embedded.memberPublishResponseDtoList[0].dogName").description("대표견 이름"),
                                fieldWithPath("_embedded.memberPublishResponseDtoList[0].accumulatedAmount").description("구매 누적 금액"),
                                fieldWithPath("_embedded.memberPublishResponseDtoList[0].subscribe").description("구독 여부"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.publish_coupon_personal.href").description("개인 쿠폰 발행 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("쿠폰 발행 시 이메일로 검색한 유저 대표견이 없으면 dog name에 null 나오는 테스트")
    public void queryMembersInPublishCoupon_dog_null() throws Exception {
        //given


        //when & then
        mockMvc.perform(get("/api/members/publication")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("email","abc@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.memberPublishResponseDtoList[0].dogName").isEmpty())
        ;
    }

    @Test
    @DisplayName("쿠폰 발행 시 이름으로 검색한 유저 조회하는 테스트")
    public void queryMembersInPublishCoupon_search_by_name() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/api/members/publication")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("name","관리자"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.memberPublishResponseDtoList[0].dogName").value("대표견"))
                .andExpect(jsonPath("_embedded.memberPublishResponseDtoList[0].email").value(appProperties.getAdminEmail()))
        ;
    }

    @Test
    @DisplayName("일반 유저가 쿠폰 발행 시 검색한 유저 조회 api 호출하면 forbidden 나오는 테스트")
    public void queryMembersInPublishCoupon_forbidden() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/api/members/publication")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("name","관리자"))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("검색조건이 하나도 없으면 bad request 나오는 테스트")
    public void queryMembersInPublishCoupon_emptyCondition() throws Exception {
        //given

        //when & then
        mockMvc.perform(get("/api/members/publication")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
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