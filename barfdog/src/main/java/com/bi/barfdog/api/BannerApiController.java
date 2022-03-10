package com.bi.barfdog.api;

import com.bi.barfdog.api.dto.BannerSaveRequestDto;
import com.bi.barfdog.common.DefaultRes;
import com.bi.barfdog.common.ResponseMessage;
import com.bi.barfdog.common.StatusCode;
import com.bi.barfdog.domain.banner.Banner;
import com.bi.barfdog.repository.BannerRepository;
import com.bi.barfdog.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BannerApiController {

    private final BannerRepository bannerRepository;
    private final BannerService bannerService;

    @GetMapping("/api/v1/banners/{dtype}")
    public ResponseEntity banners(@PathVariable("dtype") String dtype) {
        List<Banner> banners = bannerRepository.findAll(dtype);
        return new ResponseEntity(dtype, HttpStatus.OK);
    }

    @PostMapping("/api/v1/banners")
    public ResponseEntity create(@RequestPart(value = "requestDto", required = false) BannerSaveRequestDto requestDto,
                                 @RequestParam(required = false) MultipartFile pc,
                                 @RequestParam(required = false) MultipartFile mobile) {
        if(pc != null){

        }
        if (mobile != null) {
            System.out.println("moble : " + mobile.getOriginalFilename());
        }else{
            System.out.println("moblie의 값이 없습니다.");
        }

//        bannerService.save(requestDto);

        return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.CREATED_USER, requestDto), HttpStatus.OK);
    }


}
