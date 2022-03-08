package com.bi.barfdog.service;

import com.bi.barfdog.api.BannerApiController;
import com.bi.barfdog.api.dto.BannerSaveRequestDto;
import com.bi.barfdog.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    /*
     * 배너 저장
     * */
    @Transactional
    public Long save(BannerSaveRequestDto requestDto) {

        switch (requestDto.getBannerType()) {
            case "main":
                System.out.println("requestDto.getBannerType() = " + requestDto.getBannerType());
                break;
            case "myPage":

                break;
            case "top":

                break;
            case "popUp":

                break;
        }

        return null;
    }




}
