package com.bi.barfdog.directsend;

import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponType;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.*;
import java.io.*;
import java.net.*;

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

        // 예약발송 정보 추가
        String sms_type = "NORMAL"; // NORMAL - 즉시발송 / ONETIME - 1회예약 / WEEKLY - 매주정기예약 / MONTHLY - 매월정기예약
//        String start_reserve_time = "2019-03-08 12:11:00";// 발송하고자 하는 시간
//        String end_reserve_time = "2019-03-08 12:11:00";// 발송이 끝나는 시간 1회 예약일 경우 start_reserve_time = end_reserve_time
        // WEEKLY | MONTHLY 일 경우에 시작 시간부터 끝나는 시간까지 발송되는 횟수 Ex) type = WEEKLY, start_reserve_time = '2017-05-17 13:00:00', end_reserve_time = '2017-05-24 13:00:00' 이면 remained_count = 2 로 되어야 합니다.
        int remained_count = 1;
        // 예약 수정/취소 API는 소스 하단을 참고 해주시기 바랍니다.

        // 실제 발송성공실패 여부를 받기 원하실 경우 아래 주석을 해제하신 후, 사이트에 등록한 URL 번호를 입력해 주시기 바랍니다.
        //int returnURL = 0;

        // 첨부파일이 있을 시 아래 주석을 해제하고 첨부하실 파일의 URL을 배열로 입력하여 주시기 바랍니다.
        // jpg파일당 300kb 제한 3개까지 가능합니다.
        //String attaches = "https://directsend.co.kr/jpgimg1.jpg,https://directsend.co.kr/jpgimg2.jpg,https://directsend.co.kr/jpgimg3.jpg";

        /* 여기까지만 수정해주시기 바랍니다. */

        String urlParameters = "\"title\":\"" + title + "\" "
                + ", \"message\":\"" + message + "\" "
                + ", \"sender\":\"" + sender + "\" "
                + ", \"username\":\"" + username + "\" "
                + ", \"receiver\":" + receiver
                //+ ", \"address_books\":\"" + address_books + "\" " 

                // 예약 관련 파라미터 주석 해제
                //+ ", \"sms_type\":\"" + sms_type + "\" "
                //+ ", \"start_reserve_time\":\"" + start_reserve_time + "\" "
                //+ ", \"end_reserve_time\":\"" + end_reserve_time + "\" "
                //+ ", \"remained_count\":\"" + remained_count + "\" "

                //+ ", \"return_url_yn\": " + true		//returnURL이 있는 경우 주석해제 바랍니다.(필수입력)
                //+ ", \"return_url\":\"" + returnURL  + "\" " // returnURL이 있는 경우 주석해제 바랍니다.
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

        String responseString = response.toString();

        ObjectMapper objectMapper = new ObjectMapper();
        DirectSendResponseDto responseDto = objectMapper.readValue(responseString, DirectSendResponseDto.class);

        responseDto.setResponseCode(responseCode);

        return responseDto;
    }

    public static void sendCouponAlim(List<MemberCoupon> memberCouponList) throws IOException {

        if (memberCouponList.get(0).getCoupon().getCouponType() == CouponType.CODE_PUBLISHED) {
            /* 여기서부터 수정해주시기 바랍니다. */

            String username = DirectSend.USERNAME;                //필수입력
            String key = DirectSend.API_KEY;         //필수입력
            String kakao_plus_id = DirectSend.KAKAO_PLUS_ID;            //필수입력 // @검색용 아이디
            String user_template_no = DirectSend.CODE_PUBLISH_TEMPLATE;            //필수입력 (하단 290 라인 API 이용하여 확인)

            //수신자 정보 추가 - 필수 입력(주소록 미사용시), 치환문자 미사용시 치환문자 데이터를 입력하지 않고 사용할수 있습니다.
            //치환문자 미사용시 "{"mobile":"01000000001"} 번호만 입력 해주시기 바랍니다.
            String receiver = "";

            for (MemberCoupon memberCoupon : memberCouponList) {
                Member member = memberCoupon.getMember();
                Coupon coupon = memberCoupon.getCoupon();
                receiver += ",{\"name\": \"" + member.getName() + "\", \"mobile\":\"" + member.getPhoneNumber() + "\", \"note1\":\"" + coupon.getCode() + "\",\"note2\":\"" + memberCoupon.getExpiredDate() + "\"}";
            }

            receiver = receiver.substring(1);

            receiver = "["+ receiver +"]";

            // 주소록을 사용하길 원하실 경우 아래 주석을 해제하신 후, 사이트에 등록한 주소록 번호를 입력해주시기 바랍니다.
//		String address_books = "0,1,2";      //사이트에 등록한 발송 할 주소록 번호 , 로 구분함 (ex. 0,1,2)

            // 대체문자 정보 추가
            String kakao_faild_type = "1";          // 1 : 대체문자(SMS) / 2 : 대체문자(LMS) / 3 : 대체문자(MMS) 대체문자 사용시 필수 입력
            String title = "대체문자 SMS 제목입니다.";
            String message = "[$NAME]님 알림 SMS 문자 입니다. 전화번호 : [$MOBILE] 비고1 : [$NOTE1] 비고2 : [$NOTE2] 비고3 : [$NOTE3] 비고4 : [$NOTE4] 비고5 : [$NOTE5]";             //대체문자 사용시 필수입력
            String sender = "발신자번호";                    //대체문자 사용시 필수입력

            // 예약발송 정보 추가
            String reserve_type = "NORMAL"; // NORMAL - 즉시발송 / ONETIME - 1회예약 / WEEKLY - 매주정기예약 / MONTHLY - 매월정기예약
            String start_reserve_time = "2019-08-23 10:00:00"; //  발송하고자 하는 시간(시,분단위까지만 가능) (동일한 예약 시간으로는 200회 이상 API 호출을 할 수 없습니다.)
            String end_reserve_time = "2019-08-23 10:00:00"; //  발송이 끝나는 시간 1회 예약일 경우 $start_reserve_time = $end_reserve_time
            // WEEKLY | MONTHLY 일 경우에 시작 시간부터 끝나는 시간까지 발송되는 횟수 Ex) type = WEEKLY, start_reserve_time = '2019-08-23 10:00:00', end_reserve_time = '2019-08-30 10:00:00' 이면 remained_count = 2 로 되어야 합니다.
            int remained_count = 1;
            // 예약 수정/취소 API는 소스 하단을 참고 해주시기 바랍니다.

            // 실제 발송성공실패 여부를 받기 원하실 경우 아래 주석을 해제하신 후, 사이트에 등록한 URL 번호를 입력해 주시기 바랍니다.
            boolean return_url_yn = true;        //return_url 사용시 필수 입력
            int return_url = 0;

            /* 여기까지 수정해주시기 바랍니다. */

            // 첨부파일이 있을 시 아래 주석을 해제하고 첨부하실 파일의 URL을 입력하여 주시기 바랍니다.
            // jpg파일당 300kb 제한 3개까지 가능합니다.
            String attaches = "https://directsend.co.kr/jpgimg1.jpg,https://directsend.co.kr/jpgimg2.jpg,https://directsend.co.kr/jpgimg3.jpg";

            String postvars = "";
            postvars = "\"username\":\""+username+"\"";             //필수입력
            postvars = postvars+", \"key\":\""+key+"\"";           //필수입력
            postvars = postvars+", \"type\":\"java\"";           //필수입력
            postvars = postvars+", \"kakao_plus_id\":\""+kakao_plus_id+"\"";       //필수입력
            postvars = postvars+", \"user_template_no\":\""+user_template_no+"\"";       //필수입력
            postvars = postvars+", \"receiver\":"+receiver;            //주소록 사용하지 않는 경우 필수입력, json array 구조
            //postvars = postvars+", \"address_books\":\""+address_books+"\"";       //주소록 사용할 경우 주석 해제 바랍니다+
//		postvars = postvars+", \"kakao_faild_type\":\""+kakao_faild_type+"\""; //대체문자 사용시 주석해제 바랍니다+
//		postvars = postvars+", \"title\":\""+title+"\"";           //대체문자 사용시 주석해제 바랍니다+
//		postvars = postvars+", \"message\":\""+message+"\"";       //대체문자 사용시 주석해제 바랍니다
//		postvars = postvars+", \"sender\":\""+sender+"\"";         //대체문자 사용시 주석해제 바랍니다
            //postvars = postvars+", \"reserve_type\":\""+reserve_type+"\"";                 //예약 관련 정보 사용할 경우 주석 해제 바랍니다+
            //postvars = postvars+", \"start_reserve_time\":\""+start_reserve_time+"\"";     //예약 관련 정보 사용할 경우 주석 해제 바랍니다+
            //postvars = postvars+", \"end_reserve_time\":\""+end_reserve_time+"\"";         //예약 관련 정보 사용할 경우 주석 해제 바랍니다+
            //postvars = postvars+", \"remained_count\":\""+remained_count+"\"";             //예약 관련 정보 사용할 경우 주석 해제 바랍니다+
            //postvars = postvars+", \"return_url_yn\":"+return_url_yn;          //return_url이 있는 경우 주석해제 바랍니다+
            //postvars = postvars+", \"return_url\":\""+return_url+"\" ";        //return_url이 있는 경우 주석해제 바랍니다+
            //postvars = postvars+", \"attaches\":\""+ attaches +"\" ";   //첨부파일이 있는 경우 주석해제 바랍니다+
            postvars = "{"+postvars+"}";      //JSON 데이터

            String url = "https://directsend.co.kr/index.php/api_v2/kakao_notice";         //URL

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

            System.out.println(response.toString());
        }
    }
}
