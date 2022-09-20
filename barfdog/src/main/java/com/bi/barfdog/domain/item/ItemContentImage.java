package com.bi.barfdog.domain.item;

import com.bi.barfdog.domain.BaseTimeEntity;
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
public class ItemContentImage extends BaseTimeEntity { // 상품 상세내용에 사용되는 이미지

    @Id @GeneratedValue
    @Column(name = "product_content_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String folder;
    private String filename;

    public void setItem(Item item) {
        this.item = item;
    }
}
