package com.bi.barfdog.service;

import com.bi.barfdog.api.guestDto.SaveGuestRequest;
import com.bi.barfdog.domain.guest.Guest;
import com.bi.barfdog.repository.guest.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GuestService {

    private final GuestRepository guestRepository;

    @Transactional
    public void createGuest(SaveGuestRequest requestDto) {

        Guest guest = Guest.builder()
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .email(requestDto.getEmail())
                .build();
        guestRepository.save(guest);

    }
}
