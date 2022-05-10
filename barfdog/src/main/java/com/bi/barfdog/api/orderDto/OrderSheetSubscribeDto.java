package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.coupon.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSheetSubscribeDto {

    private String name;
    private String email;
    private String phoneNumber;

    private Address address;

    private List<Coupon> coupons = new ArrayList();

    private int reward;

    private boolean brochure;
}
