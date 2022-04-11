package com.bi.barfdog.domain.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Embeddable
public class SnackConstant {

    private BigDecimal snackLittle;
    private BigDecimal snackNormal;
    private BigDecimal snackMuch;

}
