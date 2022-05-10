package com.bi.barfdog.api;

import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/api/orders", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class OrderApiController {

    private final MemberRepository memberRepository;
    private final OrderService orderService;

    @GetMapping("/sheet/subscribe")
    public ResponseEntity queryOrderSheetSubscribe(@CurrentUser Member member) {

        orderService.getOrderSheetSubsDto(member);



        return ResponseEntity.ok(null);
    }

    @GetMapping("/sheet/general")
    public void queryOrderSheetGeneral() {

    }

}
