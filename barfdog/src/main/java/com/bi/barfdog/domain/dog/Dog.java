package com.bi.barfdog.domain.dog;

import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.Recipe;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class Dog {

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

    private boolean oldDog; // 노령견 여부

    private String dogType;

    @Enumerated(EnumType.STRING)
    private DogSize dogSize; // [LARGE, MIDDLE, SMALL]

    private double weight;

    private boolean neutralization; // 중성화 여부

    @Embedded
    private DogActivity dogActivity;

    @Enumerated(EnumType.STRING)
    private DogStatus dogStatus; // [HEALTHY, NEED_DIET, OBESITY, PREGNANT, LACTATING]

    private int snackCountLevel; // 간식 횟수 1~3

    private String inedibleFood;
    private String inedibleFoodEtc; // 못먹는 음식 기타 일 때

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    private String caution; // 질병 및 주의사항

    @Embedded
    private DogProfilePicture dogProfilePicture;

    @Embedded
    private DogAnalysis dogAnalysis;



}
