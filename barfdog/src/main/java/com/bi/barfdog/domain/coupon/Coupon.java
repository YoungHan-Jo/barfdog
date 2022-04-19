package com.bi.barfdog.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter
@Entity
public class Coupon {

    @Id @GeneratedValue
    @Column(name = "coupon_id")
    private Long id;
}
