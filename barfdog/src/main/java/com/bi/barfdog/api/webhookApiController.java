package com.bi.barfdog.api;

import com.bi.barfdog.api.webhookDto.ChannelTalkRequestDto;
import com.bi.barfdog.api.webhookDto.WebHookRequestDto;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONValue;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class webhookApiController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ModelMapper modelMapper;

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

    @PostMapping("/channelTalk/webhook")
    public ResponseEntity channelTalkStartWebhook(@RequestBody Object object) {

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("jsonString = " + jsonString);

        JSONObject jsonObject = new JSONObject(jsonString);

        JSONObject refers = jsonObject.getJSONObject("refers");
        JSONObject user = refers.getJSONObject("user");
        JSONObject profile = user.getJSONObject("profile");
        String name = (String) profile.get("name");
        System.out.println("name = " + name);


        try {
            String mobileNumber = (String) profile.get("mobileNumber");
            System.out.println("mobileNumber = " + mobileNumber);

        } catch (JSONException e) {
            System.out.println("휴대전화 없음");

            String email = (String) profile.get("email");
            System.out.println("email = " + email);
            
        }




        return ResponseEntity.ok(null);
    }

}
