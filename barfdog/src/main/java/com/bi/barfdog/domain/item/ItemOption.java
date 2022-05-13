package com.bi.barfdog.domain.item;

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
public class ItemOption {

    @Id @GeneratedValue
    @Column(name = "item_option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String name;

    private int optionPrice;

    private int remaining;

}
