package com.bi.barfdog.repository.reward;

import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.reward.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long>, RewardRepositoryCustom {

    List<Reward> findByMember(Member findMember);
}
