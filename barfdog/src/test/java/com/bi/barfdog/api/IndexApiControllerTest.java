package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.Address;
import com.bi.barfdog.domain.member.Agreement;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IndexApiControllerTest extends BaseTest {

    @Autowired
    MemberRepository memberRepository;

    MediaType contentType = new MediaType("application", "hal+json", Charset.forName("UTF-8"));

    @Test
    @Transactional
    @DisplayName("추천인 코드x/수신동의x 정상적으로 회원 가입이 완료되는 테스트")
    public void join() throws Exception {
       //Given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("이름")
                .email("verin4494@gmail.com")
                .password("12341234")
                .confirmPassword("12341234")
                .phoneNumber("01099038544")
                .address(new Address("15432","부산시","황령대로13","한라시그마 904호"))
                .birthday("19930521")
                .gender(Gender.MALE)
                .agreement(new Agreement(true,true,true,true,true))
                .build();


        //when & then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;
    }
    
    @Test
    @DisplayName("부족한 입력값으로 회원 가입한 경우 bad request 나오는 테스트")
    public void join_Bad_Request() throws Exception {
       //Given

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder().build();

        //when

        mockMvc.perform(post("/join")
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
       
       //then
      
    }
    




}