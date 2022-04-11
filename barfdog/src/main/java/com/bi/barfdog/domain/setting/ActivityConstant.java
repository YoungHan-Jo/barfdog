package com.bi.barfdog.domain.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
@Embeddable
public class ActivityConstant {

    private BigDecimal activityVeryLittle;
    private BigDecimal activityLittle;
    private BigDecimal activityNormal;
    private BigDecimal activityMuch;
    private BigDecimal activityVeryMuch;

}
