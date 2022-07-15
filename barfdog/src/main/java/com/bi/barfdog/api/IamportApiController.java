package com.bi.barfdog.api;

import com.bi.barfdog.api.iamportDto.WebHookRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IamportApiController {

    @PostMapping("/iamport/webhook")
    public ResponseEntity webHook(@RequestBody WebHookRequestDto jsonObject) {

        System.out.println("post");
        String impUid = jsonObject.getImp_uid();
        System.out.println("impUid = " + impUid);
        String merchantUid = jsonObject.getMerchant_uid();
        System.out.println("merchantUid = " + merchantUid);
        String status = jsonObject.getStatus();
        System.out.println("status = " + status);


        return ResponseEntity.ok(null);
    }

    @GetMapping("/iamport/webhook")
    public ResponseEntity webHook_get(@RequestBody WebHookRequestDto jsonObject) {

        System.out.println("get");
        System.out.println("requestDto = " + jsonObject);

        return ResponseEntity.ok(null);
    }
}
