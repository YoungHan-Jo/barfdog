package com.bi.barfdog.domain.orderItem;

import com.bi.barfdog.domain.item.ItemOption;
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
public class SelectOption { // 선택한 옵션

    @Id @GeneratedValue
    @Column(name = "select_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem; // 선택한옵션:주문아이템 - 다대일 관계

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption; // 선택한옵션:아이템옵션 - 다대일

    private String name; // 옵션명
    private int price; // 옵션 하나 가격
    private int amount; // 옵션 수량

    public void increaseItemOption() {
        itemOption.increaseRemaining(amount);
    }
}
