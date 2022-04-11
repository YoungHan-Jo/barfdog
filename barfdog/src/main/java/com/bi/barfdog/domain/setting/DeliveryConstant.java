package com.bi.barfdog.domain.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Embeddable
public class DeliveryConstant {

    private int price; // 기본 배송비
    private int freeCondition; // xx 원 이상 무료 배송 조건

}
