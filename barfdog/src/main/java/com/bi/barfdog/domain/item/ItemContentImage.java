package com.bi.barfdog.domain.item;

import com.bi.barfdog.domain.item.Item;
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
public class ItemContentImage {

    @Id @GeneratedValue
    @Column(name = "product_content_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int leakedOrder;
    private String folder;
    private String filename;

}
