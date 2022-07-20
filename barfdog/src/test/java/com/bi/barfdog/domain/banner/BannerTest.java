package com.bi.barfdog.domain.banner;


import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class BannerTest {




    @Test
    @DisplayName("빌더 테스트")
    public void builder() {
        MainBanner mainBanner = MainBanner.builder().build();
        MyPageBanner mypageBanner = MyPageBanner.builder().build();
        PopupBanner popupBanner = PopupBanner.builder().build();
        TopBanner topBanner = TopBanner.builder().build();

        assertThat(mainBanner).isNotNull();
        assertThat(mypageBanner).isNotNull();
        assertThat(popupBanner).isNotNull();
        assertThat(topBanner).isNotNull();
    }

}