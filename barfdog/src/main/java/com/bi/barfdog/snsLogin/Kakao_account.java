package com.bi.barfdog.snsLogin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Kakao_account {
    private KakaoProfile kakaoProfile;
    private KakaoName kakaoName;
    private KakaoEmail kakaoEmail;
    private KakaoAge_range kakaoAge_range;
    private KakaoBirthday kakaoBirthday;
    private KakaoGender kakaoGender;
    private KakaoPhone_number kakaoPhone_number;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoProfile{
        private boolean profile_needs_agreement;
        private boolean profile_nickname_needs_agreement;
        private boolean profile_image_needs_agreement;
        private KakaoProfileInfo profileInfo;
    }

    // JSON
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoProfileInfo{
        private String nickname;
        private String thumbnail_image_url; // 프로필 미리보기 이미지 URL
        private String profile_image_url;
        private boolean is_default_image;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoName{
        private boolean name_needs_agreement;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoEmail{
        private boolean email_needs_agreement;
        private boolean is_email_valid; // 이메일 우효 여부
        private boolean is_email_verified; // 이메일 인증 여부
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoAge_range{
        private boolean age_range_needs_agreement;
        private String age_range;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoBirthday{
        private boolean birthyear_needs_agreement;
        private String birthyear;
        private boolean birthday_needs_agreement;
        private String birthday;
        private String birthday_type; // SOLAR(양력) 또는 LUNAR(음력)
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoGender{
        private boolean gender_needs_agreement;
        private String gender;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KakaoPhone_number{
        private boolean phone_number_needs_agreement;
        private String phone_number;
    }
}
