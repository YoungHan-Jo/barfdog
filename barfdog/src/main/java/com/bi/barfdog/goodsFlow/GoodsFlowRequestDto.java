package com.bi.barfdog.goodsFlow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class GoodsFlowRequestDto {
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<RequestDto> items = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor @Getter
    public static class RequestDto {
        @NotNull
        private String uniqueCd; // 고객사용번호
        @NotNull
        private String transUniqueCd; // 배송고유번호
        @NotNull
        private String deliverCode; // 택배사코드
        @NotNull
        private String sheetNo; // 운송장번호
        private String ordNo; // 주문번호
    }

}
