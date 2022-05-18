package com.bi.barfdog.domain.member;

import com.bi.barfdog.api.memberDto.MemberUpdateRequestDto;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private String birthday; // 'yyyyMMdd'

    @Enumerated(EnumType.STRING)
    private Gender gender; // [MALE, FEMALE, NONE]

    @Embedded
    private Agreement agreement;

    private String recommendCode;

    private String myRecommendationCode;

    @Enumerated(EnumType.STRING)
    private Grade grade; // [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]

    private int reward;
    
    private int accumulatedAmount; // 누적 금액

    private boolean subscribe; // 정기 구독 여부

    private int accumulatedSubscribe; // 누적 구독 수

    private boolean brochure; // 브로슈어 받은적 있는지 여부

    @Embedded
    private FirstReward firstReward;

    private LocalDateTime lastLoginDate;

    private boolean withdrawal;

    private String roles; // USER,ADMIN

    private String provider;
    private String providerId;

    public List<String> getRoleList() {
        if (this.roles.length() > 0) {
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }




    /*
    * 비지니스 로직
    * */

    public void setRecommendCode(String recommendCode) {
        this.recommendCode = recommendCode;
    }

    public void chargePoint(int chargedReward) {
        this.reward += chargedReward;
    }

    public void usePoint(int usedReward) {
        this.reward -= usedReward;
    }

    public void changePassword(String temporaryPassword) {
        password = temporaryPassword;
    }

    public void updateMember(MemberUpdateRequestDto requestDto) {
        name = requestDto.getName();
        password = requestDto.getPassword();
        phoneNumber = requestDto.getPhoneNumber();
        address = requestDto.getAddress();
        birthday = requestDto.getBirthday();
        gender = requestDto.getGender();
        agreement = new Agreement(true, true,
                requestDto.isReceiveSms(), requestDto.isReceiveEmail(), true);

    }

    public void getFirstRewardRecommend() {
        firstReward.setRecommend(true);
    }

    public void login() {
        lastLoginDate = LocalDateTime.now();
    }

    public void updateBirthday(LocalDate birthday) {
        this.birthday = birthday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public void updateGrade(Grade grade) {
        this.grade = grade;
    }
}

