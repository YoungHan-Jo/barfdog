package com.bi.barfdog.snsLogin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverResponseDto {

    private String resultcode;

    private String message;

    private ResponseDto response;

    public void newMember() {
        resultcode = "1001";
        message = "new member";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDto{

        private String id;
        private String gender;
        private String email;
        private String mobile;
        private String mobile_e164;
        private String name;
        private String birthday;
        private String birthyear;

    }
}
