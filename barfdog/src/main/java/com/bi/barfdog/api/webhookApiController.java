package com.bi.barfdog.api;

import com.bi.barfdog.api.webhookDto.ChannelTalkRequestDto;
import com.bi.barfdog.api.webhookDto.WebHookRequestDto;
import com.bi.barfdog.domain.guest.Guest;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.repository.guest.GuestRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.service.GuestService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class webhookApiController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final GuestService guestService;
    private final GuestRepository guestRepository;
    private final MemberRepository memberRepository;

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

        String jsonString = getJsonString(object);
        JSONObject profile = getProfile(jsonString);
        if (profile == null) return notFound();
        String name = getName(profile);
        String phoneNumber = getMobileNumber(profile);
        String email = getEmail(profile);

        if (phoneNumber != null) {
            Optional<Guest> optionalGuest = guestRepository.findByPhoneNumber(phoneNumber);
            if (optionalGuest.isPresent()) return new ResponseEntity(HttpStatus.CONFLICT);

            System.out.println("이미 등록된 전화번호");
        }

        if (email != null) {
            Optional<Guest> optionalGuest = guestRepository.findByEmail(email);
            if (optionalGuest.isPresent()) return new ResponseEntity(HttpStatus.CONFLICT);

            System.out.println("이미 등록된 이메일");
        }

        guestService.createGuest(name, phoneNumber, email);

        return ResponseEntity.ok(null);
    }



    private String getJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    private JSONObject getProfile(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONObject refers = jsonObject.getJSONObject("refers");
        JSONObject user = refers.getJSONObject("user");
        JSONObject profile = null;
        try {
            profile = user.getJSONObject("profile");
        } catch (JSONException e) {
            System.out.println("프로필 존재하지 않음");
        }
        return profile;
    }

    private String getName(JSONObject profile) {
        String name = null;

        try {
            name = (String) profile.get("name");
            System.out.println("name = " + name);
        } catch (JSONException e) {
            System.out.println("이름 없음");
        }
        return name;
    }

    private String getMobileNumber(JSONObject profile) {
        String mobileNumber = null;

        try {
            mobileNumber = (String) profile.get("mobileNumber");

            mobileNumber = 0 + mobileNumber.substring(3);
            System.out.println("mobileNumber = " + mobileNumber);
        } catch (JSONException e) {
            System.out.println("휴대전화 없음");
        }
        return mobileNumber;
    }

    private String getEmail(JSONObject profile) {
        String email = null;

        try {
            email = (String) profile.get("email");
            System.out.println("email = " + email);
        } catch (JSONException e) {
            System.out.println("이메일 없음");
        }
        return email;
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
