package com.bi.barfdog.domain.dog;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.surveyReport.FoodAnalysis;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder @Getter
@Entity
public class Dog extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "dog_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean representative; // 대표견

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String birth; // yyyyMM 형식

    private Long startAgeMonth; // 바프독 시작 나이(개월)

    private boolean oldDog; // 노령견 여부

    private String dogType;

    @Enumerated(EnumType.STRING)
    private DogSize dogSize; // [LARGE, MIDDLE, SMALL]

    private BigDecimal weight;

    private boolean neutralization; // 중성화 여부

    @Embedded
    private DogActivity dogActivity; // 활동량 관련

    @Enumerated(EnumType.STRING)
    private DogStatus dogStatus; // [HEALTHY, NEED_DIET, OBESITY, PREGNANT, LACTATING]

    @Enumerated(EnumType.STRING)
    private SnackCountLevel snackCountLevel; // [LITTLE, NORMAL, MUCH]

    private String inedibleFood;
    private String inedibleFoodEtc; // 못먹는 음식 기타 일 때

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recommendRecipe;

    private String caution; // 질병 및 주의사항

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "subscribe_id")
    private Subscribe subscribe;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "survey_report_id")
    private SurveyReport surveyReport;

    @Embedded
    private DogProfilePicture dogProfilePicture;


    public void setSurveyReport(SurveyReport surveyReport) {
        this.surveyReport = surveyReport;
    }
}
