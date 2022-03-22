package com.bi.barfdog.api;

import com.bi.barfdog.api.dto.BannerSaveRequestDto;
import com.bi.barfdog.api.dto.MainBannerSaveRequestDto;
import com.bi.barfdog.common.DefaultRes;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.common.ResponseMessage;
import com.bi.barfdog.common.StatusCode;
import com.bi.barfdog.domain.banner.Banner;
import com.bi.barfdog.domain.banner.MainBanner;
import com.bi.barfdog.repository.BannerRepository;
import com.bi.barfdog.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/banners", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class BannerApiController {

    private final BannerRepository bannerRepository;
    private final BannerService bannerService;
    private final ModelMapper modelMapper;


    @PostMapping("/main")
    public ResponseEntity createMainBanner(
                    @RequestPart @Valid MainBannerSaveRequestDto requestDto, Errors errors,
                                           @RequestPart(required = false) MultipartFile pcFile,
                                           @RequestPart(required = false) MultipartFile mobileFile) {
        if(errors.hasErrors()){
            System.out.println("에러 발생");
            return badRequest(errors);
        }

        if(pcFile!=null){
            System.out.println("pc file : " + pcFile.getOriginalFilename());
        }
        if(mobileFile!=null){
            System.out.println("mobile file : " + mobileFile.getOriginalFilename());
        }

        System.out.println("requestDto.toString() = " + requestDto.toString());

        Long savedId = bannerService.saveMainBanner(requestDto, pcFile, mobileFile);


        return ResponseEntity.created(null).body(savedId);

    }



    @PostMapping
    public ResponseEntity create(
                                 @RequestParam(required = false) MultipartFile pcFile,
                                 @RequestParam(required = false) MultipartFile mobileFile,
                                 @RequestPart @Valid BannerSaveRequestDto bannerSaveRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        if(pcFile != null){

        }
        if (mobileFile != null) {
            System.out.println("moble : " + mobileFile.getOriginalFilename());
        }else{
            System.out.println("moblie의 값이 없습니다.");
        }

        return new ResponseEntity(DefaultRes.res(StatusCode.CREATED, ResponseMessage.CREATED_USER, bannerSaveRequestDto), HttpStatus.CREATED);
    }





    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
