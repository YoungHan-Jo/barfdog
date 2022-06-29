package com.bi.barfdog.api.settingDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSettingDto {

    @NotNull
    private BigDecimal activityVeryLittle;
    @NotNull
    private BigDecimal activityLittle;
    @NotNull
    private BigDecimal activityNormal;
    @NotNull
    private BigDecimal activityMuch;
    @NotNull
    private BigDecimal activityVeryMuch;
    @NotNull
    private BigDecimal snackLittle;
    @NotNull
    private BigDecimal snackNormal;
    @NotNull
    private BigDecimal snackMuch;

    @NotNull
    @Min(0)
    private int price; // 기본 배송비
    @NotNull
    @Min(0)
    private int freeCondition; // xx 원 이상 무료 배송 조건
}
