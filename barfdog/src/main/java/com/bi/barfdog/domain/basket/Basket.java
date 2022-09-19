package com.bi.barfdog.domain.basket;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.member.Member;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class Basket extends BaseTimeEntity { // 장바구니 객체

    // 장바구니 객체를 유저로 검색해서 장바구니 객체 리스트를 장바구니 화면에 뿌려줌

    @Id @GeneratedValue
    @Column(name = "basket_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 장바구니에 들어간 일반 아이템

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 장바구니 주인 유저

    private int amount; // 일반 아이템 수량

    public void increase() {
        amount++;
    }

    public void decrease() {
        if (amount > 1) {
            amount--;
        }
    }

    public void merge(int itemAmount) {
        amount += itemAmount;
    }
}
