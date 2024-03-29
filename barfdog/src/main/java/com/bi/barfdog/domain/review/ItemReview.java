package com.bi.barfdog.domain.review;

import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.member.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import java.time.LocalDate;

import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("item")
@Getter
@Entity
public class ItemReview extends Review{ // 리뷰를 상속받은 아이템 리뷰

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 아이템리뷰:아이템 - 다대일

    @Builder
    public ItemReview(Long id, Member member, LocalDate writtenDate, String username, int star, String contents, ReviewStatus status, String returnReason, Item item) {
        super(id, member, writtenDate, username, star, contents, status, returnReason);
        this.item = item;
    }

    public ItemReview(Item item) {
        this.item = item;
    }
}
