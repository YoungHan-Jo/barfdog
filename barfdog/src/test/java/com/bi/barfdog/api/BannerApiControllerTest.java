package com.bi.barfdog.api;

import com.bi.barfdog.api.dto.BannerSaveRequestDto;
import com.bi.barfdog.api.dto.MainBannerSaveRequestDto;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.domain.banner.BannerTargets;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BannerApiControllerTest extends BaseTest {

    @Test
    @DisplayName("정상적으로 메인 배너를 생성하는 테스트")
    public void createMainBanner() throws Exception {
       //Given
        MainBannerSaveRequestDto requestDto = MainBannerSaveRequestDto.builder()
                .name("메인배너1")
                .pcLinkUrl("pc link")
                .mobileLinkUrl("mobile link")
                .build();

        //when
        mockMvc.perform(post("/api/banners/main")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

       //then

    }





    @Test
    @DisplayName("정상적으로 배너를 생성하는 테스트")
    public void createBanner() throws Exception {
       //Given
        BannerSaveRequestDto banner = BannerSaveRequestDto.builder()
                .bannerType("main")
                .name("메인베너1")
                .targets(BannerTargets.ALL)
                .status(BannerStatus.LEAKED)
                .pcLinkUrl("pc Link")
                .mobileLinkUrl("mobile link")
                .build();

        //when

        mockMvc.perform(multipart("/api/banners")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(banner)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

       
       //then
      
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 에러가 발생하는 테스트")
   public void createBanner_BadRequest_Empty_Input() throws Exception {
       //Given
        BannerSaveRequestDto banner = BannerSaveRequestDto.builder().build();

        mockMvc.perform(post("/api/banners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(banner)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    
    
}