package com.bi.barfdog.api;

import com.bi.barfdog.api.bannerDto.MainBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.MyPageBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.PopupBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.TopBannerSaveRequestDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.banner.*;
import com.bi.barfdog.repository.BannerRepository;
import com.bi.barfdog.service.BannerService;
import com.bi.barfdog.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/banners", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class BannerApiController {

    private final BannerRepository bannerRepository;
    private final BannerService bannerService;

    private final CommonValidator commonValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @PostMapping("/main")
    public ResponseEntity createMainBanner(
                    @RequestPart @Valid MainBannerSaveRequestDto requestDto, Errors errors,
                    @RequestPart(required = false) MultipartFile pcFile,
                    @RequestPart(required = false) MultipartFile mobileFile) {
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        commonValidator.validateFiles(errors, pcFile, mobileFile);
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
                profileRootUrlBuilder.slash("index.html#resources-create-mainBanner").withRel("profile")
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
        commonValidator.validateFiles(errors, pcFile, mobileFile);
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
                profileRootUrlBuilder.slash("index.html#resources-create-myPageBanner").withRel("profile")
        );

        return ResponseEntity.created(createBannerUri).body(bannerEntityModel);
    }

    @PostMapping("/popup")
    public ResponseEntity createPopupBanner(
            @RequestPart @Valid PopupBannerSaveRequestDto requestDto, Errors errors,
            @RequestPart(required = false) MultipartFile pcFile,
            @RequestPart(required = false) MultipartFile mobileFile) {
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        commonValidator.validateFiles(errors, pcFile, mobileFile);
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
                profileRootUrlBuilder.slash("index.html#resources-create-popupBanner").withRel("profile")
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
                profileRootUrlBuilder.slash("index.html#resources-create-topBanner").withRel("profile")
        );

        return ResponseEntity.created(createBannerUri).body(entityModel);
    }

    @GetMapping("/myPage")
    public ResponseEntity queryMyPageBanner() {
        List<MyPageBanner> results = bannerRepository.findMyPageBanners();

        MyPageBanner myPageBanner = results.get(0);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("myPage");

        EntityModel<MyPageBanner> entityModel = EntityModel.of(myPageBanner,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.slash(myPageBanner.getId()).withRel("update-banner"),
                profileRootUrlBuilder.slash("index.html#resources-query-myPageBanner").withRel("profile")
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

        Optional<Banner> optionalBanner = bannerRepository.findById(id);

        if(optionalBanner.isPresent() == false){
            return notFound();
        }

        MyPageBanner myPageBanner = bannerService.updateMyPageBanner(id, requestDto, pcFile, mobileFile);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("myPage").slash(id);

        EntityModel<MyPageBanner> entityModel = EntityModel.of(myPageBanner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("myPage").withRel("query-myPageBanner"),
                profileRootUrlBuilder.slash("index.html#resources-update-myPageBanner").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/top")
    public ResponseEntity getTopBanner() {

        List<TopBanner> results = bannerRepository.findTopBanners();

        TopBanner banner = results.get(0);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("top");

        EntityModel<TopBanner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.slash(banner.getId()).withRel("update-banner"),
                profileRootUrlBuilder.slash("index.html#resources-query-topBanner").withRel("profile")
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

        Optional<Banner> optionalBanner = bannerRepository.findById(id);

        if(!optionalBanner.isPresent()){
            return notFound();
        }

        TopBanner banner = bannerService.updateTopBanner(id, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("top").slash(banner.getId());

        EntityModel<TopBanner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("top").withRel("query-topBanner"),
                profileRootUrlBuilder.slash("index.html#resources-update-topBanner").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/main")
    public ResponseEntity queryMainBanners() {
        List<EntityModel<MainBanner>> entityModelList = new ArrayList<>();
        List<MainBanner> mainBanners = bannerRepository.findMainBanners();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("main");

        for (MainBanner mainBanner : mainBanners) {
            EntityModel<MainBanner> entityModel = EntityModel.of(mainBanner,
                    selfLinkBuilder.slash(mainBanner.getId()).withSelfRel()
            );
            entityModelList.add(entityModel);
        }

        CollectionModel<EntityModel<MainBanner>> collectionModel = CollectionModel.of(entityModelList,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("create-banner"),
                profileRootUrlBuilder.slash("index.html#resources-query-mainBanners").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/main/{id}")
    public ResponseEntity queryMainBanner(@PathVariable Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }
        Banner banner = optionalBanner.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("/main").slash(banner.getId());

        EntityModel<Banner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update-banner"),
                profileRootUrlBuilder.slash("index.html#resources-query-mainBanner").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/main/{id}")
    public ResponseEntity updateMainBanner(@PathVariable Long id,
                                             @RequestPart @Valid MainBannerSaveRequestDto requestDto, Errors errors,
                                             @RequestPart(required = false) MultipartFile pcFile,
                                             @RequestPart(required = false) MultipartFile mobileFile) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Optional<Banner> optionalBanner = bannerRepository.findById(id);

        if(!optionalBanner.isPresent()){
            return notFound();
        }

        MainBanner mainBanner = bannerService.updateMainBanner(id, requestDto, pcFile, mobileFile);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("main").slash(id);

        EntityModel<MainBanner> entityModel = EntityModel.of(mainBanner,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("query-banner"),
                linkTo(BannerApiController.class).slash("main").withRel("query-mainBanners"),
                profileRootUrlBuilder.slash("index.html#resources-update-mainBanner").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }



    @PutMapping("/main/{id}/up")
    public ResponseEntity updateMainBannerUp(@PathVariable Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }

        MainBanner savedBanner = (MainBanner) optionalBanner.get();
        if(savedBanner.getLeakedOrder() == 1){
            return ResponseEntity.badRequest().build();
        }
        Banner banner = bannerService.mainBannerUp(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash(id).slash("up");

        EntityModel<Banner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("main").withRel("query-mainBanners"),
                profileRootUrlBuilder.slash("index.html#resources-update-mainBanner-leakedOrder-up").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/main/{id}/down")
    public ResponseEntity updateMainBannerDown(@PathVariable Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }

        MainBanner savedBanner = (MainBanner) optionalBanner.get();
        int mainBannersCount = bannerRepository.findMainBanners().size();

        if (mainBannersCount == savedBanner.getLeakedOrder()) {
            return ResponseEntity.badRequest().build();
        }

        Banner banner = bannerService.mainBannerDown(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash(id).slash("down");

        EntityModel<Banner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("main").withRel("query-mainBanners"),
                profileRootUrlBuilder.slash("index.html#resources-update-mainBanner-leakedOrder-down").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/main/{id}")
    public ResponseEntity deleteMainBanner(@PathVariable Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }

        bannerService.deleteMainBanner(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("main").slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("query-mainBanners"));
        representationModel.add(selfLinkBuilder.withRel("create-banner"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-mainBanner").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/popup")
    public ResponseEntity queryPopupBanners() {
        List<EntityModel<PopupBanner>> entityModelList = new ArrayList<>();
        List<PopupBanner> popupBanners = bannerRepository.findPopupBanners();

        for (PopupBanner popupBanner : popupBanners) {
            EntityModel<PopupBanner> entityModel = EntityModel.of(popupBanner,
                    linkTo(BannerApiController.class).slash("popup").slash(popupBanner.getId()).withSelfRel(),
                    linkTo(BannerApiController.class).slash("popup").slash(popupBanner.getId()).withRel("delete-popupBanner"),
                    linkTo(BannerApiController.class).slash("popup").slash(popupBanner.getId()).withRel("update-popupBanner"),
                    linkTo(BannerApiController.class).slash("popup").slash(popupBanner.getId()).slash("up").withRel("update-popupBanner-order-up"),
                    linkTo(BannerApiController.class).slash("popup").slash(popupBanner.getId()).slash("down").withRel("update-popupBanner-order-down")
            );
            entityModelList.add(entityModel);
        }

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup");
        CollectionModel<EntityModel<PopupBanner>> collectionModel = CollectionModel.of(entityModelList,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("create-banner"),
                profileRootUrlBuilder.slash("index.html#resources-query-popupBanners").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/popup/{id}")
    public ResponseEntity queryPopupBanner(@PathVariable Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }
        Banner banner = optionalBanner.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup").slash(id);

        EntityModel<Banner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update-banner"),
                profileRootUrlBuilder.slash("index.html#resources-query-popupBanner").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/popup/{id}")
    public ResponseEntity updatePopupBanner(@PathVariable Long id,
                                            @RequestPart @Valid PopupBannerSaveRequestDto requestDto, Errors errors,
                                            @RequestPart(required = false) MultipartFile pcFile,
                                            @RequestPart(required = false) MultipartFile mobileFile) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }

        PopupBanner banner = bannerService.updatePopupBanner(id, requestDto, pcFile, mobileFile);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup").slash(id);

        EntityModel<PopupBanner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("query-banner"),
                linkTo(BannerApiController.class).slash("popup").withRel("query-popupBanners"),
                profileRootUrlBuilder.slash("index.html#resources-update-popupBanner").withRel("profile")
        );


        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/popup/{id}/up")
    public ResponseEntity updatePopupBannerUp(@PathVariable Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }

        PopupBanner popupBanner = (PopupBanner) optionalBanner.get();
        if (popupBanner.getLeakedOrder() == 1) {
            return ResponseEntity.badRequest().build();
        }

        Banner banner = bannerService.popupBannerUp(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup").slash(id).slash("up");

        EntityModel<Banner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("popup").withRel("query-popupBanners"),
                profileRootUrlBuilder.slash("index.html#resources-update-popupBanner-leakedOrder-up").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/popup/{id}/down")
    public ResponseEntity updatePopupBannerDown(@PathVariable Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }

        PopupBanner popupBanner = (PopupBanner) optionalBanner.get();
        List<PopupBanner> popupBanners = bannerRepository.findPopupBanners();
        if (popupBanner.getLeakedOrder() == popupBanners.size()) {
            return ResponseEntity.badRequest().build();
        }

        Banner banner = bannerService.popupBannerDown(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup").slash(id).slash("down");

        EntityModel<Banner> entityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("popup").withRel("query-popupBanners"),
                profileRootUrlBuilder.slash("index.html#resources-update-popupBanner-leakedOrder-down").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/popup/{id}")
    public ResponseEntity deletePopupBanner(@PathVariable Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (!optionalBanner.isPresent()) {
            return notFound();
        }

        bannerService.deletePopupBanner(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup").slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("query-popupBanners"));
        representationModel.add(selfLinkBuilder.withRel("create-banner"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-popupBanner").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }








    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
