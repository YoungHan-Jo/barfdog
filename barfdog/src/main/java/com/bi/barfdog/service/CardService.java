package com.bi.barfdog.service;

import com.bi.barfdog.api.cardDto.ChangeCardDto;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.iamport.Iamport_API;
import com.bi.barfdog.repository.card.CardRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.request.UnscheduleData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final SubscribeRepository subscribeRepository;

    private IamportClient client = new IamportClient(Iamport_API.API_KEY, Iamport_API.API_SECRET);

    @Transactional
    public void changeCard(Member member, Long id, ChangeCardDto requestDto) {
        Subscribe subscribe = subscribeRepository.findById(id).get();

        String customerUid = requestDto.getCustomerUid();
        String cardName = requestDto.getCardName();
        String cardNumber = requestDto.getCardNumber();

        Card card = Card.builder()
                .member(member)
                .customerUid(customerUid)
                .cardName(cardName)
                .cardNumber(cardNumber)
                .build();
        cardRepository.save(card);

        subscribe.changeCard(card);
        unscheduleAndNewSchedule(subscribe);

    }



    private void unscheduleAndNewSchedule(Subscribe subscribe) {
        String customerUid = subscribe.getCard().getCustomerUid();
        String merchant_uid = subscribe.getNextOrderMerchant_uid();

        UnscheduleData unscheduleData = new UnscheduleData(customerUid);
        unscheduleData.addMerchantUid(merchant_uid);

        try {
            client.unsubscribeSchedule(unscheduleData);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sleepThread(500);

        Date nextPaymentDate = java.sql.Timestamp.valueOf(subscribe.getNextPaymentDate());
        int nextPaymentPrice = subscribe.getNextPaymentPrice() - subscribe.getDiscount();
        ScheduleData scheduleData = new ScheduleData(customerUid);
        scheduleData.addSchedule(new ScheduleEntry(merchant_uid, nextPaymentDate, BigDecimal.valueOf(nextPaymentPrice)));

        try {
            client.subscribeSchedule(scheduleData);
        } catch (IamportResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sleepThread(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
