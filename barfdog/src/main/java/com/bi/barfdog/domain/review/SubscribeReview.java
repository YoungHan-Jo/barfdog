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
public class SubscribeReview extends Review{

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "subscribe_id")
    private Subscribe subscribe;

    @Builder
    public SubscribeReview(Long id, Member member, LocalDate writtenDate, String username, int star, String contents, ReviewStatus status, Subscribe subscribe) {
        super(id, member, writtenDate, username, star, contents, status);
        this.subscribe = subscribe;
    }

    public SubscribeReview(Subscribe subscribe) {
        this.subscribe = subscribe;
    }
}
