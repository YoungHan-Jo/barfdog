package com.bi.barfdog.domain.delivery;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Builder
@Entity
public class Delivery extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    private String transUniqueCd; // 굿스플로에 필요한 키값

    private String deliveryNumber; // 운송장번호

    @Embedded
    private Recipient recipient; // 수령자 정보

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // 배송상태

    private String request; // 요청사항

    private LocalDateTime departureDate; // 출발일
    private LocalDateTime arrivalDate; // 도착일

    private LocalDate nextDeliveryDate; // 배송 예정 일자


    public void deliveryDone() {
        status = DeliveryStatus.DELIVERY_DONE;
        arrivalDate = LocalDateTime.now();
    }

    public void cancel() {
        status = DeliveryStatus.DELIVERY_CANCEL;
        nextDeliveryDate = null;
    }

    public void paymentDone() {
        status = DeliveryStatus.PAYMENT_DONE;
    }



    public void skip(LocalDate nextDeliveryDate) {
        this.nextDeliveryDate = nextDeliveryDate;
    }

    public void start(String deliveryNumber) {
        status = DeliveryStatus.DELIVERY_START;
        this.deliveryNumber = deliveryNumber;
        this.departureDate = LocalDateTime.now();
    }

    public void generateTransUniqueCd(String transUniqueCd) {
        this.transUniqueCd = transUniqueCd;
    }



}
