package com.bi.barfdog.api.dogDto;

import com.bi.barfdog.domain.dog.ActivityLevel;
import com.bi.barfdog.domain.dog.DogSize;
import com.bi.barfdog.domain.dog.DogStatus;
import com.bi.barfdog.domain.dog.SnackCountLevel;
import com.bi.barfdog.domain.member.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DogSaveRequestDto {

    @NotEmpty
    private String name;

    @NotNull
    private Gender gender;

    @NotEmpty
    private String birth;

    @NotNull
    private boolean oldDog;

    @NotEmpty
    private String dogType;

    @NotNull
    private DogSize dogSize;

    @NotEmpty
    private String weight;

    @NotNull
    private boolean neutralization;

    @NotNull
    private ActivityLevel activityLevel; // [VERY_LITTLE, LITTLE, NORMAL, MUCH, VERY_MUCH]
    @NotEmpty
    private String walkingCountPerWeek; // 산책 회수
    @NotEmpty
    private String walkingTimePerOneTime; // 산책 시간

    @NotNull
    private DogStatus dogStatus; //[HEALTHY, NEED_DIET, OBESITY, PREGNANT, LACTATING]

    @NotNull
    private SnackCountLevel snackCountLevel; // [LITTLE, NORMAL, MUCH]

    @NotNull
    private String inedibleFood;

    @NotNull
    private String inedibleFoodEtc; // 못먹는 음식 기타 일 때

    @NotNull
    private Long recommendRecipeId; // 특별히 챙겨주고 싶은 부분 레시피 id

    @NotNull
    private String caution; // 질병 및 주의사항


}
