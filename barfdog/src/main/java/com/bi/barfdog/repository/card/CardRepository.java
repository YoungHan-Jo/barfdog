package com.bi.barfdog.repository.card;

import com.bi.barfdog.domain.member.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
