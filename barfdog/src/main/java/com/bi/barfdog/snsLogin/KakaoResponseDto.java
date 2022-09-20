package com.bi.barfdog.snsLogin;

import lombok.*;
import javax.persistence.DiscriminatorValue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("kakao")
@Builder
@EqualsAndHashCode(callSuper = false)
public class KakaoResponseDto extends ResponseDto {
    private ResponseDto response;

    public KakaoResponseDto newMember() {
        resultcode = SnsResponse.NEW_MEMBER_CODE;
        message = SnsResponse.NEW_MEMBER_MESSAGE;
        return this;
    }

    public KakaoResponseDto internalServerError() {
        resultcode = SnsResponse.INTERNAL_ERROR_CODE;
        message = SnsResponse.INTERNAL_ERROR_MESSAGE;
        return this;
    }

    public KakaoResponseDto connectNewSns() {
        resultcode = SnsResponse.NEED_TO_CONNECT_NEW_SNS_CODE;
        message = SnsResponse.NEED_TO_CONNECT_NEW_SNS_MESSAGE;
        return this;
    }

    public KakaoResponseDto naver() {
        resultcode = SnsResponse.CONNECTED_BY_NAVER_CODE;
        message = SnsResponse.CONNECTED_BY_NAVER_MESSAGE;
        return this;
    }

    public KakaoResponseDto kakao() {
        resultcode = SnsResponse.CONNECTED_BY_KAKAO_CODE;
        message = SnsResponse.CONNECTED_BY_KAKAO_MESSAGE;
        return this;
    }

    public KakaoResponseDto success() {
        resultcode = SnsResponse.SUCCESS_CODE;
        message = SnsResponse.SUCCESS_MESSAGE;
        return this;
    }

    public KakaoResponseDto authFailedError() {
        resultcode = SnsResponse.AUTHENTICATION_FAILED_CODE;
        message = SnsResponse.AUTHENTICATION_FAILED_MESSAGE;
        return this;
    }

    public KakaoResponseDto unfinishedMember() {
        resultcode = SnsResponse.UNFINISHED_MEMBER_CODE;
        message = SnsResponse.UNFINISHED_MEMBER_MESSAGE;
        return this;
    }

    public KakaoResponseDto alreadyConnectedMember() {
        resultcode = SnsResponse.ALREADY_CONNECTED_MEMBER_CODE;
        message = SnsResponse.ALREADY_CONNECTED_MEMBER_MESSAGE;
        return this;
    }

    public KakaoResponseDto noneExistMember() {
        resultcode = SnsResponse.NONE_EXISTENT_MEMBER_CODE;
        message = SnsResponse.NONE_EXISTENT_MEMBER_MESSAGE;
        return this;
    }

    public KakaoResponseDto notAdultMember() {
        resultcode = SnsResponse.LESS_THAN_FOURTEEN_MEMBER_CODE;
        message = SnsResponse.LESS_THAN_FOURTEEN_MEMBER_MESSAGE;
        return this;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseDto{
        private Long id;
        private boolean has_signed_up;
        private KakaoAccountDto kakao_accountDto;
    }
}
