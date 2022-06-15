package com.bi.barfdog.api;

import com.bi.barfdog.api.couponDto.*;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BarfUtils;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.config.finalVariable.AutoCoupon;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.coupon.*;
import com.bi.barfdog.domain.member.*;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.coupon.CouponRepository;
import com.bi.barfdog.repository.memberCoupon.MemberCouponRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.bi.barfdog.api.couponDto.UpdateAutoCouponRequest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class CouponApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberCouponRepository memberCouponRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp() {
        memberCouponRepository.deleteAll();
    }



}