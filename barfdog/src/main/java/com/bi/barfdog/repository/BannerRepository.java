package com.bi.barfdog.repository;

import com.bi.barfdog.domain.banner.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BannerRepository {

    private final EntityManager em;

    public Banner save(Banner banner) {
        em.persist(banner);

        return banner;
    }




    public List<MainBanner> findAllMain() {
        List<MainBanner> resultList = em.createQuery(
                        "select m" +
                                " from MainBanner m", MainBanner.class)
                .getResultList();
        return resultList;
    }

    public List<MyPageBanner> findAllMyPage() {
        List<MyPageBanner> resultList = em.createQuery(
                "select b " +
                        " from MyPageBanner b", MyPageBanner.class)
                .getResultList();

        return resultList;
    }

    public List<PopupBanner> findAllPopup() {
        List<PopupBanner> resultList = em.createQuery(
                        "select p" +
                                " from PopupBanner p", PopupBanner.class)
                .getResultList();
        return resultList;

    }

    public Banner findById(Long id){
        Banner banner = em.createQuery(
                        "select b" +
                                " from Banner b" +
                                " where b.id =:id", Banner.class)
                .setParameter("id", id)
                .getSingleResult();

        return banner;
    }

    public MyPageBanner findMyPageBannerById(Long id) {

        MyPageBanner banner = em.createQuery(
                        "select b from MyPageBanner b where b.id = :id", MyPageBanner.class)
                .setParameter("id", id)
                .getSingleResult();

        return banner;
    }

    public TopBanner findTopBannerById(Long id) {

        TopBanner banner = em.createQuery(
                        "select b from TopBanner b where b.id = :id", TopBanner.class)
                .setParameter("id", id)
                .getSingleResult();

        return banner;
    }

    public List<TopBanner> findAllTop() {
        List<TopBanner> results = em.createQuery("select b from TopBanner b", TopBanner.class)
                .getResultList();
        return results;
    }
}
