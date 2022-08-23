package com.bi.barfdog.repository.guest;

import com.bi.barfdog.domain.guest.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest,Long>, GuestRepositoryCustom {
}
