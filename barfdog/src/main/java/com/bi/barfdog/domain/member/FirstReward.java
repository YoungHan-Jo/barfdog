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
public class FirstReward {

    private boolean recommend;
    private boolean receiveAgree;

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }
}
