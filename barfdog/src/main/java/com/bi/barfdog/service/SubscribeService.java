package com.bi.barfdog.service;

import com.bi.barfdog.api.subscribeDto.UpdateGramDto;
import com.bi.barfdog.api.subscribeDto.UpdatePlanDto;
import com.bi.barfdog.api.subscribeDto.UseCouponDto;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.iamport.Iamport_API;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

    private IamportClient client = new IamportClient(Iamport_API.API_KEY, Iamport_API.API_SECRET);


    @Transactional
    public void useCoupon(Long id, UseCouponDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();
        Long memberCouponId = requestDto.getMemberCouponId();
        MemberCoupon memberCoupon = memberCouponRepository.findById(memberCouponId).get();
        int discount = requestDto.getDiscount();
        subscribe.useCoupon(memberCoupon, discount);

        unscheduleAndNewSchedule(subscribe);

    }

    @Transactional
    public void updateGram(Long id, UpdateGramDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();
        subscribe.updateGram(requestDto);

        unscheduleAndNewSchedule(subscribe);
    }

    @Transactional
    public void updatePlan(Long id, UpdatePlanDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();

//        String recipeName = getRecipeName(subscribe);
//        BeforeSubscribe newBeforeSubscribe = saveNewBeforeSubscribe(subscribe, recipeName);
//        subscribe.setBeforeSubscribe(newBeforeSubscribe);

        subscribe.updatePlan(requestDto);

        subscribeRecipeRepository.deleteAllBySubscribe(subscribe);
        List<Long> recipeIdList = requestDto.getRecipeIdList();
        for (Long recipeId : recipeIdList) {
            saveSubscribeRecipe(subscribe, recipeId);
        }

        unscheduleAndNewSchedule(subscribe);

    }

    private void unscheduleAndNewSchedule(Subscribe subscribe) {
        String customerUid = subscribe.getCard().getCustomerUid();
        String merchant_uid = subscribe.getNextOrderMerchant_uid();


        UnscheduleData unscheduleData = new UnscheduleData(customerUid);
        unscheduleData.addMerchantUid(merchant_uid);

        try {
            client.unsubscribeSchedule(unscheduleData);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sleepThread(1000);

        Date nextPaymentDate = java.sql.Timestamp.valueOf(subscribe.getNextPaymentDate());
        int nextPaymentPrice = subscribe.getNextPaymentPrice() - subscribe.getDiscount();
        ScheduleData scheduleData = new ScheduleData(customerUid);
        scheduleData.addSchedule(new ScheduleEntry(merchant_uid, nextPaymentDate, BigDecimal.valueOf(nextPaymentPrice)));

        try {
            client.subscribeSchedule(scheduleData);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
