package com.bi.barfdog.snsLogin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("naver")
@Builder
public class NaverResponseDto extends ResponseDto {
    private ResponseDto response;

    public NaverResponseDto newMember() {
        resultcode = SnsResponse.NEW_MEMBER_CODE;
        message = SnsResponse.NEW_MEMBER_MESSAGE;
        return this;
    }

    public NaverResponseDto internalServerError() {
        resultcode = SnsResponse.INTERNAL_ERROR_CODE;
        message = SnsResponse.INTERNAL_ERROR_MESSAGE;
        return this;
    }

    public NaverResponseDto connectNewSns() {
        resultcode = SnsResponse.NEED_TO_CONNECT_NEW_SNS_CODE;
        message = SnsResponse.NEED_TO_CONNECT_NEW_SNS_MESSAGE;
        return this;
    }

    public NaverResponseDto naver() {
        resultcode = SnsResponse.CONNECTED_BY_NAVER_CODE;
        message = SnsResponse.CONNECTED_BY_NAVER_MESSAGE;
        return this;
    }

    public NaverResponseDto kakao() {
        resultcode = SnsResponse.CONNECTED_BY_KAKAO_CODE;
        message = SnsResponse.CONNECTED_BY_KAKAO_MESSAGE;
        return this;
    }

    public NaverResponseDto success() {
        resultcode = SnsResponse.SUCCESS_CODE;
        message = SnsResponse.SUCCESS_MESSAGE;
        return this;
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
