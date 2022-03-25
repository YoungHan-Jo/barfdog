package com.bi.barfdog.repository;

import com.bi.barfdog.domain.banner.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {
}
