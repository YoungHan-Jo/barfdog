package com.bi.barfdog.iamport;

import com.siot.IamportRestClient.Iamport;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.request.UnscheduleData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Schedule;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

public class IamportImplTest {

    @Autowired
    IamportImpl iamport = new IamportImpl();

    IamportClient client = new IamportClient(Iamport_API.API_KEY, Iamport_API.API_SECRET);




    @Test
    public void getToken() throws Exception {
       //given

       //when
        iamport.getToken();

       //then

    }

    @Test
    public void testCancelPaymentAlreadyCancelledImpUid() {
        String test_already_cancelled_imp_uid = "imp_448280090638";
        CancelData cancel_data = new CancelData(test_already_cancelled_imp_uid, true); //imp_uid를 통한 전액취소

        try {
            IamportResponse<Payment> payment_response = client.cancelPaymentByImpUid(cancel_data);

            assertNull(payment_response.getResponse()); // 이미 취소된 거래는 response가 null이다
        } catch (IamportResponseException e) {
            System.out.println(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    //TODO
                    break;
                case 500:
                    //TODO
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testCancelPaymentAlreadyCancelledMerchantUid() {
        String test_already_cancelled_merchant_uid = "merchant_1448280088556";
        CancelData cancel_data = new CancelData(test_already_cancelled_merchant_uid, false); //merchant_uid를 통한 전액취소
        cancel_data.setEscrowConfirmed(true); //에스크로 구매확정 후 취소인 경우 true설정

        try {
            IamportResponse<Payment> payment_response = client.cancelPaymentByImpUid(cancel_data);

            assertNull(payment_response.getResponse()); // 이미 취소된 거래는 response가 null이다
            System.out.println(payment_response.getMessage());
        } catch (IamportResponseException e) {
            System.out.println(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    //TODO
                    break;
                case 500:
                    //TODO
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testPartialCancelPaymentAlreadyCancelledImpUid() {
        String test_already_cancelled_imp_uid = "imp_448280090638";
        CancelData cancel_data = new CancelData(test_already_cancelled_imp_uid, true, BigDecimal.valueOf(500)); //imp_uid를 통한 500원 부분취소

        try {
            IamportResponse<Payment> payment_response = client.cancelPaymentByImpUid(cancel_data);

            System.out.println("payment_response = " + payment_response);
            Payment response = payment_response.getResponse();
            System.out.println("response = " + response);
            assertNull(payment_response.getResponse()); // 이미 취소된 거래는 response가 null이다
            System.out.println(payment_response.getMessage());
        } catch (IamportResponseException e) {
            System.out.println(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    //TODO
                    break;
                case 500:
                    //TODO
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testPartialCancelPaymentAlreadyCancelledMerchantUid() {
        String test_already_cancelled_merchant_uid = "merchant_1448280088556";
        CancelData cancel_data = new CancelData(test_already_cancelled_merchant_uid, false, BigDecimal.valueOf(500)); //merchant_uid를 통한 500원 부분취소

        try {
            IamportResponse<Payment> payment_response = client.cancelPaymentByImpUid(cancel_data);

            assertNull(payment_response.getResponse()); // 이미 취소된 거래는 response가 null이다
            System.out.println(payment_response.getMessage());
        } catch (IamportResponseException e) {
            System.out.println(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    //TODO
                    break;
                case 500:
                    //TODO
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testSubscribeScheduleAndUnschedule() {
        String test_customer_uid = "customer_123456";
        ScheduleData schedule_data = new ScheduleData(test_customer_uid); // customer_uid 장착

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.OCTOBER);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        Date d1 = cal.getTime();

        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.NOVEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        Date d2 = cal.getTime();

        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        Date d3 = cal.getTime();

        schedule_data.addSchedule(new ScheduleEntry(getRandomMerchantUid(), d1, BigDecimal.valueOf(1004))); // 예약 정보 추가
        schedule_data.addSchedule(new ScheduleEntry(getRandomMerchantUid(), d2, BigDecimal.valueOf(1005)));
        schedule_data.addSchedule(new ScheduleEntry(getRandomMerchantUid(), d3, BigDecimal.valueOf(1006)));

        System.out.println("예약 요청");
        IamportResponse<List<Schedule>> schedule_response = null;
        try {
            schedule_response = client.subscribeSchedule(schedule_data); // 예약 하기
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Schedule> schedules = schedule_response.getResponse();
        List<ScheduleEntry> req_schedules = schedule_data.getSchedules();

        for (int i = 0; i < 3; i++) {
            assertThat(test_customer_uid).isEqualTo(schedules.get(i).getCustomerUid());
            assertEquals(schedules.get(i).getMerchantUid(), req_schedules.get(i).getMerchantUid());
            assertThat(req_schedules.get(i).getScheduleAt());
            Date scheduleAt = req_schedules.get(i).getScheduleAt();
            assertThat(scheduleAt).isEqualTo(schedules.get(i).getScheduleAt());
            assertEquals(schedules.get(i).getAmount(), req_schedules.get(i).getAmount());
        }

        try {
            //1초 후 등록된 예약 unschedule by multiple merchant_uid
            Thread.sleep(1000);
            System.out.println("복수 merchant_uid 예약 취소 요청");
            UnscheduleData unschedule_data = new UnscheduleData(test_customer_uid);
            unschedule_data.addMerchantUid( req_schedules.get(0).getMerchantUid() );
            unschedule_data.addMerchantUid( req_schedules.get(2).getMerchantUid() );

            IamportResponse<List<Schedule>> unschedule_response = null;
            try {
                unschedule_response = client.unsubscribeSchedule(unschedule_data);
            } catch (IamportResponseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Schedule> cancelled_schedule = unschedule_response.getResponse();

            assertNotNull(cancelled_schedule);
            assertEquals(cancelled_schedule.get(0).getMerchantUid(), req_schedules.get(0).getMerchantUid());
            assertEquals(cancelled_schedule.get(1).getMerchantUid(), req_schedules.get(2).getMerchantUid());

            //1초 후 등록된 예약 unschedule by single multiple_uid
            Thread.sleep(1000);
            System.out.println("단일 merchant_uid 예약 취소 요청");
            unschedule_data = new UnscheduleData(test_customer_uid);
            unschedule_data.addMerchantUid( req_schedules.get(1).getMerchantUid());

            try {
                unschedule_response = client.unsubscribeSchedule(unschedule_data);
            } catch (IamportResponseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cancelled_schedule = unschedule_response.getResponse();

            assertNotNull(cancelled_schedule);
            assertEquals(cancelled_schedule.get(0).getMerchantUid(), req_schedules.get(1).getMerchantUid());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getRandomMerchantUid() {
        DateFormat df = new SimpleDateFormat("$$hhmmssSS");
        int n = (int) (Math.random() * 100) + 1;

        return df.format(new Date()) + "_" + n;
    }


}