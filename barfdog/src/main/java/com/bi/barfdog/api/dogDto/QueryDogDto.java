package com.bi.barfdog.api.dogDto;

import com.bi.barfdog.api.recipeDto.RecipeSurveyResponseDto;
import com.bi.barfdog.domain.dog.*;
import com.bi.barfdog.domain.member.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryDogDto {

    private DogDto dogDto;

    @Builder.Default
    private List<RecipeSurveyResponseDto> recipeDtoList = new ArrayList<>();
    @Builder.Default
    List<String> ingredients = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class DogDto {

        private Long id;
        private String name;
        private Gender gender;
        private String birth; // yyyyMM 형식
        private boolean oldDog; // 노령견 여부

        private String dogType; //  견종
        private DogSize dogSize; // [LARGE, MIDDLE, SMALL]
        private BigDecimal weight;
        private boolean neutralization; // 중성화 여부

        private ActivityLevel activityLevel; // [VERY_LITTLE, LITTLE, NORMAL, MUCH, VERY_MUCH]
        private int walkingCountPerWeek; // 산책 회수
        private double walkingTimePerOneTime; // 산책 시간
        private DogStatus dogStatus; // [HEALTHY, NEED_DIET, OBESITY, PREGNANT, LACTATING]
        private SnackCountLevel snackCountLevel; // [LITTLE, NORMAL, MUCH]

        private String inedibleFood;
        private String inedibleFoodEtc; // 못먹는 음식 기타 일 때

        private Long recommendRecipeId;
        private String caution; // 질병 및 주의사항

        private BigDecimal oneMealRecommendGram;

    }



}
