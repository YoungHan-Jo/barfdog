package com.bi.barfdog.repository.subscribe;

import com.bi.barfdog.domain.subscribe.BeforeSubscribe;
import com.bi.barfdog.domain.subscribe.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeforeSubscribeRepository extends JpaRepository<BeforeSubscribe, Long> {
    Optional<BeforeSubscribe> findBySubscribe(Subscribe subscribe);

    void deleteAllBySubscribe(Subscribe subscribe);
}
