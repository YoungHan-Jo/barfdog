package com.bi.barfdog.directsend;

public interface DirectSend {
    String SENDER = "0438554995";
    String USERNAME = "oilmanj";
    String API_KEY = "HrgHhRBDF7it2Vq";
    String KAKAO_PLUS_ID = "바프독";

    String CODE_PUBLISH_TEMPLATE = "1";
    String GENERAL_PUBLISH_TEMPLATE = "4";

    String ORDER_SUCCESS_TEMPLATE = "10";
    String ORDER_CANCEL_TEMPLATE = "16";
    String ORDER_PRODUCING_TEMPLATE = "25";
    String ORDER_DELIVERY_READY_TEMPLATE = "31";
    String DELIVERY_START_TEMPLATE = "28";

    String GRADE_TEMPLATE = "43";
    String TOMORROW_PAYMENT_TEMPLATE = "13";
    String RETURN_TEMPLATE = "19";
    String EXCHANGE_TEMPLATE = "22";

    String SUBSCRIBE_PAYMENT_SCHEDULE_FAIL = "52";

}
