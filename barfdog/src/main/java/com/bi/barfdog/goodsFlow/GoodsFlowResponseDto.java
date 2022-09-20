package com.bi.barfdog.goodsFlow;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class GoodsFlowResponseDto {
    private boolean success;
    private String message;

    public GoodsFlowResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public void success() {
        success = true;
    }

    public void fail() {
        success = false;
        message = "서버에서 요청을 처리할 수 없음";
    }
}
