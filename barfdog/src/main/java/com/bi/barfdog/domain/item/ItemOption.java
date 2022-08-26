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
public class ItemOption extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "item_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String name;

    private int optionPrice;

    private int remaining;

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
