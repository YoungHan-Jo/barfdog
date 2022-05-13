package com.bi.barfdog.api.couponDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAutoCouponRequest {

    @NotNull
    @Valid
    private List<UpdateAutoCouponRequestDto> updateAutoCouponRequestDtoList;

    /*
    * 내부 클래스
    * */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateAutoCouponRequestDto{
        @NotNull
        private Long id;

        @Positive
        private int discountDegree; // 할인 정도 ( 원/% )

        @PositiveOrZero
        private int availableMinPrice; // 사용가능한 최소 금액
    }
}
