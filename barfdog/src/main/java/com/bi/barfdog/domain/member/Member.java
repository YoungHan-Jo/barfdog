package com.bi.barfdog.domain.member;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Embedded
    private Address address;

    @Column(length = 8)
    private String birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender; // [MALE, FEMALE, NONE]

    @Embedded
    private Agreement agreement;

    private String recommendCode;

    private String myRecommendationCode;

    @Enumerated(EnumType.STRING)
    private Grade grade; // [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]

    private int rewardPoint;

    private LocalDateTime lastLoginDate;

    private String roles; // USER,ADMIN

    private String provider;
    private String providerId;

    public List<String> getRoleList() {
        if (this.roles.length() > 0) {
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }

    public void setRecommendCode(String recommendCode) {
        this.recommendCode = recommendCode;
    }

    public void chargePoint(int chargedPoint) {
        this.rewardPoint += chargedPoint;
    }

    public void usePoint(int usedPoint) {
        this.rewardPoint -= usedPoint;
    }
}

