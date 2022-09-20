package com.bi.barfdog.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@ToString
public class Address {


    private String zipcode; // 우편코드
    private String city; // 지자체 단위
    private String street; // 도로명주소
    private String detailAddress; // 상세주소

    protected Address() {
    }

    public Address(String zipcode, String city, String street, String detailAddress) {
        this.zipcode = zipcode;
        this.city = city;
        this.street = street;
        this.detailAddress = detailAddress;
    }
}
