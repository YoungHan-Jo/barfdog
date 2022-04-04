package com.bi.barfdog.directsend;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhoneAuthResponseDto extends DirectSendResponseDto{

    private String authNumber;

    public PhoneAuthResponseDto(int responseCode, int status, String msg, String authNumber) {
        super(responseCode, status, msg);
        this.authNumber = authNumber;
    }
}
