package com.bi.barfdog.domain.review;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class ReviewImage { // 리뷰 이미지

    @Id @GeneratedValue
    @Column(name = "review_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review; // 다대일

    private String folder;
    private String filename;

    public void setImageToReview(Review review) {
        this.review = review;
    }
}
