package com.bi.barfdog.api;

import com.bi.barfdog.common.BaseTest;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;

import static org.junit.Assert.*;

@Transactional
public class DogApiControllerTest extends BaseTest {

    MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    
    @Test
    @DisplayName("정상적으로 강아지 등록하는 테스트")
    public void create_dog() throws Exception {
       //Given
       
       //when
       
       //then
      
    }
    
}