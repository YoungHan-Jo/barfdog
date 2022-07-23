package com.bi.barfdog.repository.delivery;

import com.bi.barfdog.domain.delivery.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery,Long>,DeliveryRepositoryCustom {
    Optional<Delivery> findByTransUniqueCd(String transUniqueCd);
}
