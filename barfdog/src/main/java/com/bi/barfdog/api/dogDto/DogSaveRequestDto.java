package com.bi.barfdog.api.dogDto;

import com.bi.barfdog.domain.dog.ActivityLevel;
import com.bi.barfdog.domain.dog.DogSize;
import com.bi.barfdog.domain.dog.DogStatus;
import com.bi.barfdog.domain.dog.SnackCountLevel;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import static javax.persistence.FetchType.LAZY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DogSaveRequestDto {

    private String name;

    private Gender gender;

    private String birth;

    private boolean oldDog;

    private String dogType;

    private DogSize dogSize;

    private String weight;

    private boolean neutralization;

    private ActivityLevel activityLevel; // [VERY_LITTLE, LITTLE, NORMAL, MUCH, VERY_MUCH]
    private int walkingCountPerWeek; // 산책 회수
    private double walkingTimePerOneTime; // 산책 시간

    private DogStatus dogStatus; //[HEALTHY, NEED_DIET, OBESITY, PREGNANT, LACTATING]

    private SnackCountLevel snackCountLevel; // [LITTLE, NORMAL, MUCH]

    private String inedibleFood;
    private String inedibleFoodEtc; // 못먹는 음식 기타 일 때

    private Long recommendRecipeId; // 특별히 챙겨주고 싶은 부분 레시피 id

    private String caution; // 질병 및 주의사항


}
