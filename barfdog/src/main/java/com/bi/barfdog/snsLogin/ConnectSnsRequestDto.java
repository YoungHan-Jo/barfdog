package com.bi.barfdog.snsLogin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectSnsRequestDto {

    @NotEmpty
    private String phoneNumber; // ['010xxxxxxxx']
    @NotEmpty
    private String password;
    @NotEmpty
    private String provider; // ['naver','kakao']
    @NotEmpty
    private String providerId;

    private int tokenValidDays;

}
