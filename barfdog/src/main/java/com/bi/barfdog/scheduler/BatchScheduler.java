package com.bi.barfdog.scheduler;

import com.bi.barfdog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchScheduler {

    private final MemberService memberService;
    private final SubscribeService subscribeService;
    private final DeliveryService deliveryService;
    private final OrderService orderService;
    private final CouponService couponService;

    @Scheduled(cron = "0 0 3 1 * *")
    public void gradeCheck() {
        memberService.gradeScheduler();
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void subscribePaymentAlim() {
        subscribeService.paymentAlimScheduler();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deliveryCheck() {
        deliveryService.deliveryDoneScheduler();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void autoConfirmOrders() {
        orderService.autoConfirm();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void removeExpiredCoupon() {
        couponService.removeExpiredCoupon();
    }

}
