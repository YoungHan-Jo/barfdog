package com.bi.barfdog.api.orderDto;

import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSheetGeneralResponseDto {




    private String name;
    private String email;
    private String phoneNumber;
    private Address address;


    private
    private LocalDate nextDeliveryDate;

    @Builder.Default
    private List<OrderSheetSubsCouponDto> coupons = new ArrayList();

    private int reward;

    private boolean brochure;


    public

}
