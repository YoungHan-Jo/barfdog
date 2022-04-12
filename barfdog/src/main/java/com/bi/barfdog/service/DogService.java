package com.bi.barfdog.service;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.common.StandardVar;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.setting.ActivityConstant;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.domain.setting.SnackConstant;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import com.bi.barfdog.repository.DogRepository;
import com.bi.barfdog.repository.RecipeRepository;
import com.bi.barfdog.repository.SettingRepository;
import com.bi.barfdog.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.bi.barfdog.common.StandardVar.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DogService {

    private final DogRepository dogRepository;
    private final RecipeRepository recipeRepository;
    private final SubscribeRepository subscribeRepository;
    private final SettingRepository settingRepository;


    @Transactional
    public Dog createDog(DogSaveRequestDto requestDto, Member member) {

        List<Dog> dogs = dogRepository.findByMember(member);
        Recipe recipe = recipeRepository.findById(requestDto.getRecommendRecipeId()).get();

        String birth = requestDto.getBirth();

        Subscribe subscribe = Subscribe.builder()
                .status(SubscribeStatus.BEFORE_PAYMENT)
                .build();
        subscribeRepository.save(subscribe);

        DogSize dogSize = requestDto.getDogSize();
        Long startAge = getTerm(birth + "01");
        boolean oldDog = requestDto.isOldDog();
        boolean neutralization = requestDto.isNeutralization();
        DogStatus dogStatus = requestDto.getDogStatus();
        SnackCountLevel snackCountLevel = requestDto.getSnackCountLevel();
        BigDecimal weight = new BigDecimal(requestDto.getWeight());

        DogAnalysis dogAnalysis = getDogAnalysis(requestDto, recipe, dogSize, startAge, oldDog, neutralization, dogStatus, requestDto.getActivityLevel(), snackCountLevel);

        Dog dog = Dog.builder()
                .member(member)
                .representative(dogs.size() == 0 ? true : false)
                .name(requestDto.getName())
                .gender(requestDto.getGender())
                .birth(birth)
                .startAge(startAge)
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
                .recommendRecipe(recipe)
                .caution(requestDto.getCaution())
                .subscribe(subscribe)
                .dogAnalysis(dogAnalysis)
                .build();

        dogRepository.save(dog);

        return dog;
    }

    private DogActivity getDogActivity(DogSaveRequestDto requestDto) {
        return new DogActivity(requestDto.getActivityLevel(), Integer.valueOf(requestDto.getWalkingCountPerWeek()), Double.valueOf(requestDto.getWalkingTimePerOneTime()));
    }

    private DogAnalysis getDogAnalysis(DogSaveRequestDto requestDto, Recipe recipe, DogSize dogSize, Long startAge, boolean oldDog, boolean neutralization, DogStatus dogStatus, ActivityLevel activityLevel, SnackCountLevel snackCountLevel) {
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

        DogAnalysis dogAnalysis = new DogAnalysis(recommendKcal, oneDayRecommendGram, oneMealRecommendGram);
        return dogAnalysis;
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

}
