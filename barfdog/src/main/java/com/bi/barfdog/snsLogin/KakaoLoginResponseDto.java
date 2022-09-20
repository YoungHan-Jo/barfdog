package com.bi.barfdog.snsLogin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoLoginResponseDto {

    private String token_type; // bearer 고정
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private int refresh_token_expires_in;
}
