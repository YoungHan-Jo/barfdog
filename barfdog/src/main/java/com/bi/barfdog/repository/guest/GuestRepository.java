package com.bi.barfdog.repository.guest;

import com.bi.barfdog.domain.guest.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest,Long>, GuestRepositoryCustom {
    Optional<Guest> findByPhoneNumber(String phoneNumber);

    Optional<Guest> findByEmail(String email);
}
