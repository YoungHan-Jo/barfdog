package com.bi.barfdog.repository.subscribe;

import com.bi.barfdog.domain.subscribe.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long>, SubscribeRepositoryCustom {

}
