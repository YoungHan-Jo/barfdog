package com.bi.barfdog.domain.member;

import com.bi.barfdog.api.memberDto.MemberUpdateRequestDto;
import com.bi.barfdog.api.orderDto.GeneralOrderRequestDto;
import com.bi.barfdog.api.orderDto.SubscribeOrderRequestDto;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.reward.RewardPoint;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private String birthday; // 'yyyyMMdd'

    @Enumerated(EnumType.STRING)
    private Gender gender; // [MALE, FEMALE, NONE]

    @Embedded
    private Agreement agreement;

    private String recommendCode; // 추천한 코드

    private String myRecommendationCode; // 내 추천 코드

    @Enumerated(EnumType.STRING)
    private Grade grade; // [BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, BARF]

    private int reward;

    private boolean isPaid; // 구매한 적 있는지 여부
    private LocalDateTime firstPaymentDate; // 첫 결제 날짜

    private boolean isTemporaryPassword; // 임시 비밀번호 여부
    
    private int accumulatedAmount; // 누적 금액

    private boolean isSubscribe; // 정기 구독 여부

    private int accumulatedSubscribe; // 누적 구독 수

    private boolean isBrochure; // 브로슈어 받은적 있는지 여부

    @Embedded
    private FirstReward firstReward;

    private LocalDateTime lastLoginDate;

    private boolean isWithdrawal; // 탈퇴여부

    private String roles; // [USER,SUBSCRIBER,ADMIN] 띄워쓰기 없이

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
    public void recommendFriend(String recommendCode) {
        this.recommendCode = recommendCode;
        firstReward.setRecommend(true);
        chargeReward(RewardPoint.RECOMMEND);
    }

    public void chargeReward(int reward) {
        this.reward += reward;
    }

    public void useReward(int reward) {
        this.reward -= reward;
    }

    public void changePassword(String hashPassword) {
        password = hashPassword;
        isTemporaryPassword = false;
    }

    public void temporaryPassword(String temporaryPassword) {
        password = temporaryPassword;
        isTemporaryPassword = true;
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

    public void login() {
        lastLoginDate = LocalDateTime.now();
    }

    public void updateBirthday(LocalDate birthday) {
        this.birthday = birthday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public void withdrawal() {
        isWithdrawal = true;
        provider = "";
        providerId = "";
    }

    public void connectSns(String provider, String providerId) {
        this.provider = provider.toLowerCase();
        this.providerId = providerId;
    }

    public void unconnectSns() {
        this.provider = "";
        this.providerId = "";
    }

    public void subscribe() {
        this.roles += ",SUBSCRIBER";
    }

    public void subscribeOrder(SubscribeOrderRequestDto requestDto) {
        useReward(requestDto.getDiscountReward());
    }

    public void subscribeOrderSuccess(Order order) {
        accumulatedAmount += order.getPaymentPrice();
        if (order.isBrochure()) {
            isBrochure = true;
        }
        accumulatedSubscribe++;
        isSubscribe = true;
        roles = "USER,SUBSCRIBER";
    }

    public void generalOrder(GeneralOrderRequestDto requestDto) {
        useReward(requestDto.getDiscountReward());
    }

    public void generalOrderSuccess(Order order) {
        accumulatedAmount += order.getPaymentPrice();
        if (order.isBrochure()) {
            isBrochure = true;
        }
    }

    public void generalOrderFail(int discountReward) {
        chargeReward(discountReward);
    }

    public void subscribeOrderFail(SubscribeOrder order) {
        reward += order.getDiscountReward();
    }

    public void changeGrade(Grade grade) {
        this.grade = grade;
    }

    public void cancelSubscribePayment(SubscribeOrder order) {
        int paymentPrice = order.getPaymentPrice();

        decreaseAccumulatedSubscribe();
        decreaseAccumulatedAmount(paymentPrice);

        chargeReward(order.getDiscountReward());
    }

    private void decreaseAccumulatedSubscribe() {
        if (accumulatedSubscribe > 0) {
            accumulatedSubscribe--;
        }
    }

    public void cancelGeneralPayment(GeneralOrder order) {
        int paymentPrice = order.getPaymentPrice();
        decreaseAccumulatedAmount(paymentPrice);

        chargeReward(order.getDiscountReward());
    }

    private void decreaseAccumulatedAmount(int paymentPrice) {
        accumulatedAmount -= paymentPrice;
        if (accumulatedAmount < 0) {
            accumulatedAmount = 0;
        }
    }

    public void stopSubscriber() {
        if (!roles.contains("ADMIN")) {
            roles = "USER";
            isSubscribe = false;
        }
    }

    public void firstPayment() {
        isPaid = true;
        firstPaymentDate = LocalDateTime.now();
    }

    public void saveReceiveAgreeReward() {
        chargeReward(RewardPoint.RECEIVE_AGREEMENT);
        firstReward = FirstReward.builder()
                .receiveAgree(true)
                .recommend(getFirstReward().isRecommend())
                .build();
    }
}

