package com.bi.barfdog.domain.orderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
@Entity
public class SelectOption {

    @Id @GeneratedValue
    @Column(name = "select_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    private String name;
    private int price;
    private int amount;
}
