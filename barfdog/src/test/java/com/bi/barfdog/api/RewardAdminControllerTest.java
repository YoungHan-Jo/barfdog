package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.Area;
import com.bi.barfdog.api.rewardDto.PublishToGroupDto;
import com.bi.barfdog.api.rewardDto.PublishToPersonalDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardStatus;
import com.bi.barfdog.domain.reward.RewardType;
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
import com.bi.barfdog.repository.orderItem.SelectOptionRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.BeforeSubscribeRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import org.junit.Before;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
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
public class RewardAdminControllerTest extends BaseTest {

    @Autowired
    EntityManager em;
    @Autowired
    AppProperties appProperties;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    DogRepository dogRepository;
    @Autowired
    SubscribeRepository subscribeRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    SubscribeRecipeRepository subscribeRecipeRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    SelectOptionRepository selectOptionRepository;
    @Autowired
    SettingRepository settingRepository;
    @Autowired
    SurveyReportRepository surveyReportRepository;
    @Autowired
    BeforeSubscribeRepository beforeSubscribeRepository;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    MemberCouponRepository memberCouponRepository;
    @Autowired
    ItemImageRepository itemImageRepository;
    @Autowired
    RewardRepository rewardRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


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
    @DisplayName("정상적으로 개인에게 적립금 발행하는 테스트")
    public void publishRewardsToPersonal() throws Exception {
       //given

        List<Long> memberIdList = new ArrayList<>();
        List<Integer> rewardList = new ArrayList<>();

        List<Member> allMembers = memberRepository.findAll();
        for (Member member : allMembers) {
            memberIdList.add(member.getId());
            rewardList.add(member.getReward());
        }

        String name = "적립금 발행 테스트";
        int amount = 5000;
        PublishToPersonalDto requestDto = PublishToPersonalDto.builder()
                .name(name)
                .amount(amount)
                .memberIdList(memberIdList)
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("admin_publish_rewards_personal",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_rewards").description("적립금 내역 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("적립금 이름"),
                                fieldWithPath("amount").description("적립금 수량"),
                                fieldWithPath("memberIdList").description("적립금 발행할 유저 인덱스 id 리스트"),
                                fieldWithPath("alimTalk").description("알림톡 여부 [true/false]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_rewards.href").description("적립금 내역 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<Reward> allRewards = rewardRepository.findAll();
        assertThat(allRewards.size()).isEqualTo(allMembers.size());
        assertThat(allRewards.get(0).getName()).isEqualTo(name);
        assertThat(allRewards.get(0).getTradeReward()).isEqualTo(amount);
        assertThat(allRewards.get(0).getRewardStatus()).isEqualTo(RewardStatus.SAVED);
        assertThat(allRewards.get(0).getRewardType()).isEqualTo(RewardType.ADMIN);

        for (int i = 0; i < memberIdList.size(); ++i) {
            Member member = memberRepository.findById(memberIdList.get(i)).get();
            assertThat(member.getReward()).isEqualTo(rewardList.get(i) + amount);
        }

    }

    @Test
    @DisplayName("정상적으로 개인에게 적립금 발행하는 테스트")
    public void publishRewardsToPersonal_alimTalk() throws Exception {
        //given
        memberRepository.deleteAll();

        em.flush();
        em.clear();
        List<Long> memberIdList = new ArrayList<>();

        Member admin = generateMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01099038544", Gender.FEMALE, Grade.더바프, 100000, true, "ADMIN,SUBSCRIBER,USER", true);
//        generateDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "댕댕이");
        int adminReward = admin.getReward();
        memberIdList.add(admin.getId());

//        Member member = generateMember("jyh1234@gmail.com", "김회원", appProperties.getUserPassword(), "01056862723", Gender.MALE, Grade.브론즈, 50000, false, "USER,SUBSCRIBER", true);
//        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");
//        int memberReward = member.getReward();
//        memberIdList.add(member.getId());

        String name = "적립금 발행 테스트";
        int amount = 5000;
        PublishToPersonalDto requestDto = PublishToPersonalDto.builder()
                .name(name)
                .amount(amount)
                .memberIdList(memberIdList)
                .alimTalk(true)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        em.flush();
        em.clear();

        Member findAdmin = memberRepository.findById(admin.getId()).get();
        assertThat(findAdmin.getReward()).isEqualTo(adminReward + amount);

//        Member findMember = memberRepository.findById(member.getId()).get();
//        assertThat(findMember.getReward()).isEqualTo(memberReward);

        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList.size()).isEqualTo(1);


    }


    @Test
    @DisplayName("적립금 개인 발행 시 파라미터값 부족 400")
    public void publishRewardsToPersonal_badRequest() throws Exception {
        //given

        List<Long> memberIdList = new ArrayList<>();

        List<Member> allMembers = memberRepository.findAll();
        for (Member member : allMembers) {
            memberIdList.add(member.getId());
        }

        String name = "적립금 발행 테스트";
        int amount = 5000;
        PublishToPersonalDto requestDto = PublishToPersonalDto.builder()
                .memberIdList(memberIdList)
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("적립금 개인발행 시 존재하지않는 유저 id일 경우")
    public void publishRewardsToPersonal_wrongMemberId() throws Exception {
        //given
        List<Long> memberIdList = new ArrayList<>();
        memberIdList.add(10000L);

        String name = "적립금 발행 테스트";
        int amount = 5000;
        PublishToPersonalDto requestDto = PublishToPersonalDto.builder()
                .name(name)
                .amount(amount)
                .memberIdList(memberIdList)
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/personal")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("정상적으로 특정 그룹에게 적립금 발급하는 테스트")
    public void publishToGroup() throws Exception {
       //given
        memberRepository.deleteAll();

        em.flush();
        em.clear();

        Member admin = generateMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01099038544", Gender.FEMALE, Grade.더바프, 100000, true, "ADMIN,SUBSCRIBER,USER", true);
        generateDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "댕댕이");

        int adminReward = admin.getReward();

        Member member = generateMember("jyh1234@gmail.com", "김회원", appProperties.getUserPassword(), "01056862723", Gender.MALE, Grade.브론즈, 50000, false, "USER,SUBSCRIBER", true);
        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");

        int memberReward = member.getReward();


        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.더바프);

        String name = "그룹 적립금 발행 테스트";
        int amount = 8000;
        PublishToGroupDto requestDto = PublishToGroupDto.builder()
                .name(name)
                .amount(amount)
                .subscribe(true)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1980")
                .birthYearTo("2020")
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("admin_publish_rewards_group",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_rewards").description("적립금 내역 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("적립금 이름"),
                                fieldWithPath("amount").description("적립금 수량"),
                                fieldWithPath("subscribe").description("구독 여부 true/false"),
                                fieldWithPath("longUnconnected").description("장기 미접속 true/false"),
                                fieldWithPath("gradeList").description("등급 리스트"),
                                fieldWithPath("area").description("지역 [ALL, METRO, NON_METRO]"),
                                fieldWithPath("birthYearFrom").description("태어난 년도 from"),
                                fieldWithPath("birthYearTo").description("태어난 년도 to"),
                                fieldWithPath("alimTalk").description("알림톡 여부 [true/false]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_rewards.href").description("적립금 내역 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Member findAdmin = memberRepository.findById(admin.getId()).get();
        assertThat(findAdmin.getReward()).isEqualTo(adminReward + amount);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getReward()).isEqualTo(memberReward);

        List<Reward> rewardList = rewardRepository.findAll();
        assertThat(rewardList.size()).isEqualTo(1);


    }

    @Test
    @DisplayName("정상적으로 특정 그룹에게 적립금 발급시 적립금 + 되었는지")
    public void publishToGroup_rewardPlus() throws Exception {
        //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);

        String name = "그룹 적립금 발행 테스트";
        int amount = 8000;
        PublishToGroupDto requestDto = PublishToGroupDto.builder()
                .name(name)
                .amount(amount)
                .subscribe(true)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1980")
                .birthYearTo("2020")
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        em.flush();
        em.clear();

        List<Member> findMembers = memberRepository.findByGrades(gradeList);


    }

    @Test
    @DisplayName("특정 그룹에게 적립금 발급 시 값 부족 400")
    public void publishToGroup_badRequest() throws Exception {
        //given
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);
        gradeList.add(Grade.실버);

        String name = "그룹 적립금 발행 테스트";
        int amount = 8000;
        PublishToGroupDto requestDto = PublishToGroupDto.builder()
                .subscribe(false)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("1980")
                .birthYearTo("2020")
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("특정 그룹에게 적립금 발급 시 생일 값 잘못됨 400")
    public void publishToGroup_badRequest_wrongBirth() throws Exception {
        //given

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);
        gradeList.add(Grade.실버);

        String name = "그룹 적립금 발행 테스트";
        int amount = 8000;
        PublishToGroupDto requestDto = PublishToGroupDto.builder()
                .name(name)
                .amount(amount)
                .subscribe(false)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("2000")
                .birthYearTo("1999")
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("특정 그룹에게 적립금 발급 시 생일 값 잘못됨 400")
    public void publishToGroup_wrongBirth() throws Exception {
        //given

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(Grade.브론즈);
        gradeList.add(Grade.실버);

        String name = "그룹 적립금 발행 테스트";
        int amount = 8000;
        PublishToGroupDto requestDto = PublishToGroupDto.builder()
                .name(name)
                .amount(amount)
                .subscribe(false)
                .longUnconnected(false)
                .gradeList(gradeList)
                .area(Area.ALL)
                .birthYearFrom("asdf")
                .birthYearTo("asdf")
                .alimTalk(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/rewards/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("정상적으로 해당 이메일 적립금 내역 조회하는 테스트")
    public void queryRewards() throws Exception {
       //given
        String email = appProperties.getUserEmail();
        Member member = memberRepository.findByEmail(email).get();

        IntStream.range(1,13).forEach(i -> {
            generateReward(member, i);
        });

        String from = LocalDate.of(2020, 05, 11).toString();
        String to = LocalDate.now().toString();

        //when & then
        mockMvc.perform(get("/api/admin/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("email",email)
                        .param("name","")
                        .param("from",from)
                        .param("to",to))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_rewards",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("publish_reward_personal").description("개인에게 적립금 발행하는 링크"),
                                linkWithRel("query_member").description("적립글 발행할 유저 검색하는 링크"),
                                linkWithRel("publish_reward_group").description("그룹에게 적립금 발행하는 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수"),
                                parameterWithName("email").description("검색할 유저 email [값 없으면 빈 문자열]"),
                                parameterWithName("name").description("검색할 유저 이름 [값 없으면 빈 문자열]"),
                                parameterWithName("from").description("가입날짜 'yyyy-MM-dd' 부터 "),
                                parameterWithName("to").description("가입날짜 'yyyy-MM-dd' 까지 ")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryAdminRewardsDtoList[0].id").description("적립금내역 인덱스 id"),
                                fieldWithPath("_embedded.queryAdminRewardsDtoList[0].createdDate").description("발급 날짜시간"),
                                fieldWithPath("_embedded.queryAdminRewardsDtoList[0].name").description("적립금 이름"),
                                fieldWithPath("_embedded.queryAdminRewardsDtoList[0].amount").description("적립금 수량"),
                                fieldWithPath("_embedded.queryAdminRewardsDtoList[0].memberName").description("유저 이름"),
                                fieldWithPath("_embedded.queryAdminRewardsDtoList[0].email").description("유저 이메일"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.publish_reward_personal.href").description("개인에게 적립금 발행하는 링크"),
                                fieldWithPath("_links.query_member.href").description("적립글 발행할 유저 검색하는 링크"),
                                fieldWithPath("_links.publish_reward_group.href").description("그룹에게 적립금 발행하는 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        List<Reward> allRewards = rewardRepository.findAll();
        assertThat(allRewards.size()).isEqualTo(12);


    }

    @Test
    @DisplayName("정상적으로 해당 이메일 적립금 내역 조회하는 테스트 - 검색 키워드 포함")
    public void queryRewards_contains_keyword() throws Exception {
        //given
        String email = appProperties.getUserEmail();
        Member member = memberRepository.findByEmail(email).get();

        IntStream.range(1,13).forEach(i -> {
            generateReward(member, i);
        });

        String from = LocalDate.of(2020, 05, 11).toString();
        String to = LocalDate.now().toString();

        //when & then
        mockMvc.perform(get("/api/admin/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "1")
                        .param("size", "5")
                        .param("email","user@")
                        .param("name","")
                        .param("from",from)
                        .param("to",to))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(12))
        ;
    }

    @Test
    @DisplayName("정상적으로 이름으로 적립금 내역 조회 성공")
    public void queryRewards_byName() throws Exception {
        //given
        String email = appProperties.getUserEmail();
        Member member = memberRepository.findByEmail(email).get();

        String adminEmail = appProperties.getAdminEmail();
        Member admin = memberRepository.findByEmail(adminEmail).get();

        IntStream.range(1,7).forEach(i -> {
            generateReward(member, i);
            generateReward(admin, i);
        });


        String from = LocalDate.of(2020, 05, 11).toString();
        String to = LocalDate.now().toString();

        //when & then
        mockMvc.perform(get("/api/admin/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("email", "")
                        .param("name", "김회원")
                        .param("from", from)
                        .param("to", to))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryAdminRewardsDtoList[0].memberName").value("김회원"))
                .andExpect(jsonPath("page.totalElements").value(6));
    }

    @Test
    @DisplayName("정상적으로 이름으로 적립금 내역 조회 성공 - 글자 포함")
    public void queryRewards_byName_contains_keyword() throws Exception {
        //given
        String email = appProperties.getUserEmail();
        Member member = memberRepository.findByEmail(email).get();

        String adminEmail = appProperties.getAdminEmail();
        Member admin = memberRepository.findByEmail(adminEmail).get();

        IntStream.range(1,7).forEach(i -> {
            generateReward(member, i);
            generateReward(admin, i);
        });


        String from = LocalDate.of(2020, 05, 11).toString();
        String to = LocalDate.now().toString();

        //when & then
        mockMvc.perform(get("/api/admin/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("email", "")
                        .param("name", "김회")
                        .param("from", from)
                        .param("to", to))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryAdminRewardsDtoList[0].memberName").value("김회원"))
                .andExpect(jsonPath("page.totalElements").value(6));
    }

    @Test
    @DisplayName("유저정보 없이 적립금 내역 조회 성공")
    public void queryRewards_noCond() throws Exception {
        //given
        String email = appProperties.getUserEmail();
        Member member = memberRepository.findByEmail(email).get();

        String adminEmail = appProperties.getAdminEmail();
        Member admin = memberRepository.findByEmail(adminEmail).get();

        IntStream.range(1,7).forEach(i -> {
            generateReward(member, i);
            generateReward(admin, i);
        });

        String from = LocalDate.of(2020, 05, 11).toString();
        String to = LocalDate.now().toString();

        //when & then
        mockMvc.perform(get("/api/admin/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("email", "")
                        .param("name", "")
                        .param("from", from)
                        .param("to", to))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(12));
    }


    @Test
    @DisplayName("적립금 조회 시 페이지 정보 없을 경우 0페이지 20개 조회")
    public void queryRewards_noPage() throws Exception {
        //given
        String email = appProperties.getUserEmail();
        Member member = memberRepository.findByEmail(email).get();

        IntStream.range(1,25).forEach(i -> {
            generateReward(member, i);
        });

        String from = LocalDate.of(2020, 05, 11).toString();
        String to = LocalDate.now().toString();

        //when & then
        mockMvc.perform(get("/api/admin/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("email", "")
                        .param("name", "")
                        .param("from", from)
                        .param("to", to))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("적립금 내역 조회 시 설정 날자 없음 400")
    public void queryRewards_noDate_400() throws Exception {
        //given
        String email = appProperties.getUserEmail();
        Member member = memberRepository.findByEmail(email).get();

        IntStream.range(1,7).forEach(i -> {
            generateReward(member, i);
        });

        String from = LocalDate.of(2020, 05, 11).toString();
        String to = LocalDate.now().toString();


        //when & then
        mockMvc.perform(get("/api/admin/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("email", "")
                        .param("name", "김회원")
                        .param("from", "")
                        .param("to", ""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("적립금 조회 시 날짜 설정 잘못 되었을 경우 400")
    public void queryRewards_wrongDate() throws Exception {
        //given
        String email = appProperties.getUserEmail();
        Member member = memberRepository.findByEmail(email).get();

        IntStream.range(1,25).forEach(i -> {
            generateReward(member, i);
        });

        String from = LocalDate.now().toString();
        String to = LocalDate.of(2020, 05, 11).toString();

        //when & then
        mockMvc.perform(get("/api/admin/rewards")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size", "5")
                        .param("page", "1")
                        .param("email", "")
                        .param("name", "김회원")
                        .param("from", from)
                        .param("to", to))
                .andDo(print())
                .andExpect(status().isBadRequest());
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
    
    
    
    
    
    
    
    
    

    private void generateReward(Member member, int i) {
        Reward reward = Reward.builder()
                .member(member)
                .name("적립금" + i)
                .rewardType(RewardType.ADMIN)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(5000 + i)
                .build();
        rewardRepository.save(reward);
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