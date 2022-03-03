package com.bi.barfdog.api;

import com.bi.barfdog.domain.banner.Banner;
import com.bi.barfdog.repository.BannerRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BannerApiController {

    private final BannerRepository bannerRepository;

    @GetMapping("/api/v1/banners/{dtype}")
    public Result banners(@PathVariable("dtype") String dtype) {
        List<Banner> banners = bannerRepository.findAll(dtype);
        return new Result(banners);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

}
