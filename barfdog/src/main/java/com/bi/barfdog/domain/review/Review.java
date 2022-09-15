package com.bi.barfdog.domain.review;

import com.bi.barfdog.api.reviewDto.ReturnReviewDto;
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

    private LocalDate writtenDate; // 작성일

    private String username; // 작성자이름

    private int star; // 별 1~5

    @Column(columnDefinition = "TEXT")
    private String contents; // 내용

    @Enumerated(EnumType.STRING)
    private ReviewStatus status; // [REQUEST,RETURN,APPROVAL,ADMIN]

    private String returnReason; // 리뷰 요청 반려 사유

    public void update(UpdateReviewDto requestDto) {
        this.star = requestDto.getStar();
        this.contents = requestDto.getContents();
        this.status = ReviewStatus.REQUEST;
    }

    public boolean isRequest() {
        return this.status == ReviewStatus.REQUEST;
    }

    public void approval() {
        this.status = ReviewStatus.APPROVAL;
        this.returnReason = "";
    }

    public void returnReview(ReturnReviewDto requestDto) {
        this.status = ReviewStatus.RETURN;
        this.returnReason = requestDto.getReturnReason();
    }

    public boolean isApproval() {
        return this.status == ReviewStatus.APPROVAL;
    }
}
