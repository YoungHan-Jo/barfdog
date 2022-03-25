package com.bi.barfdog.repository;

import com.bi.barfdog.domain.banner.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BannerRepositoryImpl implements BannerRepositoryCustom{

    private final EntityManager em;

    @Override
    public List<MainBanner> findMainBanners() {
        List<MainBanner> mainBanners = em.createQuery("select b from MainBanner b", MainBanner.class)
                .getResultList();
        return mainBanners;
    }

    @Override
    public List<PopupBanner> findPopupBanners() {
        List<PopupBanner> resultList = em.createQuery(
                        "select p" +
                                " from PopupBanner p", PopupBanner.class)
                .getResultList();
        return resultList;
    }

    @Override
    public List<TopBanner> findTopBanners() {
        List<TopBanner> results = em.createQuery("select b from TopBanner b", TopBanner.class)
                .getResultList();
        return results;
    }

    @Override
    public MainBanner findToDownByOrder(int order) {
        MainBanner result = em.createQuery(
                        "select b" +
                                " from MainBanner b" +
                                " where b.leakedOrder = :order", MainBanner.class)
                .setParameter("order", order)
                .getSingleResult();
        return result;
    }

    @Override
    public List<MyPageBanner> findMyPageBanners() {
        List<MyPageBanner> resultList = em.createQuery(
                        "select b " +
                                " from MyPageBanner b", MyPageBanner.class)
                .getResultList();

        return resultList;
    }
}
