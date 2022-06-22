package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.domain.coupon.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalPublishRequestDto{

    @Builder.Default
    @Size(min = 1)
    private List<Long> memberIdList = new ArrayList<>();

    @NotEmpty
    private String expiredDate;

    @NotNull
    private CouponType couponType;

    @NotNull
    private Long couponId;

    @NotNull
    private boolean alimTalk;

}
