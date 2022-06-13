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
public class ItemReview extends Review{

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    public ItemReview(Long id, Member member, LocalDate writtenDate, String username, int star, String contents, ReviewStatus status, Item item) {
        super(id, member, writtenDate, username, star, contents, status);
        this.item = item;
    }

    public ItemReview(Item item) {
        this.item = item;
    }
}
