package com.bi.barfdog.service;

import com.bi.barfdog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponService {

    private final CouponRepository couponRepository;


}
