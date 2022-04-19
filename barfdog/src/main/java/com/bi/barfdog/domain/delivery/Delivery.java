package com.bi.barfdog.domain.delivery;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    private String request;


}
