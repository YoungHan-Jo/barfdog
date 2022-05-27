package com.bi.barfdog.domain.item;

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
public class ItemImage extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "item_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int leakOrder;
    private String folder;
    private String filename;

    public void setItem(Item item) {
        this.item = item;
    }

    public void setOrder(int leakOrder) {
        this.leakOrder = leakOrder;
    }
}
