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
public class MemberCoupon extends BaseTimeEntity { // 회원이 보유한 쿠폰

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_coupon_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 멤버쿠폰:멤버 - 다대일

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon; // 멤버쿠폰:쿠폰 - 다대일

    private LocalDateTime expiredDate; // 쿠폰 유효 기간

    private int remaining; // 남은 개수

    @Enumerated(EnumType.STRING)
    private CouponStatus memberCouponStatus; // 쿠폰 상태

    public void active() {
        memberCouponStatus = CouponStatus.ACTIVE;
    }

    public void inactive() {
        memberCouponStatus = CouponStatus.INACTIVE;
    }

    public void useCoupon() {
        remaining--;
        if (remaining < 1) {
            remaining = 0;
            this.inactive();
        }
    }

    public void revivalCoupon() {
        remaining++;
        this.active();
    }
}
