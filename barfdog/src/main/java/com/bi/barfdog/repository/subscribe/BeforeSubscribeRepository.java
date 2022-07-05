package com.bi.barfdog.repository.subscribe;

import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeforeSubscribeRepository extends JpaRepository<BeforeSubscribe, Long> {
}
