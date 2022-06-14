package com.bi.barfdog.domain.review;

import com.bi.barfdog.api.reviewDto.UpdateReviewDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.member.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Entity
public abstract class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate writtenDate;

    private String username;

    private int star;

    private String contents;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status; // [REQUEST,RETURN,APPROVAL,ADMIN]

    private String returnReason;

    public void update(UpdateReviewDto requestDto) {
        this.star = requestDto.getStar();
        this.contents = requestDto.getContents();
    }
}
