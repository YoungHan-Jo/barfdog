package com.bi.barfdog.api;

import com.bi.barfdog.api.dto.BannerSaveRequestDto;
import com.bi.barfdog.domain.banner.Banner;
import com.bi.barfdog.domain.banner.BannerStatus;
import com.bi.barfdog.repository.BannerRepository;
import com.bi.barfdog.service.BannerService;
import com.fasterxml.jackson.databind.deser.BasicDeserializerFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BannerApiController {

    private final BannerRepository bannerRepository;
    private final BannerService bannerService;

    @GetMapping("/api/v1/banners/{dtype}")
    public Result banners(@PathVariable("dtype") String dtype) {
        List<Banner> banners = bannerRepository.findAll(dtype);
        return new Result(banners);
    }

    @PostMapping("/api/v1/banners/{bannerType}")
    public Result create(@PathVariable("bannerType") String bannerType,
                         @RequestPart(value = "requestDto", required = false) BannerSaveRequestDto requestDto,
                         @RequestParam(required = false) MultipartFile pc,
                         @RequestParam(required = false) MultipartFile mobile) {
        if(pc != null){

        }
        if (mobile != null) {
            System.out.println("moble : " + mobile);
        }else{
            System.out.println("moblie의 값이 없습니다.");
        }

        bannerService.save(bannerType, requestDto);

        return new Result(requestDto);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

}
