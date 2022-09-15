package com.bi.barfdog.domain.review;

import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.Subscribe;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import java.time.LocalDate;

import static javax.persistence.FetchType.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("subscribe")
@Getter
@Entity
public class SubscribeReview extends Review{ // Review를 상속받은 구독리뷰

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "subscribe_id")
    private Subscribe subscribe; // 해당 구독 일대일

    @Builder
    public SubscribeReview(Long id, Member member, LocalDate writtenDate, String username, int star, String contents, ReviewStatus status, String returnReason, Subscribe subscribe) {
        super(id, member, writtenDate, username, star, contents, status, returnReason);
        this.subscribe = subscribe;
    }

    public SubscribeReview(Subscribe subscribe) {
        this.subscribe = subscribe;
    }
}
