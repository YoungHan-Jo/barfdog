package com.bi.barfdog.repository.banner;

import com.bi.barfdog.domain.banner.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("update MainBanner b set b.leakedOrder = b.leakedOrder - 1 where b.leakedOrder > :order")
    int increaseOrdersUnderDeleteMainBanner(@Param("order") int order);

    @Modifying(clearAutomatically = true)
    @Query("update PopupBanner b set b.leakedOrder = b.leakedOrder - 1 where b.leakedOrder > :order")
    int increaseOrdersUnderDeletePopupBanner(@Param("order") int order);
}
