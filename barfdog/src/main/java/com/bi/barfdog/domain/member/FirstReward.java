package com.bi.barfdog.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class FirstReward { // 첫 적립금 받은 여부

    private boolean recommend; // 다른사람 추천으로 인한
    private boolean receiveAgree; // sms/email 수신동의로 인한

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }
}
