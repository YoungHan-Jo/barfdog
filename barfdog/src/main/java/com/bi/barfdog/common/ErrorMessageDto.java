package com.bi.barfdog.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class ErrorMessageDto {
    private int status;
    private String reason;
}
