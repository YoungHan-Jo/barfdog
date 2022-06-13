package com.bi.barfdog.repository.memberCoupon;

import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long>, MemberCouponRepositoryCustom {

}
