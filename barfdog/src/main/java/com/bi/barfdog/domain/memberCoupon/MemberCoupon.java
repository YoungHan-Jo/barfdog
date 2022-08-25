package com.bi.barfdog.domain.memberCoupon;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
@Entity
public class MemberCoupon extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_coupon_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private LocalDateTime expiredDate; // 쿠폰 유효 기간

    private int remaining; // 남은 개수

    private CouponStatus memberCouponStatus; // 쿠폰 상태

    public void active() {
        memberCouponStatus = CouponStatus.ACTIVE;
    }

    private void inactive() {
        memberCouponStatus = CouponStatus.INACTIVE;
    }

    public void useCoupon() {
        remaining--;
        if (remaining <= 0) this.inactive();
    }

    public void revivalCoupon() {
        remaining++;
        this.active();
    }
}
