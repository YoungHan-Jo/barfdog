package com.bi.barfdog.domain.item;

import com.bi.barfdog.api.itemDto.ItemUpdateDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
@Entity
public class ItemOption extends BaseTimeEntity { // 상품 옵션

    @Id @GeneratedValue
    @Column(name = "item_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 아이템옵션:아이템 - 다대일 관계

    private String name; // 옵션이름

    private int optionPrice; // 옵션가격

    private int remaining; // 옵션 수량

    public void update(ItemUpdateDto.ItemOptionUpdateDto dto) {
        name = dto.getName();
        optionPrice = dto.getPrice();
        remaining = dto.getRemaining();
    }


    public void increaseRemaining(int amount) {
        remaining += amount;
    }

    public void decreaseRemaining(int amount) {
        remaining -= amount;
        if (remaining < 0) {
            remaining = 0;
        }
    }
}
