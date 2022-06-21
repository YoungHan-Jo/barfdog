package com.bi.barfdog.domain.basket;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.item.ItemOption;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class BasketOption extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "basket_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "basket_id")
    private Basket basket;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption;

    private int amount;
}
