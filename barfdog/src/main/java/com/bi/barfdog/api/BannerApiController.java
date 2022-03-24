package com.bi.barfdog.api;

import com.bi.barfdog.api.dto.*;
import com.bi.barfdog.common.DefaultRes;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.common.ResponseMessage;
import com.bi.barfdog.common.StatusCode;
import com.bi.barfdog.domain.banner.Banner;
import com.bi.barfdog.domain.banner.MainBanner;
import com.bi.barfdog.domain.banner.MyPageBanner;
import com.bi.barfdog.domain.banner.TopBanner;
import com.bi.barfdog.repository.BannerRepository;
import com.bi.barfdog.service.BannerService;
import com.bi.barfdog.validator.BannerValidator;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.swing.*;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RequestMapping(value = "/api/banners", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class BannerApiController {

    private final BannerRepository bannerRepository;
    private final BannerService bannerService;
    private final ModelMapper modelMapper;
    private final BannerValidator bannerValidator;

    final String ROOT = "/docs/index.html";


    @PostMapping("/main")
    public ResponseEntity createMainBanner(
                    @RequestPart @Valid MainBannerSaveRequestDto requestDto, Errors errors,
                    @RequestPart(required = false) MultipartFile pcFile,
                    @RequestPart(required = false) MultipartFile mobileFile) {
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        bannerValidator.validate(errors, pcFile, mobileFile);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Banner banner = bannerService.saveMainBanner(requestDto, pcFile, mobileFile);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("main");
        URI createBannerUri = selfLinkBuilder.toUri();

        EntityModel<Banner> bannerEntityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("main").withRel("query-mainBanners"),
                linkTo(BannerApiController.class).slash("main").slash(banner.getId()).withRel("update-banner"),
                Link.of(ROOT + "#resources-create-mainBanner").withRel("profile")
        );

        return ResponseEntity.created(createBannerUri).body(bannerEntityModel);
    }

    @PostMapping("/myPage")
    public ResponseEntity createMyPageBanner(
            @RequestPart @Valid MyPageBannerSaveRequestDto requestDto, Errors errors,
            @RequestPart(required = false) MultipartFile pcFile,
            @RequestPart(required = false) MultipartFile mobileFile) {
        if(errors.hasErrors()){
            System.out.println("에러 발생");
            return badRequest(errors);
        }
        bannerValidator.validate(errors, pcFile, mobileFile);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        System.out.println("requestDto.toString() = " + requestDto.toString());

        Banner banner = bannerService.saveMyPageBanner(requestDto, pcFile, mobileFile);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("myPage");
        URI createBannerUri = selfLinkBuilder.toUri();

        EntityModel<Banner> bannerEntityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("myPage").withRel("query-myPageBanner"),
                linkTo(BannerApiController.class).slash("myPage").slash(banner.getId()).withRel("update-banner"),
                Link.of(ROOT + "#resources-create-myPageBanner").withRel("profile")
        );

        return ResponseEntity.created(createBannerUri).body(bannerEntityModel);
    }

    @PostMapping("/popup")
    public ResponseEntity createPopupBanner(
            @RequestPart @Valid PopupBannerSaveRequestDto requestDto, Errors errors,
            @RequestPart(required = false) MultipartFile pcFile,
            @RequestPart(required = false) MultipartFile mobileFile) {
        if(errors.hasErrors()){
            System.out.println("에러 발생");
            return badRequest(errors);
        }
        bannerValidator.validate(errors, pcFile, mobileFile);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Banner banner = bannerService.savePopupBanner(requestDto, pcFile, mobileFile);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup");
        URI createBannerUri = selfLinkBuilder.toUri();

        EntityModel<Banner> bannerEntityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("popup").withRel("query-popupBanners"),
                linkTo(BannerApiController.class).slash("popup").slash(banner.getId()).withRel("update-banner"),
                Link.of(ROOT + "#resources-create-popupBanner").withRel("profile")
        );

        return ResponseEntity.created(createBannerUri).body(bannerEntityModel);
    }

    @PostMapping("/top")
    public ResponseEntity createTopBanner(
                    @RequestBody @Valid TopBannerSaveRequestDto requestDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Banner banner = bannerService.saveTopBanner(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("top");
        URI createBannerUri = selfLinkBuilder.toUri();

        EntityModel<Banner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("top").withRel("query-topBanner"),
                linkTo(BannerApiController.class).slash("top").slash(banner.getId()).withRel("update-banner"),
                Link.of(ROOT + "#resources-create-topBanner").withRel("profile")
        );

        return ResponseEntity.created(createBannerUri).body(entityModel);
    }

    @GetMapping("/myPage")
    public ResponseEntity getMyPageBanner() {
        List<MyPageBanner> results = bannerRepository.findAllMyPage();

        MyPageBanner myPageBanner = results.get(0);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("myPage");

        EntityModel<MyPageBanner> entityModel = EntityModel.of(myPageBanner,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.slash(myPageBanner.getId()).withRel("update-banner"),
                Link.of(ROOT + "#resources-query-myPageBanner").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/myPage/{id}")
    public ResponseEntity updateMyPageBanner(@PathVariable Long id,
                                             @RequestPart @Valid MyPageBannerSaveRequestDto requestDto, Errors errors,
                                             @RequestPart(required = false) MultipartFile pcFile,
                                             @RequestPart(required = false) MultipartFile mobileFile) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        try {
            Banner savedBanner = bannerRepository.findById(id);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        MyPageBanner myPageBanner = bannerService.updateMyPageBanner(id, requestDto, pcFile, mobileFile);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("myPage").slash(id);


        EntityModel<MyPageBanner> entityModel = EntityModel.of(myPageBanner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("myPage").withRel("query-myPageBanner"),
                Link.of(ROOT + "#resources-update-myPageBanner").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/top")
    public ResponseEntity getTopBanner() {

        List<TopBanner> results = bannerRepository.findAllTop();

        TopBanner banner = results.get(0);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("top");

        EntityModel<TopBanner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.slash(banner.getId()).withRel("update-banner"),
                Link.of(ROOT + "#resources-query-topBanner").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/top/{id}")
    public ResponseEntity updateTopBanner(@PathVariable Long id,
                                          @RequestBody @Valid TopBannerSaveRequestDto requestDto,
                                          Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        try {
            bannerRepository.findById(id);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }

        Banner banner = bannerService.updateTopBanner(id, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("top").slash(banner.getId());

        EntityModel<Banner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("top").withRel("query-topBanner"),
                Link.of(ROOT + "#resources-update-topBanner").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }








    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
