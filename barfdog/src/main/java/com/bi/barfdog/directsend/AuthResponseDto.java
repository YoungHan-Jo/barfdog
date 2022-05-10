package com.bi.barfdog.directsend;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto extends DirectSendResponseDto{

    private String authNumber;

    public AuthResponseDto(int responseCode, int status, String msg, String authNumber) {
        super(responseCode, status, msg);
        this.authNumber = authNumber;
    }

    public String getAuthNumber() {
        return authNumber;
    }

    public void setAuthNumber(String authNumber) {
        this.authNumber = authNumber;
    }
}
