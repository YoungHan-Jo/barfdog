package com.bi.barfdog.directsend;

import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DirectSendUtils {

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
            sendAlimTalk(DirectSend.CODE_PUBLISH_TEMPLATE, getReceiverOfCoupon(memberCouponList));
        } else if (isGeneralPublished(memberCouponList)) {
            sendAlimTalk(DirectSend.GENERAL_PUBLISH_TEMPLATE, getReceiverOfCoupon(memberCouponList));
        }
    }

    private static boolean isGeneralPublished(List<MemberCoupon> memberCouponList) {
        return memberCouponList.get(0).getCoupon().getCouponType() == CouponType.GENERAL_PUBLISHED;
    }

    private static boolean isCodePublished(List<MemberCoupon> memberCouponList) {
        return memberCouponList.get(0).getCoupon().getCouponType() == CouponType.CODE_PUBLISHED;
    }

    private static void sendAlimTalk( String templateNumber, String receiver) throws IOException {
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

}
