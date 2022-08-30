package com.bi.barfdog.service;

import com.bi.barfdog.api.orderDto.StopSubscribeDto;
import com.bi.barfdog.api.subscribeDto.UpdateGramDto;
import com.bi.barfdog.api.subscribeDto.UpdatePlanDto;
import com.bi.barfdog.api.subscribeDto.UseCouponDto;
import com.bi.barfdog.common.RandomString;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.iamport.Iamport_API;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.subscribe.BeforeSubscribeRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.request.UnscheduleData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final RecipeRepository recipeRepository;
    private final SubscribeRecipeRepository subscribeRecipeRepository;
    private final BeforeSubscribeRepository beforeSubscribeRepository;
    private final OrderRepository orderRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final DeliveryRepository deliveryRepository;

    private IamportClient client = new IamportClient(Iamport_API.API_KEY, Iamport_API.API_SECRET);


    @Transactional
    public void changeCoupon(Long id, UseCouponDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();

        Long memberCouponId = requestDto.getMemberCouponId();
        MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId).get();
        int discountCoupon = requestDto.getDiscount();
        subscribe.changeCoupon(memberCoupon, discountCoupon);

        unscheduleAndNewSchedule(subscribe);

    }

    @Transactional
    public void updateGram(Long id, UpdateGramDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();
        changeBeforeSubscribe(subscribe);

        subscribe.updateGram(requestDto);

        unscheduleAndNewSchedule(subscribe);
    }

    private void changeBeforeSubscribe(Subscribe subscribe) {
        BigDecimal oneMealRecommendGram = subscribe.getDog().getSurveyReport().getFoodAnalysis().getOneMealRecommendGram();

        beforeSubscribeRepository.deleteAllBySubscribe(subscribe);

        BeforeSubscribe beforeSubscribe = BeforeSubscribe.builder()
                .subscribe(subscribe)
                .subscribeCount(subscribe.getSubscribeCount())
                .plan(subscribe.getPlan())
                .oneMealRecommendGram(oneMealRecommendGram)
                .recipeName(getRecipeName(subscribe))
                .paymentPrice(subscribe.getNextPaymentPrice())
                .build();
        beforeSubscribeRepository.save(beforeSubscribe);

    }

    @Transactional
    public void changePlan(Long id, UpdatePlanDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();
        changeBeforeSubscribe(subscribe);

        updatePlanAndChangeSelectRecipes(subscribe, requestDto);

        unscheduleAndNewSchedule(subscribe);

    }

    @Transactional
    public void skipSubscribe(Long id, String type) {
        Subscribe subscribe = subscribeRepository.findById(id).get();
        subscribe.skipSubscribe(type);

        unscheduleAndNewSchedule(subscribe);

        LocalDate nextDeliveryDate = subscribe.getNextDeliveryDate();
        String nextOrderMerchant_uid = subscribe.getNextOrderMerchantUid();
        Optional<SubscribeOrder> optionalSubscriberOrder = orderRepository.findByMerchantUid(nextOrderMerchant_uid);
        if (optionalSubscriberOrder.isPresent()) {
            SubscribeOrder order = optionalSubscriberOrder.get();
            order.skipDelivery(nextDeliveryDate);
        }
    }

    @Transactional
    public void stopSubscribe(Long id, StopSubscribeDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();
        unschedule(subscribe);
        Optional<SubscribeOrder> optionalSubscribeOrder = orderRepository.findByMerchantUid(subscribe.getNextOrderMerchantUid());
        if (optionalSubscribeOrder.isPresent()) {
            SubscribeOrder order = optionalSubscribeOrder.get();
            Delivery delivery = order.getDelivery();
            orderRepository.delete(order);
            deliveryRepository.delete(delivery);

            subscribe.stopSubscribe(requestDto.getReasonList());
        }
    }

    private void unschedule(Subscribe subscribe) {
        String customerUid = subscribe.getCard().getCustomerUid();
        String merchant_uid = subscribe.getNextOrderMerchantUid();

        UnscheduleData unscheduleData = new UnscheduleData(customerUid);
        unscheduleData.addMerchantUid(merchant_uid);

        try {
            client.unsubscribeSchedule(unscheduleData);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void unscheduleAndNewSchedule(Subscribe subscribe) {
        String customerUid = subscribe.getCard().getCustomerUid();
        String merchant_uid = subscribe.getNextOrderMerchantUid();

        UnscheduleData unscheduleData = new UnscheduleData(customerUid);
        unscheduleData.addMerchantUid(merchant_uid);

        try {
            client.unsubscribeSchedule(unscheduleData);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sleepThread(500);

        SubscribeOrder subscribeOrder = orderRepository.findByMerchantUid(merchant_uid).get();

        String newMerchantUid = generateMerchantUid();

        subscribeOrder.changeMerchantUid(newMerchantUid);
        subscribe.changeNextMerchantUid(newMerchantUid);

        Date nextPaymentDate = java.sql.Timestamp.valueOf(subscribe.getNextPaymentDate());
        int nextPaymentPrice = subscribe.getNextPaymentPrice();
        int discountCoupon = subscribe.getDiscountCoupon();
        int discountGrade = subscribe.getDiscountGrade();
        int finalPrice = nextPaymentPrice - (discountCoupon + discountGrade);
        ScheduleData scheduleData = new ScheduleData(customerUid);
        scheduleData.addSchedule(new ScheduleEntry(newMerchantUid, nextPaymentDate, BigDecimal.valueOf(finalPrice)));

        try {
            client.subscribeSchedule(scheduleData);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateMerchantUid() {
        RandomString rs = new RandomString(15);
        String randomString = rs.nextString();
        LocalDate today = LocalDate.now();
        String dateString = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String merchantUid = dateString + "_" + randomString;
        return merchantUid;
    }

    private void sleepThread(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void saveSubscribeRecipe(Subscribe subscribe, Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).get();
        SubscribeRecipe subscribeRecipe = SubscribeRecipe.builder()
                .subscribe(subscribe)
                .recipe(recipe)
                .build();
        subscribeRecipeRepository.save(subscribeRecipe);
    }

    private BeforeSubscribe saveNewBeforeSubscribe(Subscribe subscribe, String recipeName) {
        BeforeSubscribe newBeforeSubscribe = BeforeSubscribe.builder()
                .subscribeCount(subscribe.getSubscribeCount())
                .plan(subscribe.getPlan())
                .oneMealRecommendGram(subscribe.getDog().getSurveyReport().getFoodAnalysis().getOneMealRecommendGram())
                .recipeName(recipeName)
                .paymentPrice(subscribe.getNextPaymentPrice())
                .build();
        beforeSubscribeRepository.save(newBeforeSubscribe);
        return newBeforeSubscribe;
    }

    private String getRecipeName(Subscribe subscribe) {
        List<SubscribeRecipe> subscribeRecipes = subscribeRecipeRepository.findBySubscribe(subscribe);
        String recipeName = "";
        if (subscribeRecipes.size() > 0) {
            recipeName = subscribeRecipes.get(0).getRecipe().getName();
            if (subscribeRecipes.size() > 1) {
                recipeName += "," +subscribeRecipes.get(1).getRecipe().getName();
            }
        }
        return recipeName;
    }


    public void paymentAlimScheduler() {

        List<Subscribe> subscribeList = subscribeRepository.findTomorrowPayment();

        for (Subscribe subscribe : subscribeList) {
            try {
                SubscribeOrder order = orderRepository.findByMerchantUid(subscribe.getNextOrderMerchantUid()).get();
                DirectSendUtils.sendTomorrowPaymentAlim(subscribe, order);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Transactional
    public void selectPlanFirstTime(Long id, UpdatePlanDto requestDto) {

        Subscribe subscribe = subscribeRepository.findById(id).get();

        updatePlanAndChangeSelectRecipes(subscribe, requestDto);
    }

    private void updatePlanAndChangeSelectRecipes(Subscribe subscribe, UpdatePlanDto requestDto) {
        subscribe.updatePlan(requestDto);

        subscribeRecipeRepository.deleteAllBySubscribe(subscribe);
        List<Long> recipeIdList = requestDto.getRecipeIdList();
        for (Long recipeId : recipeIdList) {
            saveSubscribeRecipe(subscribe, recipeId);
        }
    }
}
