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
public class BasketOption extends BaseTimeEntity { // 장바구니에 담은 옵션

    @Id @GeneratedValue
    @Column(name = "basket_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "basket_id")
    private Basket basket; // 장바구니 옵션을 포함하고 있는 장바구니 객체

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption; // 장바구니 옵션에 해당하는 일반아이템옵션

    private int amount; // 일반 아이템 옵션 수량

    public void merge(int optionAmount) {
        amount += optionAmount;
    }
}
