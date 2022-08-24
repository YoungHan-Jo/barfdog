package com.bi.barfdog.service;

import com.bi.barfdog.common.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class DeliveryServiceTest extends BaseTest {

    @Autowired
    DeliveryService deliveryService;

    @Test
    public void deliveryDoneScheduler() throws Exception {
       //given

        deliveryService.deliveryDoneScheduler();

       //when & then

    }

}