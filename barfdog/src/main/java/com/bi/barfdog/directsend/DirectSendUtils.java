package com.bi.barfdog.directsend;

import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.delivery.Recipient;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.dog.DogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class DirectSendUtils {

    private final DogRepository dogRepository;

    public static DirectSendResponseDto sendSmsDirect(String title, String message, String phoneNumber) throws IOException {
        String url = "https://directsend.co.kr/index.php/api_v2/sms_change_word";		// URL

        java.net.URL obj;
        obj = new java.net.URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        con.setRequestProperty("Accept", "application/json");

        /* 여기서부터 수정해주시기 바랍니다. */

        String sender = DirectSend.SENDER;        //필수입력
        String username = DirectSend.USERNAME;    //필수입력
        String key = DirectSend.API_KEY;          //필수입력

        //수신자 정보 추가 - 필수 입력(주소록 미사용시), 치환문자 미사용시 치환문자 데이터를 입력하지 않고 사용할수 있습니다.
        //치환문자 미사용시 {\"mobile\":\"01000000001\"} 번호만 입력 해주시기 바랍니다.
        String receiver = "{\"name\": \"휴대폰인증\", \"mobile\":\"" +phoneNumber +"\", \"note1\":\"휴대폰 인증 내용\"}";
        System.out.println("receiver = " + receiver);

        receiver = "["+ receiver +"]";

        String urlParameters = "\"title\":\"" + title + "\" "
                + ", \"message\":\"" + message + "\" "
                + ", \"sender\":\"" + sender + "\" "
                + ", \"username\":\"" + username + "\" "
                + ", \"receiver\":" + receiver
                + ", \"key\":\"" + key + "\" "
                + ", \"type\":\"" + "java" + "\" ";
        //+ ", \"attaches\":\"" + attaches + "\" ";	// 첨부파일이 있는 경우 주석해제 바랍니다.
        urlParameters = "{"+ urlParameters  +"}";		//JSON 데이터

        System.setProperty("jsse.enableSNIExtension", "false");
        con.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter (con.getOutputStream());
        wr.write(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println(responseCode);

        /*
         * responseCode 가 200 이 아니면 내부에서 문제가 발생한 케이스입니다.
         * directsend 관리자에게 문의해주시기 바랍니다.
         */

        java.io.BufferedReader in = new java.io.BufferedReader(
                new java.io.InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        System.out.println("결과 : " + response.toString());

        DirectSendResponseDto responseDto = getDirectSendResponseDto(response, responseCode);

        return responseDto;
    }

    public static void sendCouponAlim(List<MemberCoupon> memberCouponList) throws IOException {
        if (isCodePublished(memberCouponList)) {
            sendAlimTalk(DirectSend.CODE_PUBLISH_TEMPLATE_TEST, getReceiverOfCoupon(memberCouponList));
        } else if (isGeneralPublished(memberCouponList)) {
            sendAlimTalk(DirectSend.GENERAL_PUBLISH_TEMPLATE_TEST, getReceiverOfCoupon(memberCouponList));
        }
    }

    public static void sendCodeCouponPublishAlim(List<CodeCouponAlimDto> codeCouponAlimDtoList) throws IOException {

        String receiver = "";

        for (CodeCouponAlimDto codeCouponAlimDto : codeCouponAlimDtoList) {

            receiver += ",{\"name\": \"" + codeCouponAlimDto.getName() + "\", " +
                    "\"mobile\":\"" + codeCouponAlimDto.getPhone() + "\", " +
                    "\"note1\":\"" + codeCouponAlimDto.getDogName() + "\"," +
                    "\"note2\":\"" + codeCouponAlimDto.getCouponName() + "\"," +
                    "\"note3\":\"" + codeCouponAlimDto.getCode() + "\"}";
        }

        if (codeCouponAlimDtoList.size() > 0) {
            receiver = receiver.substring(1);
        }



        sendAlimTalk(DirectSend.CODE_COUPON_TEMPLATE, receiver);
    }

    public static FriendTalkResponseDto sendFriendTalk(String templateNumber, List<Member> memberList) throws IOException {

        String receiver = "";

        for (Member member : memberList) {
            receiver += ",{\"name\": \"" + member.getName() + "\", " +
                    "\"mobile\":\"" + member.getPhoneNumber() + "\"}";
        }

        if (memberList.size() > 0) {
            receiver = receiver.substring(1);
        }

        FriendTalkResponseDto responseDto = sendFriendTalk(templateNumber, receiver);

        return responseDto;
    }








    public static void sendTomorrowPaymentAlim(Subscribe subscribe,SubscribeOrder order) throws IOException {

        Dog dog = subscribe.getDog();
        Member member = dog.getMember();
        String dogName = dog.getName();
        String itemName = "정기 구독 상품";

        String paymentMethod = getPaymentMethod(order);


        String paymentPrice = order.getPaymentPrice() + "원";

        String nextPaymentDate = order.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String delivery = getDeliveryInfo(order);


        String receiver = "";

        receiver += ",{\"name\": \"" + dogName + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + itemName + "\"," +
                "\"note2\":\"" + paymentMethod + "\"," +
                "\"note3\":\"" + paymentPrice + "\"," +
                "\"note4\":\"" + nextPaymentDate + "\"," +
                "\"note5\":\"" + delivery + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.TOMORROW_PAYMENT_TEMPLATE, receiver);
    }

    private static String getPaymentMethod(SubscribeOrder order) {
        String paymentMethod = "";

        switch (order.getPaymentMethod()) {
            case CREDIT_CARD:
                paymentMethod = "신용카드";
            case KAKAO_PAY:
                paymentMethod = "카카오페이";
            case NAVER_PAY:
                paymentMethod = "네이버페이";
            default:
                paymentMethod = "기타 결제";
        }
        return paymentMethod;
    }



    public static void sendGeneralOrderSuccessAlim(Order order, String dogName, List<OrderItem> orderItemList) throws IOException {

        Member member = order.getMember();
        String itemName = getItemNames(orderItemList);

        String orderDate = getOrderDate(order);

        String delivery = getDeliveryInfo(order);

        String receiver = "";

        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dogName + "\"," +
                "\"note2\":\"" + orderDate + "\"," +
                "\"note3\":\"" + itemName + "\"," +
                "\"note4\":\"" + order.getPaymentPrice() + "\"," +
                "\"note5\":\"" + delivery + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.ORDER_SUCCESS_TEMPLATE, receiver);
    }

    private static String getItemNames(List<OrderItem> orderItemList) {
        String itemName ="";
        if (orderItemList.size() > 0) {
            OrderItem orderItem = orderItemList.get(0);
            itemName += orderItem.getItem().getName();
            if (orderItemList.size() > 1) {
                int i = orderItemList.size() - 1;
                itemName += " 외 " + i +"건";
            }
        }
        return itemName;
    }

    private static String getDeliveryInfo(Order order) {
        Recipient recipient = order.getDelivery().getRecipient();
        String street = recipient.getStreet();
        String detailAddress = recipient.getDetailAddress();
        String delivery = street + ", " + detailAddress;
        return delivery;
    }

    public static void sendSubscribeOrderProducingAlim(Order order) throws IOException {

        Member member = order.getMember();
        String itemName ="정기 구독 상품";

        String orderDate = getOrderDate(order);

        SubscribeOrder subscribeOrder = (SubscribeOrder) order;
        Dog dog = subscribeOrder.getSubscribe().getDog();

        String delivery = getDeliveryInfo(order);

        String receiver = "";

        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dog.getName() + "\"," +
                "\"note2\":\"" + orderDate + "\"," +
                "\"note3\":\"" + itemName + "\"," +
                "\"note4\":\"" + order.getPaymentPrice() + "\"," +
                "\"note5\":\"" + delivery + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.ORDER_PRODUCING_TEMPLATE, receiver);
    }

    public static void sendGeneralOrderDeliveryReadyAlim(Order order, String dogName, String itemName) throws IOException {

        Member member = order.getMember();

        String orderDate = getOrderDate(order);
        String delivery = getDeliveryInfo(order);

        String receiver = "";
        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dogName + "\"," +
                "\"note2\":\"" + orderDate + "\"," +
                "\"note3\":\"" + itemName + "\"," +
                "\"note4\":\"" + order.getPaymentPrice() + "\"," +
                "\"note5\":\"" + delivery + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.ORDER_DELIVERY_READY_TEMPLATE, receiver);
    }

    public static void sendReturnAlim(Member member, String dogName, String itemName) throws IOException {

        String receiver = "";
        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dogName + "\"," +
                "\"note2\":\"" + itemName + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.RETURN_TEMPLATE, receiver);
    }

    public static void sendExchangeAlim(Member member, String dogName, String itemName) throws IOException {

        String receiver = "";
        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dogName + "\"," +
                "\"note2\":\"" + itemName + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.EXCHANGE_TEMPLATE, receiver);
    }



    public static void sendGeneralOrderDeliveryStartAlim(Order order, String dogName, List<OrderItem> orderItemList) throws IOException {

        Member member = order.getMember();
        String itemName = getItemNames(orderItemList);

        String receiver = "";
        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dogName + "\"," +
                "\"note2\":\"" + order.getMerchantUid() + "\"," +
                "\"note3\":\"" + itemName + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.DELIVERY_START_TEMPLATE, receiver);
    }


    public static void sendSubscribeOrderDeliveryStartAlim(Order order) throws IOException {

        Member member = order.getMember();
        String itemName = "정기 구독 상품";

        SubscribeOrder subscribeOrder = (SubscribeOrder) order;
        String dogName = subscribeOrder.getSubscribe().getDog().getName();

        String receiver = "";
        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dogName + "\"," +
                "\"note2\":\"" + order.getMerchantUid() + "\"," +
                "\"note3\":\"" + itemName + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.DELIVERY_START_TEMPLATE, receiver);
    }

    public static void sendSubscribePaymentScheduleFailAlim(Order order) throws IOException {

        Member member = order.getMember();

        SubscribeOrder subscribeOrder = (SubscribeOrder) order;
        String dogName = subscribeOrder.getSubscribe().getDog().getName();

        String receiver = "";
        receiver += ",{\"name\": \"" + dogName + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.SUBSCRIBE_PAYMENT_SCHEDULE_FAIL, receiver);
    }

    private static String getOrderDate(Order order) {
        LocalDateTime createdDate = order.getCreatedDate();
        String orderDate = createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return orderDate;
    }

    public static void sendSubscribeOrderSuccessAlim(Order order) throws IOException {

        Member member = order.getMember();
        String itemName ="정기 구독 상품";

        String orderDate = getOrderDate(order);

        SubscribeOrder subscribeOrder = (SubscribeOrder) order;
        Dog dog = subscribeOrder.getSubscribe().getDog();

        String delivery = getDeliveryInfo(order);

        String receiver = "";

        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dog.getName() + "\"," +
                "\"note2\":\"" + orderDate + "\"," +
                "\"note3\":\"" + itemName + "\"," +
                "\"note4\":\"" + order.getPaymentPrice() + "\"," +
                "\"note5\":\"" + delivery + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.ORDER_SUCCESS_TEMPLATE, receiver);
    }


    public static void sendSubscribeOrderCancelAlim(Order order) throws IOException {

        Member member = order.getMember();
        String itemName ="정기 구독 상품";

        SubscribeOrder subscribeOrder = (SubscribeOrder) order;
        Dog dog = subscribeOrder.getSubscribe().getDog();

        String receiver = "";

        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dog.getName() + "\"," +
                "\"note2\":\"" + itemName + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.ORDER_CANCEL_TEMPLATE, receiver);
    }

    public static void sendGeneralOrderCancelAlim(Order order, String dogName, List<OrderItem> orderItems) throws IOException {

        Member member = order.getMember();

        String itemNames = getItemNames(orderItems);

        String receiver = "";

        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + dogName + "\"," +
                "\"note2\":\"" + itemNames + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.ORDER_CANCEL_TEMPLATE, receiver);
    }


    public static void sendGradeAlim(Member member, Grade beforeGrade, Grade newGrade) throws IOException {

        String receiver = "";

        receiver += ",{\"name\": \"" + member.getName() + "\", " +
                "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                "\"note1\":\"" + beforeGrade.toString() + "\"," +
                "\"note2\":\"" + newGrade.toString() + "\"}";

        receiver = receiver.substring(1);

        sendAlimTalk(DirectSend.GRADE_TEMPLATE, receiver);
    }












    private static boolean isGeneralPublished(List<MemberCoupon> memberCouponList) {
        return memberCouponList.get(0).getCoupon().getCouponType() == CouponType.GENERAL_PUBLISHED;
    }

    private static boolean isCodePublished(List<MemberCoupon> memberCouponList) {
        return memberCouponList.get(0).getCoupon().getCouponType() == CouponType.CODE_PUBLISHED;
    }

    private static void sendAlimTalk(String templateNumber, String receiver) throws IOException {
        /* 여기서부터 수정해주시기 바랍니다. */

        String username = DirectSend.USERNAME;                //필수입력
        String key = DirectSend.API_KEY;         //필수입력
        String kakao_plus_id = DirectSend.KAKAO_PLUS_ID;            //필수입력 // @검색용 아이디
        String user_template_no = templateNumber;            //필수입력 (하단 290 라인 API 이용하여 확인)

        receiver = "["+ receiver +"]";

        String postvars = "";
        postvars = "\"username\":\""+username+"\"";             //필수입력
        postvars = postvars+", \"key\":\""+key+"\"";           //필수입력
        postvars = postvars+", \"type\":\"java\"";           //필수입력
        postvars = postvars+", \"kakao_plus_id\":\""+kakao_plus_id+"\"";       //필수입력
        postvars = postvars+", \"user_template_no\":\""+user_template_no+"\"";       //필수입력
        postvars = postvars+", \"receiver\":"+ receiver;            //주소록 사용하지 않는 경우 필수입력, json array 구조
        postvars = "{"+postvars+"}";      //JSON 데이터

        String url = "https://directsend.co.kr/index.php/api_v2/kakao_notice";         //URL

        URL obj;
        obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        con.setRequestProperty("Accept", "application/json");

        System.setProperty("jsse.enableSNIExtension", "false");
        con.setDoOutput(true);
        OutputStreamWriter  wr = new OutputStreamWriter (con.getOutputStream());
        wr.write(postvars);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println(responseCode);

        /*
         * responseCode 가 200 이 아니면 내부에서 문제가 발생한 케이스입니다.
         * directsend 관리자에게 문의해주시기 바랍니다.
         */

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
    }

    private static FriendTalkResponseDto sendFriendTalk(String templateNumber, String receiver) throws IOException{

        String username = DirectSend.USERNAME;                //필수입력
        String key = DirectSend.API_KEY;         //필수입력
        String kakao_plus_id = DirectSend.KAKAO_PLUS_ID;            //필수입력
        String user_template_no = templateNumber;            //템플릿 번호를 사용하지 않을 경우 주석처리 바랍니다. (하단 290 라인 API 이용하여 확인)

        //수신자 정보 추가 - 필수 입력(주소록 미사용시), 치환문자 미사용시 치환문자 데이터를 입력하지 않고 사용할수 있습니다.
        //치환문자 미사용시 "{"mobile":"01000000001"} 번호만 입력 해주시기 바랍니다.
        receiver = "["+ receiver +"]";

        String postvars = "";
        postvars = "\"username\":\""+username+"\"";             //필수입력
        postvars = postvars+", \"key\":\""+key+"\"";           //필수입력
        postvars = postvars+", \"type\":\"java\"";           //필수입력
        postvars = postvars+", \"kakao_plus_id\":\""+kakao_plus_id+"\"";       //필수입력
        postvars = postvars+", \"user_template_no\":\""+user_template_no+"\"";       //템플릿 번호를 사용하지 않을 경우 주석처리 바랍니다.
        postvars = postvars+", \"receiver\":"+receiver;            //주소록 사용하지 않는 경우 필수입력, json array 구조
        postvars = "{"+postvars+"}";      //JSON 데이터

        String url = "https://directsend.co.kr/index.php/api_v2/kakao_friend";         //URL

        java.net.URL obj;
        obj = new java.net.URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        con.setRequestProperty("Accept", "application/json");

        System.setProperty("jsse.enableSNIExtension", "false");
        con.setDoOutput(true);
        OutputStreamWriter  wr = new OutputStreamWriter (con.getOutputStream());
        wr.write(postvars);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println(responseCode);

        /*
         * responseCode 가 200 이 아니면 내부에서 문제가 발생한 케이스입니다.
         * directsend 관리자에게 문의해주시기 바랍니다.
         */

        java.io.BufferedReader in = new java.io.BufferedReader(
                new java.io.InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        System.out.println("response : " + response.toString());

        FriendTalkResponseDto responseDto = getFriendTalkResponseDto(response, responseCode);

        return responseDto;
    }






    private static String getReceiverOfCoupon(List<MemberCoupon> memberCouponList) {
        String receiver = "";
        for (MemberCoupon memberCoupon : memberCouponList) {
            Member member = memberCoupon.getMember();
            Coupon coupon = memberCoupon.getCoupon();
            CouponType couponType = memberCoupon.getCoupon().getCouponType();
            String discountTypeStr = coupon.getDiscountType() == DiscountType.FLAT_RATE ? "원" : "%";

            if (couponType == CouponType.CODE_PUBLISHED){
                receiver += ",{\"name\": \"" + member.getName() + "\", " +
                        "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                        "\"note1\":\"" + coupon.getCode() + "\"," +
                        "\"note2\":\"" + memberCoupon.getExpiredDate() + "\"}";
            } else if (couponType == CouponType.GENERAL_PUBLISHED) {
                receiver += ",{\"name\": \"" + member.getName() + "\", " +
                        "\"mobile\":\"" + member.getPhoneNumber() + "\", " +
                        "\"note1\":\"" + coupon.getDiscountDegree() + discountTypeStr + "\"," +
                        "\"note2\":\"" + memberCoupon.getExpiredDate() + "\"}";
            }
        }

        receiver = receiver.substring(1);
        return receiver;
    }









    public static DirectSendResponseDto sendEmailDirect(String title, String contents, String email) throws Exception {

        // URL
        String url = "https://directsend.co.kr/index.php/api_v2/mail_change_word";

        java.net.URL obj;
        obj = new java.net.URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        con.setRequestProperty("Accept", "application/json");

        // 여기서부터 수정해주시기 바랍니다.

        String subject = title;   //필수입력(템플릿 사용시 23 line 설명 참조)
        String body = contents;		//필수입력, 템플릿 사용시 빈값을 입력 하시기 바랍니다. 예시) String body = "";
        String sender = "info@freshour.co.kr";        //필수입력
        String sender_name = "바프독";
        String username = DirectSend.USERNAME;              //필수입력
        String key = DirectSend.API_KEY;           //필수입력

        //수신자 정보 추가 - 필수 입력(주소록 미사용시), 치환문자 미사용시 치환문자 데이터를 입력하지 않고 사용할수 있습니다.
        //치환문자 미사용시 {\"email\":\"aaaa@naver.com\"} 이메일만 입력 해주시기 바랍니다.
        String receiver = "{\"name\": \"고객님\", \"email\":\""+email+"\", \"mobile\":\"\", \"note1\":\"\", \"note2\":\"\", \"note3\":\"\", \"note4\":\"\", \"note5\":\"\"}";

        receiver = "["+ receiver +"]";

        /* 여기까지 수정해주시기 바랍니다. */

        String urlParameters = "\"subject\":\"" + subject + "\" "
                + ", \"body\":\"" + body + "\" "
                + ", \"sender\":\"" + sender + "\" "
                + ", \"sender_name\":\"" + sender_name + "\" "
                + ", \"username\":\"" + username + "\" "
                + ", \"receiver\":" + receiver
                + ", \"key\":\"" + key + "\" ";
        urlParameters = "{"+ urlParameters  +"}";		//JSON 데이터


        System.setProperty("jsse.enableSNIExtension", "false");
        con.setDoOutput(true);
        OutputStreamWriter  wr = new OutputStreamWriter (con.getOutputStream());
        wr.write(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println(responseCode);

        /*
         * responseCode 가 200 이 아니면 내부에서 문제가 발생한 케이스입니다.
         * directsend 관리자에게 문의해주시기 바랍니다.
         */

        java.io.BufferedReader in = new java.io.BufferedReader(
                new java.io.InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        System.out.println(response.toString());

        DirectSendResponseDto responseDto = getDirectSendResponseDto(response, responseCode);

        return responseDto;
    }

    private static DirectSendResponseDto getDirectSendResponseDto(StringBuffer response, int responseCode) throws JsonProcessingException {
        String responseString = response.toString();

        ObjectMapper objectMapper = new ObjectMapper();
        DirectSendResponseDto responseDto = objectMapper.readValue(responseString, DirectSendResponseDto.class);

        responseDto.setResponseCode(responseCode);
        return responseDto;
    }

    private static FriendTalkResponseDto getFriendTalkResponseDto(StringBuffer response,int responseCode) throws JsonProcessingException {

        String responseString = response.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        FriendTalkResponseDto responseDto = objectMapper.readValue(responseString, FriendTalkResponseDto.class);

        responseDto.setResponseCode(responseCode);
        return responseDto;
    }


}
