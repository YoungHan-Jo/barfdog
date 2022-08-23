package com.bi.barfdog.api;

import com.bi.barfdog.api.cardDto.ChangeCardDto;
import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.PaymentMethod;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.*;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribePlan;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.domain.surveyReport.*;
import com.bi.barfdog.api.memberDto.jwt.JwtLoginDto;
import com.bi.barfdog.iamport.Iamport_API;
import com.bi.barfdog.repository.card.CardRepository;
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
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.UnscheduleData;
import org.junit.Before;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static com.bi.barfdog.config.finalVariable.StandardVar.*;
import static com.bi.barfdog.config.finalVariable.StandardVar.LACTATING;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class CardApiControllerTest extends BaseTest {

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
    RewardRepository rewardRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    ItemImageRepository itemImageRepository;

    @Before
    public void setUp() {

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemImageRepository.deleteAll();
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
        deliveryRepository.deleteAll();

    }

    @Test
    @DisplayName("구독 카드 리스트 조회하기")
    public void querySubscribeCards() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        IntStream.range(1,6).forEach(i -> {
            generateSubscribeOrderAndEtc(member, i, OrderStatus.DELIVERY_START);
        });

       //when & then
        mockMvc.perform(get("/api/cards")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query_subscribeCards",
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
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].subscribeCardDto.subscribeId").description("구독 id"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].subscribeCardDto.cardId").description("카드 id"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].subscribeCardDto.cardName").description("카드 이름"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].subscribeCardDto.cardNumber").description("카드 번호"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].subscribeCardDto.dogName").description("강아지 이름"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].subscribeCardDto.plan").description("플랜 [FULL,HALF,TOPPING]"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].subscribeCardDto.nextPaymentDate").description("다음 결제 날짜"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].subscribeCardDto.nextPaymentPrice").description("다음 결제 금액"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0].recipeNameList").description("구독중인 레시피 이름 리스트"),
                                fieldWithPath("_embedded.querySubscribeCardsDtoList[0]._links.change_card.href").description("카드 변경 링크"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("카드 변경")
    public void changeCard() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        SubscribeOrder order = generateSubscribeOrderAndEtc(member, 1, OrderStatus.DELIVERY_START);

        Subscribe subscribe = order.getSubscribe();

        String customerUid = "customer_Uid_l6d6evqi";
        ChangeCardDto requestDto = ChangeCardDto.builder()
                .customerUid(customerUid)
                .build();

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/cards/subscribes/{id}", subscribe.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("change_subscribeCard",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_cards").description("구독카드 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("카드 변경 시킬 구독의 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("customerUid").description("새로 발급 받은 아임포트 customerUid")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_cards.href").description("구독카드 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        Subscribe findSubscribe = subscribeRepository.findById(subscribe.getId()).get();
        Card findCard = findSubscribe.getCard();
        assertThat(findCard.getMember().getId()).isEqualTo(member.getId());
        assertThat(findCard.getCustomerUid()).isEqualTo(customerUid);

        Thread.sleep(1000);

        unschedule(customerUid, subscribe.getNextOrderMerchantUid());


    }

    private void unschedule(String customerUid, String merchantUid) {
        IamportClient client = new IamportClient(Iamport_API.API_KEY, Iamport_API.API_SECRET);

        UnscheduleData unschedule_data = new UnscheduleData(customerUid);
        unschedule_data.addMerchantUid(merchantUid);
        try {
            client.unsubscribeSchedule(unschedule_data);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private SubscribeOrder generateSubscribeOrderAndEtc(Member member, int i, OrderStatus orderStatus) {

        Recipe recipe1 = recipeRepository.findAll().get(0);
        Recipe recipe2 = recipeRepository.findAll().get(1);

        DogSaveRequestDto requestDto = DogSaveRequestDto.builder()
                .name("김바프")
                .gender(Gender.MALE)
                .birth("202102")
                .oldDog(false)
                .dogType("포메라니안")
                .dogSize(DogSize.SMALL)
                .weight("3.5")
                .neutralization(true)
                .activityLevel(ActivityLevel.NORMAL)
                .walkingCountPerWeek("10")
                .walkingTimePerOneTime("1.1")
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(SnackCountLevel.NORMAL)
                .inedibleFood("NONE")
                .inedibleFoodEtc("NONE")
                .recommendRecipeId(recipe1.getId())
                .caution("NONE")
                .build();

        String birth = requestDto.getBirth();

        DogSize dogSize = requestDto.getDogSize();
        Long startAgeMonth = getTerm(birth + "01");
        boolean oldDog = requestDto.isOldDog();
        boolean neutralization = requestDto.isNeutralization();
        DogStatus dogStatus = requestDto.getDogStatus();
        SnackCountLevel snackCountLevel = requestDto.getSnackCountLevel();
        BigDecimal weight = new BigDecimal(requestDto.getWeight());

        Delivery delivery = generateDelivery(member, i);

        Card card = generateCard(member, i);

        Subscribe subscribe = generateSubscribeBeforePayment(i, card);
        BeforeSubscribe beforeSubscribe = generateBeforeSubscribe(i);
        subscribe.setBeforeSubscribe(beforeSubscribe);

        generateSubscribeRecipe(recipe1, subscribe);
        generateSubscribeRecipe(recipe2, subscribe);

        List<Dog> dogs = dogRepository.findByMember(member);
        Recipe findRecipe = recipeRepository.findById(requestDto.getRecommendRecipeId()).get();

        Dog dog = Dog.builder()
                .member(member)
                .representative(dogs.size() == 0 ? true : false)
                .name(requestDto.getName())
                .gender(requestDto.getGender())
                .birth(birth)
                .startAgeMonth(startAgeMonth)
                .oldDog(oldDog)
                .dogType(requestDto.getDogType())
                .dogSize(dogSize)
                .weight(weight)
                .neutralization(neutralization)
                .dogActivity(getDogActivity(requestDto))
                .dogStatus(dogStatus)
                .snackCountLevel(snackCountLevel)
                .inedibleFood(requestDto.getInedibleFood())
                .inedibleFoodEtc(requestDto.getInedibleFoodEtc())
                .recommendRecipe(findRecipe)
                .caution(requestDto.getCaution())
                .subscribe(subscribe)
                .build();
        dogRepository.save(dog);
        subscribe.setDog(dog);

        SurveyReport surveyReport = SurveyReport.builder()
                .dog(dog)
                .ageAnalysis(getAgeAnalysis(startAgeMonth))
                .weightAnalysis(getWeightAnalysis(dogSize, weight))
                .activityAnalysis(getActivityAnalysis(dogSize, dog))
                .walkingAnalysis(getWalkingAnalysis(member, dog))
                .foodAnalysis(getDogAnalysis(requestDto, findRecipe, dogSize, startAgeMonth, oldDog, neutralization, dogStatus, requestDto.getActivityLevel(), snackCountLevel))
                .snackAnalysis(getSnackAnalysis(dog))
                .build();
        surveyReportRepository.save(surveyReport);
        dog.setSurveyReport(surveyReport);

        Coupon coupon = generateGeneralCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.ACTIVE);

        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .impUid("imp_uid"+i)
                .merchantUid("merchant_uid"+i)
                .orderStatus(orderStatus)
                .member(member)
                .orderPrice(120000)
                .deliveryPrice(0)
                .discountTotal(0)
                .discountReward(0)
                .discountCoupon(0)
                .paymentPrice(120000)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(memberCoupon)
                .subscribeCount(subscribe.getSubscribeCount())
                .orderConfirmDate(LocalDateTime.now().minusHours(3))
                .build();
        orderRepository.save(subscribeOrder);



        return subscribeOrder;
    }

    private Card generateCard(Member member, int i) {
        Card card = Card.builder()
                .member(member)
                .customerUid("customer_Uid_l6d6evqi")
                .cardName("카드이름" + i)
                .cardNumber("카드번호" + i)
                .build();
        return cardRepository.save(card);
    }

    private SubscribeOrder generateSubscribeOrderAndEtc_NoBeforeSubscribe_no_deliveryNumber(Member member, int i, OrderStatus orderStatus) {

        Recipe recipe1 = recipeRepository.findAll().get(0);
        Recipe recipe2 = recipeRepository.findAll().get(1);

        DogSaveRequestDto requestDto = DogSaveRequestDto.builder()
                .name("김바프")
                .gender(Gender.MALE)
                .birth("202102")
                .oldDog(false)
                .dogType("포메라니안")
                .dogSize(DogSize.SMALL)
                .weight("3.5")
                .neutralization(true)
                .activityLevel(ActivityLevel.NORMAL)
                .walkingCountPerWeek("10")
                .walkingTimePerOneTime("1.1")
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(SnackCountLevel.NORMAL)
                .inedibleFood("NONE")
                .inedibleFoodEtc("NONE")
                .recommendRecipeId(recipe1.getId())
                .caution("NONE")
                .build();

        String birth = requestDto.getBirth();

        DogSize dogSize = requestDto.getDogSize();
        Long startAgeMonth = getTerm(birth + "01");
        boolean oldDog = requestDto.isOldDog();
        boolean neutralization = requestDto.isNeutralization();
        DogStatus dogStatus = requestDto.getDogStatus();
        SnackCountLevel snackCountLevel = requestDto.getSnackCountLevel();
        BigDecimal weight = new BigDecimal(requestDto.getWeight());

        Delivery delivery = generateDeliveryNoNumber(member, i);

        Subscribe subscribe = generateSubscribeBeforePayment(i);

        generateSubscribeRecipe(recipe1, subscribe);
        generateSubscribeRecipe(recipe2, subscribe);

        List<Dog> dogs = dogRepository.findByMember(member);
        Recipe findRecipe = recipeRepository.findById(requestDto.getRecommendRecipeId()).get();

        Dog dog = Dog.builder()
                .member(member)
                .representative(dogs.size() == 0 ? true : false)
                .name(requestDto.getName())
                .gender(requestDto.getGender())
                .birth(birth)
                .startAgeMonth(startAgeMonth)
                .oldDog(oldDog)
                .dogType(requestDto.getDogType())
                .dogSize(dogSize)
                .weight(weight)
                .neutralization(neutralization)
                .dogActivity(getDogActivity(requestDto))
                .dogStatus(dogStatus)
                .snackCountLevel(snackCountLevel)
                .inedibleFood(requestDto.getInedibleFood())
                .inedibleFoodEtc(requestDto.getInedibleFoodEtc())
                .recommendRecipe(findRecipe)
                .caution(requestDto.getCaution())
                .subscribe(subscribe)
                .build();
        dogRepository.save(dog);
        subscribe.setDog(dog);

        SurveyReport surveyReport = SurveyReport.builder()
                .dog(dog)
                .ageAnalysis(getAgeAnalysis(startAgeMonth))
                .weightAnalysis(getWeightAnalysis(dogSize, weight))
                .activityAnalysis(getActivityAnalysis(dogSize, dog))
                .walkingAnalysis(getWalkingAnalysis(member, dog))
                .foodAnalysis(getDogAnalysis(requestDto, findRecipe, dogSize, startAgeMonth, oldDog, neutralization, dogStatus, requestDto.getActivityLevel(), snackCountLevel))
                .snackAnalysis(getSnackAnalysis(dog))
                .build();
        surveyReportRepository.save(surveyReport);
        dog.setSurveyReport(surveyReport);

        Coupon coupon = generateGeneralCoupon(1);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, 1, CouponStatus.ACTIVE);

        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .impUid("imp_uid"+i)
                .merchantUid("merchant_uid"+i)
                .orderStatus(orderStatus)
                .member(member)
                .orderPrice(120000)
                .deliveryPrice(0)
                .discountTotal(0)
                .discountReward(0)
                .discountCoupon(0)
                .paymentPrice(120000)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .subscribe(subscribe)
                .memberCoupon(memberCoupon)
                .subscribeCount(subscribe.getSubscribeCount())
                .orderConfirmDate(LocalDateTime.now().minusHours(3))
                .build();
        orderRepository.save(subscribeOrder);

        return subscribeOrder;
    }

    private BeforeSubscribe generateBeforeSubscribe(int i) {
        BeforeSubscribe beforeSubscribe = BeforeSubscribe.builder()
                .subscribeCount(i)
                .plan(SubscribePlan.HALF)
                .oneMealRecommendGram(BigDecimal.valueOf(140.0))
                .recipeName("덕램")
                .build();
        return beforeSubscribeRepository.save(beforeSubscribe);
    }



    private Subscribe generateSubscribeBeforePayment(int i) {
        Subscribe subscribe = Subscribe.builder()
                .subscribeCount(i+1)
                .plan(SubscribePlan.FULL)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextOrderMerchantUid("merchant_alisdjklf"+i)
                .nextPaymentPrice(1200)
                .status(SubscribeStatus.SUBSCRIBING)
                .build();
        subscribeRepository.save(subscribe);
        return subscribe;
    }

    private Subscribe generateSubscribeBeforePayment(int i, Card card) {
        Subscribe subscribe = Subscribe.builder()
                .subscribeCount(i + 1)
                .plan(SubscribePlan.FULL)
                .card(card)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextOrderMerchantUid("merchant_uid" + BarfUtils.generateRandomCode())
                .nextPaymentPrice(1200)
                .status(SubscribeStatus.SUBSCRIBING)
                .build();
        subscribeRepository.save(subscribe);
        return subscribe;
    }


    private SurveyReport generateSurveyReport(Member member) {

        Recipe recipe = recipeRepository.findAll().get(0);

        DogSaveRequestDto requestDto = DogSaveRequestDto.builder()
                .name("김바프")
                .gender(Gender.MALE)
                .birth("202102")
                .oldDog(false)
                .dogType("포메라니안")
                .dogSize(DogSize.SMALL)
                .weight("3.5")
                .neutralization(true)
                .activityLevel(ActivityLevel.NORMAL)
                .walkingCountPerWeek("10")
                .walkingTimePerOneTime("1.1")
                .dogStatus(DogStatus.HEALTHY)
                .snackCountLevel(SnackCountLevel.NORMAL)
                .inedibleFood("NONE")
                .inedibleFoodEtc("NONE")
                .recommendRecipeId(recipe.getId())
                .caution("NONE")
                .build();

        List<Dog> dogs = dogRepository.findByMember(member);
        Recipe findRecipe = recipeRepository.findById(requestDto.getRecommendRecipeId()).get();

        String birth = requestDto.getBirth();

        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.BEFORE_PAYMENT)
                .build();
        subscribeRepository.save(subscribe);

        DogSize dogSize = requestDto.getDogSize();
        Long startAgeMonth = getTerm(birth + "01");
        boolean oldDog = requestDto.isOldDog();
        boolean neutralization = requestDto.isNeutralization();
        DogStatus dogStatus = requestDto.getDogStatus();
        SnackCountLevel snackCountLevel = requestDto.getSnackCountLevel();
        BigDecimal weight = new BigDecimal(requestDto.getWeight());

        Dog dog = Dog.builder()
                .member(member)
                .representative(dogs.size() == 0 ? true : false)
                .name(requestDto.getName())
                .gender(requestDto.getGender())
                .birth(birth)
                .startAgeMonth(startAgeMonth)
                .oldDog(oldDog)
                .dogType(requestDto.getDogType())
                .dogSize(dogSize)
                .weight(weight)
                .neutralization(neutralization)
                .dogActivity(getDogActivity(requestDto))
                .dogStatus(dogStatus)
                .snackCountLevel(snackCountLevel)
                .inedibleFood(requestDto.getInedibleFood())
                .inedibleFoodEtc(requestDto.getInedibleFoodEtc())
                .recommendRecipe(findRecipe)
                .caution(requestDto.getCaution())
                .subscribe(subscribe)
                .build();
        dogRepository.save(dog);

        SurveyReport surveyReport = SurveyReport.builder()
                .dog(dog)
                .ageAnalysis(getAgeAnalysis(startAgeMonth))
                .weightAnalysis(getWeightAnalysis(dogSize, weight))
                .activityAnalysis(getActivityAnalysis(dogSize, dog))
                .walkingAnalysis(getWalkingAnalysis(member, dog))
                .foodAnalysis(getDogAnalysis(requestDto, findRecipe, dogSize, startAgeMonth, oldDog, neutralization, dogStatus, requestDto.getActivityLevel(), snackCountLevel))
                .snackAnalysis(getSnackAnalysis(dog))
                .build();
        surveyReportRepository.save(surveyReport);
        return surveyReport;
    }




    private GeneralOrder generateGeneralOrder(Member member, int i, OrderStatus orderstatus) {

        Delivery delivery = generateDelivery(member, i);
        GeneralOrder generalOrder = GeneralOrder.builder()
                .impUid("imp_uid" + i)
                .merchantUid("merchant_uid" + i)
                .orderStatus(orderstatus)
                .member(member)
                .orderPrice(120000)
                .deliveryPrice(0)
                .discountTotal(10000)
                .discountReward(10000)
                .discountCoupon(0)
                .paymentPrice(110000)
                .paymentDate(LocalDateTime.now().minusDays(2))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .orderConfirmDate(LocalDateTime.now().minusHours(3))
                .build();

        IntStream.range(1,3).forEach(j -> {
            Item item = generateItem(j);
            generateOption(item, j);

            IntStream.range(1, 4).forEach(k ->{
                ItemImage itemImage = ItemImage.builder()
                        .item(item)
                        .leakOrder(k)
                        .folder("folder" + j)
                        .filename("filename" + k + j)
                        .build();
                itemImageRepository.save(itemImage);
            });

            OrderItem orderItem = generateOrderItem(member, generalOrder, j, item, orderstatus);

            IntStream.range(1,j+1).forEach(k -> {
                SelectOption selectOption = SelectOption.builder()
                        .orderItem(orderItem)
                        .name("옵션" + k)
                        .price(1000 * k)
                        .amount(k)
                        .build();
                selectOptionRepository.save(selectOption);
            });

            generalOrder.addOrderItemList(orderItem);

        });


        return orderRepository.save(generalOrder);
    }

    private OrderItem generateOrderItem(Member member, GeneralOrder generalOrder, int j, Item item, OrderStatus orderStatus) {

        Coupon coupon = generateGeneralCoupon(j);
        MemberCoupon memberCoupon = generateMemberCoupon(member, coupon, j, CouponStatus.ACTIVE);
        OrderItem orderItem = OrderItem.builder()
                .generalOrder(generalOrder)
                .item(item)
                .salePrice(item.getSalePrice())
                .amount(j)
                .memberCoupon(memberCoupon)
                .finalPrice(item.getSalePrice() * j)
                .status(orderStatus)
                .isSavedReward(false)
                .saveReward(500*j)
                .orderCancel(OrderCancel.builder()
                        .cancelConfirmDate(LocalDateTime.now().minusDays(1))
                        .cancelRequestDate(LocalDateTime.now().minusDays(3))
                        .cancelDetailReason("상세이유")
                        .cancelReason("이유")
                        .build())
                .orderReturn(OrderReturn.builder()
                        .returnConfirmDate(LocalDateTime.now().minusDays(1))
                        .returnRequestDate(LocalDateTime.now().minusDays(3))
                        .returnDetailReason("상세이유")
                        .returnReason("이유")
                        .build())
                .orderExchange(OrderExchange.builder()
                        .exchangeConfirmDate(LocalDateTime.now().minusDays(1))
                        .exchangeRequestDate(LocalDateTime.now().minusDays(3))
                        .exchangeDetailReason("상세이유")
                        .exchangeReason("이유")
                        .build())
                .build();
        return orderItemRepository.save(orderItem);
    }



    private Item generateItem(int i) {
        Item item = Item.builder()
                .itemType(ItemType.GOODS)
                .name("굿즈 상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .itemIcons("NEW,BEST")
                .totalSalesAmount(i)
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
    }

    private ItemOption generateOption(Item item, int i) {
        ItemOption itemOption = ItemOption.builder()
                .item(item)
                .name("옵션" + i)
                .optionPrice(i * 1000)
                .remaining(999)
                .build();
        return itemOptionRepository.save(itemOption);
    }

    private SubscribeOrder generateSubscribeOrder(Member member, int i, OrderStatus orderStatus) {
        Delivery delivery = generateDelivery(member, i);
        Dog dog = generateDog(member, i, 20L, DogSize.LARGE, "15.2", ActivityLevel.LITTLE, 1, 1, SnackCountLevel.NORMAL);

        Subscribe subscribe = Subscribe.builder()
                .dog(dog)
                .subscribeCount(i)
                .plan(SubscribePlan.FULL)
                .nextPaymentDate(LocalDateTime.now().plusDays(6))
                .nextDeliveryDate(LocalDate.now().plusDays(8))
                .nextPaymentPrice(120000)
                .status(SubscribeStatus.SUBSCRIBING)
                .build();
        subscribeRepository.save(subscribe);

        SubscribeOrder subscribeOrder = SubscribeOrder.builder()
                .impUid("imp_uid"+i)
                .merchantUid("merchant_uid"+i)
                .orderStatus(orderStatus)
                .member(member)
                .orderPrice(120000)
                .deliveryPrice(0)
                .discountTotal(0)
                .discountReward(0)
                .discountCoupon(0)
                .paymentPrice(120000)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .isPackage(false)
                .delivery(delivery)
                .subscribe(subscribe)
                .build();
        return orderRepository.save(subscribeOrder);
    }

    private Delivery generateDelivery(Member member, int i) {
        Delivery delivery = Delivery.builder()
                .deliveryNumber("cj023923423" + i)
                .recipient(Recipient.builder()
                        .name(member.getName())
                        .phone(member.getPhoneNumber())
                        .zipcode(member.getAddress().getZipcode())
                        .street(member.getAddress().getStreet())
                        .detailAddress(member.getAddress().getDetailAddress())
                        .build())
                .departureDate(LocalDateTime.now().minusDays(4))
                .arrivalDate(LocalDateTime.now().minusDays(1))
                .status(DeliveryStatus.DELIVERY_START)
                .nextDeliveryDate(LocalDate.now().plusDays(2))
                .request("안전배송 부탁드립니다.")
                .build();
        deliveryRepository.save(delivery);
        return delivery;
    }

    private Delivery generateDeliveryNoNumber(Member member, int i) {
        Delivery delivery = Delivery.builder()
                .recipient(Recipient.builder()
                        .name(member.getName())
                        .phone(member.getPhoneNumber())
                        .zipcode(member.getAddress().getZipcode())
                        .street(member.getAddress().getStreet())
                        .detailAddress(member.getAddress().getDetailAddress())
                        .build())
                .departureDate(LocalDateTime.now().minusDays(4))
                .arrivalDate(LocalDateTime.now().minusDays(1))
                .status(DeliveryStatus.PAYMENT_DONE)
                .request("안전배송 부탁드립니다.")
                .build();
        deliveryRepository.save(delivery);
        return delivery;
    }

    private Dog generateDog(Member member, int i, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
        Dog dog = Dog.builder()
                .member(member)
                .name("강아지" + i)
                .birth("202103")
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




    private SnackAnalysis getSnackAnalysis(Dog dog) {
        double avgSnackCountInLargeDog = getAvgSnackByDogSize(DogSize.LARGE);
        double avgSnackCountInMiddleDog = getAvgSnackByDogSize(DogSize.MIDDLE);
        double avgSnackCountInSmallDog = getAvgSnackByDogSize(DogSize.SMALL);

        int mySnackCount = getMySnackCount(dog);

        SnackAnalysis snackAnalysis = SnackAnalysis.builder()
                .avgSnackCountInLargeDog(avgSnackCountInLargeDog)
                .avgSnackCountInMiddleDog(avgSnackCountInMiddleDog)
                .avgSnackCountInSmallDog(avgSnackCountInSmallDog)
                .mySnackCount(mySnackCount)
                .build();
        return snackAnalysis;
    }

    private int getMySnackCount(Dog dog) {
        int mySnackCount;

        switch (dog.getSnackCountLevel()) {
            case LITTLE: mySnackCount = 1;
                break;
            case NORMAL: mySnackCount = 2;
                break;
            default: mySnackCount = 3;
                break;
        }

        return mySnackCount;
    }

    private double getAvgSnackByDogSize(DogSize dogSize) {
        List<String> snackGroupByDogSize = dogRepository.findSnackGroupByDogSize(dogSize);

        double sum = 0.0;

        for (String s : snackGroupByDogSize) {
            double d = Double.valueOf(s);
            sum += d;
        }
        return Math.round(sum / snackGroupByDogSize.size() * 10.0) / 10.0;
    }

    private WalkingAnalysis getWalkingAnalysis(Member member, Dog dog) {
        List<Long> ranks = dogRepository.findRanksById(dog.getId());
        int rank = 1;
        for (Long id : ranks) {
            if (id == dog.getId()) {
                break;
            }
            rank++;
        }

        double highRankPercent = Math.round((double) rank / ranks.size() * 1000.0) / 10.0;

        double totalWalkingTime = dog.getDogActivity().getWalkingTimePerOneTime() * dog.getDogActivity().getWalkingCountPerWeek();
        double avgWalkingTimeInCity = dogRepository.findAvgTotalWalkingTimeByCity(member.getAddress().getCity());
        double avgTotalWalkingTimeByAge = dogRepository.findAvgTotalWalkingTimeByAge(Math.floor(dog.getStartAgeMonth() / 12));
        double avgWalkingTimeInDogSize = dogRepository.findAvgTotalWalkingTimeByDogSize(dog.getDogSize());

        WalkingAnalysis walkingAnalysis = WalkingAnalysis.builder()
                .highRankPercent(highRankPercent)
                .walkingCountPerWeek(dog.getDogActivity().getWalkingCountPerWeek())
                .totalWalingTime(Math.round(totalWalkingTime * 10.0) / 10.0)
                .avgWalkingTimeInCity(Math.round(avgWalkingTimeInCity * 10.0) / 10.0)
                .avgWalkingTimeInAge(Math.round(avgTotalWalkingTimeByAge * 10.0) / 10.0)
                .avgWalkingTimeInDogSize(Math.round(avgWalkingTimeInDogSize * 10.0) / 10.0)
                .build();
        return walkingAnalysis;
    }

    private ActivityAnalysis getActivityAnalysis(DogSize dogSize, Dog dog) {
        List<String> activityGroup = dogRepository.findActivityGroupByDogSize(dogSize);

        int activityGroupOneCount = 0;
        int activityGroupTwoCount = 0;
        int activityGroupThreeCount = 0;
        int activityGroupFourCount = 0;
        int activityGroupFiveCount = 0;

        double sum = 0.0;

        for (String s : activityGroup) {
            switch (s) {
                case "1": activityGroupOneCount++;
                    break;
                case "2": activityGroupTwoCount++;
                    break;
                case "3": activityGroupThreeCount++;
                    break;
                case "4": activityGroupFourCount++;
                    break;
                case "5": activityGroupFiveCount++;
                    break;
                default:
                    break;
            }
            sum += Double.valueOf(s);
        }

        ActivityLevel avgActivityLevel = getAvgActivityLevel(activityGroup, sum);

        int myActivityGroup = getMyActivityGroup(dog);

        ActivityAnalysis activityAnalysis = ActivityAnalysis.builder()
                .avgActivityLevel(avgActivityLevel)
                .activityGroupOneCount(activityGroupOneCount)
                .activityGroupTwoCount(activityGroupTwoCount)
                .activityGroupThreeCount(activityGroupThreeCount)
                .activityGroupFourCount(activityGroupFourCount)
                .activityGroupFiveCount(activityGroupFiveCount)
                .myActivityGroup(myActivityGroup)
                .build();
        return activityAnalysis;
    }

    private ActivityLevel getAvgActivityLevel(List<String> activityGroup, double sum) {
        ActivityLevel avgActivityLevel;

        int round = (int) Math.round(sum / activityGroup.size());
        switch (round) {
            case 1:
                avgActivityLevel = ActivityLevel.VERY_LITTLE;
                break;
            case 2:
                avgActivityLevel = ActivityLevel.LITTLE;
                break;
            case 3:
                avgActivityLevel = ActivityLevel.NORMAL;
                break;
            case 4:
                avgActivityLevel = ActivityLevel.MUCH;
                break;
            default:
                avgActivityLevel = ActivityLevel.VERY_MUCH;
                break;
        }
        return avgActivityLevel;
    }

    private int getMyActivityGroup(Dog dog) {
        int myActivityGroup;

        switch (dog.getDogActivity().getActivityLevel()) {
            case VERY_LITTLE: myActivityGroup = 1;
                break;
            case LITTLE: myActivityGroup = 2;
                break;
            case NORMAL: myActivityGroup = 3;
                break;
            case MUCH: myActivityGroup = 4;
                break;
            default: myActivityGroup = 5;
                break;
        }
        return myActivityGroup;
    }

    private WeightAnalysis getWeightAnalysis(DogSize dogSize, BigDecimal weight) {
        double avgWeightByDogSize = dogRepository.findAvgWeightByDogSize(dogSize);

        double avgWeight = Math.round(avgWeightByDogSize * 10.0) / 10.0;

        double fattestWeightByDogSize = dogRepository.findFattestWeightByDogSize(dogSize);
        double lightestWeight = dogRepository.findLightestWeightByDogSize(dogSize);

        double weightRange = Math.round(((fattestWeightByDogSize-lightestWeight)/5.0) * 10.0) / 10.0;

        List<String> weightGroup = dogRepository.findWeightGroupByDogSize(dogSize, lightestWeight, weightRange);

        int weightGroupOneCount = 0;
        int weightGroupTwoCount = 0;
        int weightGroupThreeCount = 0;
        int weightGroupFourCount = 0;
        int weightGroupFiveCount = 0;

        for (String s : weightGroup) {
            switch (s) {
                case "1": weightGroupOneCount++;
                    break;
                case "2": weightGroupTwoCount++;
                    break;
                case "3": weightGroupThreeCount++;
                    break;
                case "4": weightGroupFourCount++;
                    break;
                case "5": weightGroupFiveCount++;
                    break;
                default:
                    break;
            }
        }

        int myWeightGroup = getMyWeightGroup(weight, lightestWeight, weightRange);

        WeightAnalysis weightAnalysis = WeightAnalysis.builder()
                .avgWeight(avgWeight)
                .weightGroupOneCount(weightGroupOneCount)
                .weightGroupTwoCount(weightGroupTwoCount)
                .weightGroupThreeCount(weightGroupThreeCount)
                .weightGroupFourCount(weightGroupFourCount)
                .weightGroupFiveCount(weightGroupFiveCount)
                .myWeightGroup(myWeightGroup)
                .weightInLastReport(weight)
                .build();

        return weightAnalysis;
    }

    private int getMyWeightGroup(BigDecimal weight, double lightestWeight, double weightRange) {
        int myWeightGroup;

        if (includedInRange(weight, lightestWeight + weightRange)) {
            myWeightGroup = 1;
        } else if(includedInRange(weight, lightestWeight + weightRange * 2.0)) {
            myWeightGroup = 2;
        } else if(includedInRange(weight, lightestWeight + weightRange * 3.0)) {
            myWeightGroup = 3;
        } else if (includedInRange(weight, lightestWeight + weightRange * 4.0)) {
            myWeightGroup = 4;
        } else {
            myWeightGroup = 5;
        }
        return myWeightGroup;
    }

    private boolean includedInRange(BigDecimal weight, double weightRange) {
        int compare = weight.compareTo(BigDecimal.valueOf(weightRange));

        if (compare <= 0) {
            return true;
        }
        return false;
    }

    private AgeAnalysis getAgeAnalysis(Long startAgeMonth) {
        double avgAgeMonth = dogRepository.findAvgStartAgeMonth();

        int oldestMonth = dogRepository.findOldestMonth();

        long monthRange = Math.round(oldestMonth / 5.0);

        int avgMonth = (int) Math.round(avgAgeMonth);

        List<String> ageGroup = dogRepository.findAgeGroup(monthRange);

        int ageGroupOneCount = 0;
        int ageGroupTwoCount = 0;
        int ageGroupThreeCount = 0;
        int ageGroupFourCount = 0;
        int ageGroupFiveCount = 0;

        for (String s : ageGroup) {
            switch (s) {
                case "1": ageGroupOneCount++;
                    break;
                case "2": ageGroupTwoCount++;
                    break;
                case "3": ageGroupThreeCount++;
                    break;
                case "4": ageGroupFourCount++;
                    break;
                case "5": ageGroupFiveCount++;
                    break;
                default:
                    break;
            }
        }

        int myAgeGroup = getMyAgeGroup(startAgeMonth, monthRange);

        AgeAnalysis ageAnalysis = AgeAnalysis.builder()
                .avgAgeMonth(avgMonth)
                .ageGroupOneCount(ageGroupOneCount)
                .ageGroupTwoCount(ageGroupTwoCount)
                .ageGroupThreeCount(ageGroupThreeCount)
                .ageGroupFourCount(ageGroupFourCount)
                .ageGroupFiveCount(ageGroupFiveCount)
                .myAgeGroup(myAgeGroup)
                .myStartAgeMonth(startAgeMonth)
                .build();
        return ageAnalysis;
    }

    private int getMyAgeGroup(Long startAgeMonth, long monthRange) {
        int myAgeGroup;

        if (startAgeMonth < monthRange) {
            myAgeGroup = 1;
        } else if (startAgeMonth < monthRange * 2) {
            myAgeGroup = 2;
        } else if (startAgeMonth < monthRange * 3) {
            myAgeGroup = 3;
        } else if (startAgeMonth < monthRange * 4) {
            myAgeGroup = 4;
        } else {
            myAgeGroup = 5;
        }
        return myAgeGroup;
    }

    private DogActivity getDogActivity(DogSaveRequestDto requestDto) {
        return new DogActivity(requestDto.getActivityLevel(), Integer.valueOf(requestDto.getWalkingCountPerWeek()), Double.valueOf(requestDto.getWalkingTimePerOneTime()));
    }

    private FoodAnalysis getDogAnalysis(DogSaveRequestDto requestDto, Recipe recipe, DogSize dogSize, Long startAge, boolean oldDog, boolean neutralization, DogStatus dogStatus, ActivityLevel activityLevel, SnackCountLevel snackCountLevel) {
        BigDecimal rootVar = BigDecimal.valueOf(70.0);
        BigDecimal standardVar = getStandardVar(dogSize, startAge, oldDog, neutralization, dogStatus);

        BigDecimal rootXWeightX075 = rootVar.multiply(BigDecimal.valueOf(Math.pow(new Double(requestDto.getWeight()), 0.75)));

        Setting setting = settingRepository.findAll().get(0);
        ActivityConstant activityConstant = setting.getActivityConstant();
        SnackConstant snackConstant = setting.getSnackConstant();

        BigDecimal activityVar = getActivityVar(activityLevel, activityConstant);

        BigDecimal snackVar = getSnackVar(snackCountLevel, snackConstant);

        BigDecimal recommendKcal = rootXWeightX075.multiply(standardVar.multiply(activityVar.multiply(snackVar))).divide(BigDecimal.valueOf(10000.0))
                .setScale(4, BigDecimal.ROUND_HALF_UP);

        BigDecimal gramPerKcal = recipe.getGramPerKcal();

        BigDecimal oneDayRecommendGram = gramPerKcal.multiply(recommendKcal).setScale(0,BigDecimal.ROUND_HALF_UP);

        BigDecimal oneMealRecommendGram = oneDayRecommendGram.divide(BigDecimal.valueOf(2)).setScale(0,BigDecimal.ROUND_HALF_UP);

        FoodAnalysis foodAnalysis = new FoodAnalysis(recommendKcal, oneDayRecommendGram, oneMealRecommendGram);
        return foodAnalysis;
    }

    private BigDecimal getSnackVar(SnackCountLevel snackCountLevel, SnackConstant snackConstant) {
        switch (snackCountLevel) {
            case LITTLE: return BigDecimal.valueOf(100.0).add(snackConstant.getSnackLittle());
            case NORMAL: return BigDecimal.valueOf(100.0);
            case MUCH: return BigDecimal.valueOf(100.0).subtract(snackConstant.getSnackMuch());
            default: return BigDecimal.valueOf(0);
        }
    }



    private BigDecimal getActivityVar(ActivityLevel activityLevel, ActivityConstant activityConstant) {
        switch (activityLevel) {
            case VERY_LITTLE: return BigDecimal.valueOf(100.0).subtract(activityConstant.getActivityVeryLittle());
            case LITTLE: return BigDecimal.valueOf(100.0).subtract(activityConstant.getActivityLittle());
            case NORMAL: return BigDecimal.valueOf(100.0);
            case MUCH: return BigDecimal.valueOf(100.0).add(activityConstant.getActivityMuch());
            case VERY_MUCH: return BigDecimal.valueOf(100.0).add(activityConstant.getActivityVeryMuch());
            default: return BigDecimal.valueOf(0);
        }
    }

    private BigDecimal getStandardVar(DogSize dogSize, Long age, boolean oldDog, boolean neutralization, DogStatus dogStatus) {
        BigDecimal var;

        if (oldDog == false) {
            if (dogSize == DogSize.LARGE) { // 대형견
                if (age <= 18L) { // 어린 개
                    var = BigDecimal.valueOf(YOUNG_DOG);
                } else{
                    var = switchDogStatus(neutralization, dogStatus);
                }
            } else { // 소,중형견
                if (age <= 12L) { // 어린 개
                    var = BigDecimal.valueOf(YOUNG_DOG);
                } else{
                    var = switchDogStatus(neutralization, dogStatus);
                }
            }
        } else { // 노견
            var = BigDecimal.valueOf(OLD_DOG);
        }

        return var;
    }

    private BigDecimal switchDogStatus(boolean neutralization, DogStatus dogStatus) {
        switch (dogStatus) {
            case HEALTHY: return neutralization ? BigDecimal.valueOf(NEUTRALIZATION_TRUE) : BigDecimal.valueOf(NEUTRALIZATION_FALSE);
            case NEED_DIET: return BigDecimal.valueOf(NEED_DIET);
            case OBESITY: return BigDecimal.valueOf(OBESITY);
            case PREGNANT: return BigDecimal.valueOf(PREGNANT);
            case LACTATING: return BigDecimal.valueOf(LACTATING);
            default: return BigDecimal.valueOf(0);
        }
    }


    public Long getTerm(String birthday) {
        Long month = 0L;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate startDate = LocalDate.parse(birthday, formatter);
            LocalDate endDate = LocalDate.now();
            month = ChronoUnit.MONTHS.between(startDate, endDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return month;
    }










    //==============================================

    private LocalDate getNextDeliveryDate() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();
        int i = dayOfWeekNumber - 3;
        LocalDate nextDeliveryDate = null;
        if (dayOfWeekNumber <= 5) {
            nextDeliveryDate = today.plusDays(i+7);
        } else {
            nextDeliveryDate = today.plusDays(i+14);
        }
        return nextDeliveryDate;
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


    private Subscribe generateSubscribeBeforePayment(Dog dog, SubscribePlan plan, SubscribeStatus status, int nextPaymentPrice) {
        List<Recipe> recipes = recipeRepository.findAll();

        Subscribe subscribe = Subscribe.builder()
                .status(status)
                .plan(plan)
                .subscribeCount(0)
                .nextPaymentPrice(nextPaymentPrice)
                .build();

        SubscribeRecipe subscribeRecipe1 = generateSubscribeRecipe(recipes.get(0), subscribe);
        SubscribeRecipe subscribeRecipe2 = generateSubscribeRecipe(recipes.get(1), subscribe);
        subscribe.addSubscribeRecipe(subscribeRecipe1);
        subscribe.addSubscribeRecipe(subscribeRecipe2);

        dog.setSubscribe(subscribe);

        return subscribeRepository.save(subscribe);
    }

    private SubscribeRecipe generateSubscribeRecipe(Recipe recipe, Subscribe subscribe) {
        SubscribeRecipe subscribeRecipe = SubscribeRecipe.builder()
                .recipe(recipe)
                .subscribe(subscribe)
                .build();

        return subscribeRecipeRepository.save(subscribeRecipe);
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

    private Dog generateDogRepresentativeBeforePaymentSubscribe(Member admin, long startAgeMonth, DogSize dogSize, String weight, ActivityLevel activitylevel, int walkingCountPerWeek, double walkingTimePerOneTime, SnackCountLevel snackCountLevel) {
        Dog dog = Dog.builder()
                .member(admin)
                .name("대표견")
                .birth("202103")
                .representative(true)
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