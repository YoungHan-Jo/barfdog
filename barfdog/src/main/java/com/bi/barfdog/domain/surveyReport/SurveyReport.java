package com.bi.barfdog.domain.surveyReport;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.dog.Dog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class SurveyReport extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "survey_report_id")
    private Long id;

    @OneToOne(mappedBy = "surveyReport", fetch = LAZY)
    private Dog dog;

    @Embedded
    private AgeAnalysis ageAnalysis;

    @Embedded
    private WeightAnalysis weightAnalysis;

    @Embedded
    private ActivityAnalysis activityAnalysis;

    @Embedded
    private WalkingAnalysis walkingAnalysis;

    @Embedded
    private SnackAnalysis snackAnalysis;

    @Embedded
    private FoodAnalysis foodAnalysis;


    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public void update(SurveyReport newSurveyReport) {
        ageAnalysis = newSurveyReport.getAgeAnalysis();
        weightAnalysis = newSurveyReport.getWeightAnalysis();
        activityAnalysis = newSurveyReport.getActivityAnalysis();
        walkingAnalysis = newSurveyReport.getWalkingAnalysis();
        snackAnalysis = newSurveyReport.getSnackAnalysis();
        foodAnalysis = newSurveyReport.getFoodAnalysis();
    }

}
