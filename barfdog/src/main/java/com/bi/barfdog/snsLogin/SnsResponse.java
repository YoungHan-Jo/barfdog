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

    String UNFINISHED_MEMBER_CODE = "-101";
    String UNFINISHED_MEMBER_MESSAGE = "did not finished to connecting by kakao";

    String ALREADY_CONNECTED_MEMBER_CODE = "-102";
    String ALREADY_CONNECTED_MEMBER_MESSAGE = "has already been connected by by kakao";

    String NONE_EXISTENT_MEMBER_CODE = "-103";
    String NONE_EXISTENT_MEMBER_MESSAGE = "does not exist account";

    String LESS_THAN_FOURTEEN_MEMBER_CODE = "-406";
    String LESS_THAN_FOURTEEN_MEMBER_MESSAGE = "member under the age of 14";

    String TEST_NAVER_ACCESS_TOKEN = "AAAAOdxBjKYZT8NIXCR9byF6WPrL26E9AejAXJrGAZ-lFmxez82f_X9K0NuZ3QoxWb2yKnVgQuJ-D34CwmXgR-00Z_o";
}
