package com.bi.barfdog.api;

import com.bi.barfdog.api.barfDto.FriendTalkAllDto;
import com.bi.barfdog.api.barfDto.FriendTalkGroupDto;
import com.bi.barfdog.api.couponDto.Area;
import com.bi.barfdog.api.guestDto.SaveGuestRequest;
import com.bi.barfdog.api.settingDto.UpdateSettingDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.guest.Guest;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.guest.GuestRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class AdminApiControllerTest extends BaseTest {

    @Autowired
    AppProperties appProperties;
    @Autowired
    EntityManager em;
    @Autowired
    SettingRepository settingRepository;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberCouponRepository memberCouponRepository;

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
    @Autowired
    GuestRepository guestRepository;

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
    @DisplayName("정상적으로 사이트 설정 조회")
    public void querySetting() throws Exception {
       //given

       //when & then
        mockMvc.perform(get("/api/admin/setting")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_setting",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("update_setting").description("관리자 설정 수정 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("createdDate").description("생성 날짜"),
                                fieldWithPath("modifiedDate").description("최종 수정 날짜"),
                                fieldWithPath("id").description("설정 id"),
                                fieldWithPath("activityConstant.activityVeryLittle").description("활동량 매우 적음"),
                                fieldWithPath("activityConstant.activityLittle").description("활동량 적음"),
                                fieldWithPath("activityConstant.activityNormal").description("활동량 보통"),
                                fieldWithPath("activityConstant.activityMuch").description("활동량 많음"),
                                fieldWithPath("activityConstant.activityVeryMuch").description("활동량 매우 많음"),
                                fieldWithPath("snackConstant.snackLittle").description("간식량 적음"),
                                fieldWithPath("snackConstant.snackNormal").description("간식량 보통"),
                                fieldWithPath("snackConstant.snackMuch").description("간식량 많음"),
                                fieldWithPath("deliveryConstant.price").description("기본 택배비"),
                                fieldWithPath("deliveryConstant.freeCondition").description("무료배송 조건. xx원 이상 무료배송"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.update_setting.href").description("관리자 설정 수정 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    public void updateSetting() throws Exception {
       //given

        int price = 3000;
        int freeCondition = 100000;
        BigDecimal snackMuch = BigDecimal.valueOf(-1.30).setScale(2);
        BigDecimal snackNormal = BigDecimal.valueOf(0).setScale(2);
        BigDecimal snackLittle = BigDecimal.valueOf(1.30).setScale(2);
        BigDecimal activityVeryMuch = BigDecimal.valueOf(1.60).setScale(2);
        BigDecimal activityMuch = BigDecimal.valueOf(1.00).setScale(2);
        BigDecimal activityLittle = BigDecimal.valueOf(-1.00).setScale(2);
        BigDecimal activityNormal = BigDecimal.valueOf(0).setScale(2);
        BigDecimal activityVeryLittle = BigDecimal.valueOf(-1.60).setScale(2);

        UpdateSettingDto requestDto = UpdateSettingDto.builder()
                .activityVeryLittle(activityVeryLittle)
                .activityLittle(activityLittle)
                .activityNormal(activityNormal)
                .activityMuch(activityMuch)
                .activityVeryMuch(activityVeryMuch)
                .snackLittle(snackLittle)
                .snackNormal(snackNormal)
                .snackMuch(snackMuch)
                .price(price)
                .freeCondition(freeCondition)
                .build();


        //when & then
        mockMvc.perform(put("/api/admin/setting")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update_setting",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_setting").description("관리자 설정 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("activityVeryLittle").description("활동량 매우 적음"),
                                fieldWithPath("activityLittle").description("활동량 적음"),
                                fieldWithPath("activityNormal").description("활동량 보통"),
                                fieldWithPath("activityMuch").description("활동량 많음"),
                                fieldWithPath("activityVeryMuch").description("활동량 매우 많음"),
                                fieldWithPath("snackLittle").description("간식량 적음"),
                                fieldWithPath("snackNormal").description("간식량 보통"),
                                fieldWithPath("snackMuch").description("간식량 많음"),
                                fieldWithPath("price").description("배송비"),
                                fieldWithPath("freeCondition").description("무료배송 조건, xx원 이상 무료배송")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_setting.href").description("관리자 설정 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Setting setting = settingRepository.findAll().get(0);
        assertThat(setting.getActivityConstant().getActivityVeryMuch()).isEqualTo(activityVeryMuch);
        assertThat(setting.getActivityConstant().getActivityMuch()).isEqualTo(activityMuch);
        assertThat(setting.getActivityConstant().getActivityNormal()).isEqualTo(activityNormal);
        assertThat(setting.getActivityConstant().getActivityLittle()).isEqualTo(activityLittle);
        assertThat(setting.getActivityConstant().getActivityVeryLittle()).isEqualTo(activityVeryLittle);

        assertThat(setting.getSnackConstant().getSnackMuch()).isEqualTo(snackMuch);
        assertThat(setting.getSnackConstant().getSnackNormal()).isEqualTo(snackNormal);
        assertThat(setting.getSnackConstant().getSnackLittle()).isEqualTo(snackLittle);

        assertThat(setting.getDeliveryConstant().getPrice()).isEqualTo(price);
        assertThat(setting.getDeliveryConstant().getFreeCondition()).isEqualTo(freeCondition);


    }


    @Test
    @DisplayName("유저전체에게 친구톡 보내기")
    public void friendTalk_All() throws Exception {
       //given
        memberRepository.deleteAll();

        em.flush();
        em.clear();

        Member admin = generateMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01099038544", Gender.FEMALE, Grade.더바프, 100000, true, "ADMIN,SUBSCRIBER,USER", true);
        generateDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "댕댕이");

//        Member member = generateMember("jyh1234@gmail.com", "김회원", appProperties.getUserPassword(), "01056862723", Gender.MALE, Grade.브론즈, 50000, false, "USER,SUBSCRIBER", true);
//        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");


        FriendTalkAllDto requestDto = FriendTalkAllDto.builder()
                .templateNum(55)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/friendTalk/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_friendTalk_all",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("templateNum").description("친구톡 템플릿 번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("status").description("다이렉트센드 status"),
                                fieldWithPath("message").description("다이렉트센드 message"),
                                fieldWithPath("responseCode").description("다이렉트 센드 responseCode, 200이 아니면 다이렉트센드 내부 문제"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @DisplayName("유저전체에게 친구톡 보내기 - 템플릿 null")
    public void friendTalk_All_template_Null() throws Exception {
        //given
        memberRepository.deleteAll();

        em.flush();
        em.clear();

        Member admin = generateMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01099038544", Gender.FEMALE, Grade.더바프, 100000, true, "ADMIN,SUBSCRIBER,USER", true);
        generateDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "댕댕이");

//        Member member = generateMember("jyh1234@gmail.com", "김회원", appProperties.getUserPassword(), "01056862723", Gender.MALE, Grade.브론즈, 50000, false, "USER,SUBSCRIBER", true);
//        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");


        //when & then
        mockMvc.perform(post("/api/admin/friendTalk/all")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 그룹에게 친구톡 보내기")
    public void friendTalk_group() throws Exception {
        //given
        memberRepository.deleteAll();

        em.flush();
        em.clear();

        Member admin = generateMember(appProperties.getAdminEmail(), "관리자", appProperties.getAdminPassword(), "01099038544", Gender.FEMALE, Grade.더바프, 100000, true, "ADMIN,SUBSCRIBER,USER", true);
        generateDog(admin, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "댕댕이");

//        Member member = generateMember("jyh1234@gmail.com", "김회원", appProperties.getUserPassword(), "01056862723", Gender.MALE, Grade.브론즈, 50000, false, "USER,SUBSCRIBER", true);
//        generateDog(member, 18L, DogSize.LARGE, "14.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL, true, "대표견");

        List<Grade> gradeList = new ArrayList<>();

        gradeList.add(Grade.더바프);

        FriendTalkGroupDto requestDto = FriendTalkGroupDto.builder()
                .templateNum(55)
                .gradeList(gradeList)
                .subscribe(true)
                .birthYearFrom("1990")
                .birthYearTo("2000")
                .area(Area.NON_METRO)
                .longUnconnected(false)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/friendTalk/group")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_friendTalk_group",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("templateNum").description("친구톡 템플릿 번호"),
                                fieldWithPath("gradeList").description("등급 리스트"),
                                fieldWithPath("subscribe").description("구독 여부 true/false"),
                                fieldWithPath("birthYearFrom").description("년생 from"),
                                fieldWithPath("birthYearTo").description("년생 to"),
                                fieldWithPath("area").description("수도권 여부 [ALL, METRO, NON_METRO]"),
                                fieldWithPath("longUnconnected").description("장기 미접속 여부 true/false")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("status").description("다이렉트센드 status"),
                                fieldWithPath("message").description("다이렉트센드 message"),
                                fieldWithPath("responseCode").description("다이렉트 센드 responseCode, 200이 아니면 다이렉트센드 내부 문제"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

    }


    @Test
    @DisplayName("상담자 추가")
    public void createGuest() throws Exception {
       //given

        String name = "이름";
        SaveGuestRequest requestDto = SaveGuestRequest.builder()
                .name(name)
                .phoneNumber("01099038544")
                .email("user@gmail.com")
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_create_guest",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_guests").description("상담자 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("상담자 이름"),
                                fieldWithPath("phoneNumber").description("상담자 전화번호 '010xxxxXXXX' , 없으면 null"),
                                fieldWithPath("email").description("상담자 이메일 'xxxx@xxxx.com', 없으면 null")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_guests.href").description("상담자 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<Guest> allGuest = guestRepository.findAll();
        assertThat(allGuest.size()).isEqualTo(1);
        assertThat(allGuest.get(0).getName()).isEqualTo(name);

    }


    @Test
    @DisplayName("정상적으로 상담고객 리스트 조회")
    public void queryGuests() throws Exception {
       //given

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        admin.firstPayment();
        member.firstPayment();

        IntStream.range(1, 5).forEach(i ->{
            generateGuest("user"+i+"@gmail.com");
        });
        generateGuest("user@gmail.com");
        generateGuest("admin@gmail.com");
        IntStream.range(5, 7).forEach(i ->{
            generateGuest("user"+i+"@gmail.com");
        });

        //when & then
        mockMvc.perform(get("/api/admin/guests")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size","2")
                        .param("page","1")
                        .param("name","")
                        .param("email","gmail.com")
                        .param("phoneNumber","")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_admin_guests",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Json Web Token")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수"),
                                parameterWithName("name").description("검색할 고객 이름"),
                                parameterWithName("email").description("검색할 고객 이메일"),
                                parameterWithName("phoneNumber").description("검색할 고객 휴대전화번호 '010xxxxxxxx'")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].guestName").description("삼담시 입력한 이름"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].guestEmail").description("상담시 입력한 email , 없으면 null"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].guestPhoneNumber").description("상담시 입력한 전화번호, 없으면 null"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].createdDate").description("채널톡 상담 시작 날짜"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].memberId").description("가입한 member id , 미가입일 경우 null"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].memberName").description("가입한 member 이름, 미가입일 경우 null"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].memberEmail").description("가입한 member email, 미가입일 경우 null"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].memberPhoneNumber").description("가입한 member 전화번호 , 미가입일 경우 null"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].joinDate").description("가입 날짜 , 미가입일 경우 null"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].firstPaymentDate").description("첫 결제 날짜, 미가입or첫구매없을 경우 null"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0].paid").description("결제 여부, 미가입or첫구매없을 경우 false"),
                                fieldWithPath("_embedded.queryAdminGuestDtoList[0]._links.query_member.href").description("회원 정보 조회 링크, 미가입일 경우 null"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @DisplayName("정상적으로 상담고객 리스트 조회_member전화번호검색")
    public void queryGuests_member_phoneNumber() throws Exception {
        //given

        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        admin.firstPayment();

        IntStream.range(1, 5).forEach(i ->{
            generateGuest("user"+i+"@gmail.com");
        });
        IntStream.range(5, 7).forEach(i ->{
            generateGuest("user"+i+"@gmail.com");
        });
        generateGuest("user@gmail.com");
        generateGuest("admin@gmail.com");


        //when & then
        mockMvc.perform(get("/api/admin/guests")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size","2")
                        .param("page","0")
                        .param("name","")
                        .param("email","")
                        .param("phoneNumber","9903")
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    private void generateGuest(String email) {
        Guest guest = Guest.builder()
                .name("이름")
                .email(email)
                .build();
        guestRepository.save(guest);
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