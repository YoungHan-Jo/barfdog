package com.bi.barfdog.domain.dog;

import com.bi.barfdog.domain.Member;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
public class Dog {

    @Id
    @GeneratedValue
    @Column(name = "dog_id")
    private Long id;

    private String name;
    private LocalDate birth;
    private String oldAge; // 노령견 여부 Y/N
    private String dogType;
    private String dogSize; // LARGE, MIDDLE, SMALL

    @Column(precision = 3, scale = 1) // 총 3자리, 소수점 1자리
    private BigDecimal weight;

    private String neutralization; // 중성화 여부 Y/N
    private String activityLevel; // 활동량 1~5
    private int walkingCountPerWeek; // 산책 회수
    private double walkingTimePerOneTime; // 산책 시간

    @Enumerated(EnumType.STRING)
    private DogStatus dogStatus; // [HEALTHY, DIET, OBESITY, PREGNANT, LACTATING]
    private int snackCount;

    @Enumerated(EnumType.STRING)
    private SpecialOption specialOption; // [FIRST, RECOVERY, CARE, GROWTH]

    @Enumerated(EnumType.STRING)
    private InedibleFood inedibleFood; // [NONE, CHICKEN, TURKEY, BEEF, LAMB, DUCK, ETC]
    private String inedibleFoodEtc; // 못먹는 음식 기타 일 때

    private String disease; // 질병


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;









}
