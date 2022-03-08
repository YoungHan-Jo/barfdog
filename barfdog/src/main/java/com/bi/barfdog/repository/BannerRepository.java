package com.bi.barfdog.repository;

import com.bi.barfdog.domain.banner.Banner;
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

    public List<Banner> findAll(String dtype) {
        return em.createQuery(
                "select b" +
                        " from Banner b" +
                        " where dtype = :dtype" +
                        " order by leaked_order desc", Banner.class)
                .setParameter("dtype",dtype)
                .getResultList();
    }


}
