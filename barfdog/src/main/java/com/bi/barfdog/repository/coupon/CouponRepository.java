package com.bi.barfdog.repository.coupon;

import com.bi.barfdog.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long>, CouponRepositoryCustom {
    Optional<Coupon> findByName(String name);

    Optional<Coupon> findByCode(String code);
}
