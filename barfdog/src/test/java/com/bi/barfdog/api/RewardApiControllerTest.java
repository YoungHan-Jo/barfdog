package com.bi.barfdog.api;

import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardPoint;
import com.bi.barfdog.domain.reward.RewardStatus;
import com.bi.barfdog.domain.reward.RewardType;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.repository.RewardRepository;
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
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class RewardApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RewardRepository rewardRepository;

    @Test
    @DisplayName("정상적으로 적립금 리스트 조회하는 테스트")
    public void queryRewards() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,14).forEach(i -> {
            generateInviteReward(member, i);
        });

        //when & then
        mockMvc.perform(get("/api/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_rewards",
                        links(
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
                                fieldWithPath("reward").description("사용 가능한 적립금"),
                                fieldWithPath("pagedModel._embedded.queryRewardsDtoList[0].createdTime").description("발행 날짜"),
                                fieldWithPath("pagedModel._embedded.queryRewardsDtoList[0].name").description("적립금 이름"),
                                fieldWithPath("pagedModel._embedded.queryRewardsDtoList[0].rewardStatus").description("적립금 상태 [SAVED, USED]"),
                                fieldWithPath("pagedModel._embedded.queryRewardsDtoList[0].tradeReward").description("거래 적립금 금액(절대값)"),
                                fieldWithPath("pagedModel.page.size").description("한 페이지 당 개수"),
                                fieldWithPath("pagedModel.page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("pagedModel.page.totalPages").description("총 페이지 수"),
                                fieldWithPath("pagedModel.page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("pagedModel._links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("pagedModel._links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("pagedModel._links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("pagedModel._links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("pagedModel._links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 친구초대 화면 조회하는 테스트")
    public void queryInvite() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        String recommendCode = BarfUtils.generateRandomCode();
        member.setRecommendCode(recommendCode);

        IntStream.range(1,25).forEach(i -> {
            generateMember(i,member.getMyRecommendationCode());
        });

        IntStream.range(1,14).forEach(i -> {
            generateInviteReward(member, i);
        });

       //when & then
        mockMvc.perform(get("/api/rewards/invite")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("recommend").value(recommendCode))
                .andExpect(jsonPath("joinedCount").value(24))
                .andExpect(jsonPath("orderedCount").value(13))
                .andExpect(jsonPath("totalRewards").value(RewardPoint.RECOMMEND*13))
                .andDo(document("query_rewards_invite",
                        links(
                                linkWithRel("recommend_friend").description("친구 추천코드 입력 링크"),
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
                                fieldWithPath("recommend").description("추천코드 [없으면 null]"),
                                fieldWithPath("joinedCount").description("가입한 친구 수"),
                                fieldWithPath("orderedCount").description("주문한 친구 수"),
                                fieldWithPath("totalRewards").description("총 적립 포인트"),
                                fieldWithPath("pagedModel._embedded.queryRewardsDtoList[0].createdTime").description("발행 날짜"),
                                fieldWithPath("pagedModel._embedded.queryRewardsDtoList[0].name").description("적립금 이름"),
                                fieldWithPath("pagedModel._embedded.queryRewardsDtoList[0].rewardStatus").description("적립금 상태 [SAVED, USED]"),
                                fieldWithPath("pagedModel._embedded.queryRewardsDtoList[0].tradeReward").description("거래 적립금 금액(절대값)"),
                                fieldWithPath("pagedModel.page.size").description("한 페이지 당 개수"),
                                fieldWithPath("pagedModel.page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("pagedModel.page.totalPages").description("총 페이지 수"),
                                fieldWithPath("pagedModel.page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("pagedModel._links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("pagedModel._links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("pagedModel._links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("pagedModel._links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("pagedModel._links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.recommend_friend.href").description("친구 추천코드 입력 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

    }








    private void generateMember(int i, String recommendCode) {
        Member member = Member.builder()
                .email("email@gmail.com" + i)
                .name("일반 회원" + i)
                .password("1234")
                .phoneNumber("010123455"+i)
                .address(new Address("12345","부산광역시","부산광역시 해운대구 센텀2로 19","106호"))
                .birthday("19991201")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .recommendCode(recommendCode)
                .myRecommendationCode(BarfUtils.generateRandomCode())
                .grade(Grade.BRONZE)
                .reward(0)
                .accumulatedAmount(0)
                .firstReward(new FirstReward(true, false))
                .roles("USER")
                .build();

        memberRepository.save(member);
    }







    private Reward generateInviteReward(Member member, int i) {
        Reward reward = Reward.builder()
                .member(member)
                .name("초대 적립금" + i)
                .rewardType(RewardType.INVITE)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(3000)
                .build();
        return rewardRepository.save(reward);
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