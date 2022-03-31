package com.bi.barfdog.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@ToString
public class Address {


    private String zipcode;
    private String city;
    private String street;
    private String detailAddress;

    protected Address() {
    }

    public Address(String zipcode, String city, String street, String detailAddress) {
        this.zipcode = zipcode;
        this.city = city;
        this.street = street;
        this.detailAddress = detailAddress;
    }
}
