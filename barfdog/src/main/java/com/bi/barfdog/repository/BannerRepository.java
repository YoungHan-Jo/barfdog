package com.bi.barfdog.repository;

import com.bi.barfdog.domain.banner.Banner;
import com.bi.barfdog.domain.banner.MainBanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BannerRepository {

    private final EntityManager em;

    public Long save(Banner banner) {
        em.persist(banner);

        return banner.getId();
    }


    public void saveMainBanner(MainBanner mainbanner) {
        em.persist(mainbanner);
    }

    public List<MainBanner> findAllMain() {
        List<MainBanner> resultList = em.createQuery(
                        "select m" +
                                " from MainBanner m", MainBanner.class)
                .getResultList();
        return resultList;
    }
}
