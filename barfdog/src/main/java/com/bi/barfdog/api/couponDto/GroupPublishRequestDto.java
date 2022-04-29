package com.bi.barfdog.api.couponDto;

import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupPublishRequestDto {

    @NotNull
    private boolean subscribe;
    @NotNull
    private boolean longUnconnected;

    @NotNull
    @Size(min = 1)
    private List<Grade> gradeList = new ArrayList<>();

    @NotNull
    private AREA area; // [ALL, METRO, NON_METRO]

    @NotEmpty
    private String birthYearFrom;
    @NotEmpty
    private String birthYearTo;

    @NotEmpty
    private String expiredDate;
    @NotNull
    private CouponType couponType;
    @NotNull
    private Long couponId;
    @NotNull
    private boolean alimTalk;

}
