package com.bi.barfdog.api;

import com.bi.barfdog.api.iamportDto.WebHookRequestDto;
import com.bi.barfdog.common.RandomString;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class IamportApiController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @PostMapping("/iamport/webhook")
    public ResponseEntity webHook(@RequestBody WebHookRequestDto jsonObject) {

        System.out.println("post");
        String impUid = jsonObject.getImp_uid();
        System.out.println("impUid = " + impUid);
        String merchantUid = jsonObject.getMerchant_uid();
        System.out.println("merchantUid = " + merchantUid);
        String status = jsonObject.getStatus();
        System.out.println("status = " + status);

        Optional<SubscribeOrder> optionalSubscribeOrder = orderRepository.findByMerchantUid(merchantUid);
        if (!optionalSubscribeOrder.isPresent()){
            System.out.println("존재하지않는 merchantUid");
            return ResponseEntity.ok(null);
        }

        if (status.equals("paid")) {
            orderService.successPaymentSchedule(merchantUid, impUid);
        } else if (status.equals("failed")) {
            orderService.failPaymentSchedule(merchantUid);
        }

        return ResponseEntity.ok(null);
    }

}
