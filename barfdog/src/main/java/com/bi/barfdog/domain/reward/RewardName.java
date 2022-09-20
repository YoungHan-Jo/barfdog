package com.bi.barfdog.domain.reward;

public interface RewardName {
    String RECOMMEND = "[친구추천] 친구 추천 적립금";
    String INVITE = "[친구초대] 초대한 친구 첫구매 적립금";
    String REVIEW = "[리뷰] 리뷰 작성 적립금";
    String RECEIVE_AGREEMENT = "[수신 동의] sms/이메일 수신 동의 적립금";

    String USE_ORDER = "[주문] 주문 사용 적립금";
    String FAILED_ORDER = "[결제실패] 결제 실패 반환 적립금";
    String CANCEL_PAYMENT = "[결제취소] 결제 취소 반환 적립금";
    String CANCEL_ORDER = "[주문취소] 주문 취소 적립금";
    String RETURN_ORDER = "[반품] 반품 적립금";
    String CONFIRM_ORDER = "[구매] 구매 적립금";
}
