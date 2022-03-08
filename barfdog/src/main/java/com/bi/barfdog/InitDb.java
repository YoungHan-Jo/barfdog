package com.bi.barfdog;

import com.bi.barfdog.domain.banner.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            MainBanner mainBanner1 = createMainBanner("메인배너1", 1);
            em.persist(mainBanner1);

            MypageBanner mypageBanner = createMypageBanner("마이페이지배너1");
            em.persist(mypageBanner);

            PopupBanner popupBanner = createPopupBanner("팝업배너1", 1);
            em.persist(popupBanner);

            TopBanner topBanner = createTopBanner("최상단배너1", "black");
            em.persist(topBanner);

        }



        public void dbInit2() {
            MainBanner mainBanner2 = createMainBanner("메인배너2", 2);
            em.persist(mainBanner2);

            MypageBanner mypageBanner = createMypageBanner("마이페이지배너2");
            em.persist(mypageBanner);

            PopupBanner popupBanner = createPopupBanner("팝업배너2", 2);
            em.persist(popupBanner);

            TopBanner topBanner = createTopBanner("최상단배너2", "white");
            em.persist(topBanner);

        }

        private MainBanner createMainBanner(String name, int leakedOrder) {
            MainBanner mainBanner = MainBanner.builder()
                    .name(name)
                    .leakedOrder(leakedOrder)
                    .build();

            return mainBanner;
        }

        private MypageBanner createMypageBanner(String name) {
            MypageBanner mypageBanner = MypageBanner.builder()
                    .name(name)
                    .build();
            return mypageBanner;
        }

        private PopupBanner createPopupBanner(String name, int leakedOrder) {
            PopupBanner popupBanner = PopupBanner.builder()
                    .name(name)
                    .leakedOrder(leakedOrder)
                    .build();
            return popupBanner;
        }

        private TopBanner createTopBanner(String name, String backgroundColor) {
            TopBanner topBanner = TopBanner.builder()
                    .name(name)
                    .backgroundColor(backgroundColor)
                    .build();

            return topBanner;
        }

    }
}

