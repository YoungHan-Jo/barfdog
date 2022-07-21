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

    private String deliveryNumber;

    @Embedded
    private Recipient recipient;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private String request; // 요청사항

    private LocalDateTime departureDate; // 출발일
    private LocalDateTime arrivalDate; // 도착일

    private LocalDate nextDeliveryDate; // 배송 예정 일시

    public void paymentDone() {
        status = DeliveryStatus.PAYMENT_DONE;
    }

    public void firstPaymentDone(LocalDate nextDeliveryDate) {
        status = DeliveryStatus.PAYMENT_DONE;
        this.nextDeliveryDate = nextDeliveryDate;
    }

    public void skip(LocalDate nextDeliveryDate) {
        this.nextDeliveryDate = nextDeliveryDate;
    }
}
