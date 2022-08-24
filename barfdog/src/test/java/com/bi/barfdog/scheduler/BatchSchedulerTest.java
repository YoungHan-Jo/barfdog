package com.bi.barfdog.scheduler;

import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.service.DeliveryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class BatchSchedulerTest extends BaseTest {

    @Autowired
    DeliveryService deliveryService;

    @Test
    public void deliveryDoneScheduler() throws Exception {
       //given

        deliveryService.deliveryDoneScheduler();

       //when & then

    }


}