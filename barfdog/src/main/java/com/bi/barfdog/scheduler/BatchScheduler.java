package com.bi.barfdog.scheduler;

import com.bi.barfdog.service.DeliveryService;
import com.bi.barfdog.service.MemberService;
import com.bi.barfdog.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BatchScheduler {

    private final MemberService memberService;
    private final SubscribeService subscribeService;
    private final DeliveryService deliveryService;

    @Scheduled(cron = "0 0 3 1 * *")
    public void gradeCheck() {
        memberService.gradeScheduler();

    }

    @Scheduled(cron = "0 0 9 * * *")
    public void subscribePaymentAlim() {
        subscribeService.paymentAlimScheduler();
    }

    @Scheduled(cron = "0 * * * * *")
    public void deliveryCheck() {
        deliveryService.deliveryDoneScheduler();
    }


}
