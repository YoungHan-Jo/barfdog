package com.bi.barfdog.domain.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class Recipient {

    private String name;
    private String phone;

    private String zipcode;
    private String street;
    private String detailAddress;
}
