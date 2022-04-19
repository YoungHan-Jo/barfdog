package com.bi.barfdog.api;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/api/orders", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class OrderApiController {

    @GetMapping("/sheet/subscribe")
    public void queryOrderSheetSubscribe() {

    }

    @GetMapping("/sheet/general")
    public void queryOrderSheetGeneral() {

    }

}
