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
    private Long id;
    private String connected_at;
    private KakaoForPartner for_partner;
    private KakaoProperties properties;
    private KakaoAccount kakao_account;

    public static class KakaoForPartner {
        private String uuid;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoProperties{
        private String nickname;
        private String profile_image;
        private String thumbnail_image;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoAccount {
        private boolean profile_needs_agreement;
        private KakaoProfile profile;
        private boolean has_email;
        private boolean email_needs_agreement;
        private boolean is_email_valid;
        private boolean is_email_verified;
        private String email;
        private boolean has_age_range;
        private boolean age_range_needs_agreement;
        private String age_range;
        private boolean has_birthday;
        private boolean birthday_needs_agreement;
        private String birthday;
        private String birthday_type;
        private boolean has_gender;
        private boolean gender_needs_agreement;
        private String gender;
        private boolean phone_number_needs_agreement;
        private String phone_number;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoProfile {
        private String nickname;
        private String thumbnail_image_url;
        private String profile_image_url;
        private boolean is_default_image;
    }

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
}
