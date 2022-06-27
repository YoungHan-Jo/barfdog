package com.bi.barfdog.snsLogin;

public interface SnsResponse {
    String NEW_MEMBER_CODE = "251";
    String NEW_MEMBER_MESSAGE = "new member";

    String NEED_TO_CONNECT_NEW_SNS_CODE = "252";
    String NEED_TO_CONNECT_NEW_SNS_MESSAGE = "need to connect new sns";

    String CONNECTED_BY_KAKAO_CODE = "253";
    String CONNECTED_BY_KAKAO_MESSAGE = "has already been connected by kakao";

    String CONNECTED_BY_NAVER_CODE = "254";
    String CONNECTED_BY_NAVER_MESSAGE = "has already been connected by naver";

    String SUCCESS_CODE = "200";
    String SUCCESS_MESSAGE = "login success";

    String INTERNAL_ERROR_CODE = "500";
    String INTERNAL_ERROR_MESSAGE = "internal error";

    String AUTHENTICATION_FAILED_CODE = "024";
    String AUTHENTICATION_FAILED_MESSAGE = "authentication failed";

    String TEST_ACCESS_CODE = "AAAAOYIVrKisUDudHAEKBoWJtIz3IZz40gZSGMzUVeNgMYGEYzkhAg8GHrvOn8LCJ-W7yeoM7CSJ4w24G3W_1-KDJUc";
}
