package com.bi.barfdog.goodsFlow;

import com.bi.barfdog.common.BaseTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class GoodsFlowUtilsTest extends BaseTest {

    @Test
    public void CheckTraceResults() throws Exception {
       //given

        List<CheckTraceResultRequestDto> requestDtoList = new ArrayList<>();

        CheckTraceResultRequestDto requestDto1 = new CheckTraceResultRequestDto("o0001-01", "1");
        CheckTraceResultRequestDto requestDto2 = new CheckTraceResultRequestDto("o0001-02", "2");

        requestDtoList.add(requestDto1);
        requestDtoList.add(requestDto2);

        GoodsFlowUtils.checkTraceResults(requestDtoList);

       //when & then

    }


}