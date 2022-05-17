package com.bi.barfdog.api;

import com.bi.barfdog.api.bannerDto.*;
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
import org.springframework.http.HttpStatus;
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

    private WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BannerApiController.class).slash("main").withRel("query_mainBanners"));
        representationModel.add(linkTo(BannerApiController.class).slash("main").slash(banner.getId()).withRel("query_mainBanner"));
        representationModel.add(linkTo(BannerApiController.class).slash("main").slash(banner.getId()).withRel("update_banner"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-mainBanner").withRel("profile"));

        return ResponseEntity.created(createBannerUri).body(representationModel);
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

        List<MyPageBanner> myPageBanners = bannerRepository.findMyPageBanners();
        if (myPageBanners.size() > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        System.out.println("requestDto.toString() = " + requestDto.toString());

        Banner banner = bannerService.saveMyPageBanner(requestDto, pcFile, mobileFile);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("myPage");
        URI createBannerUri = selfLinkBuilder.toUri();

        EntityModel<Banner> bannerEntityModel = EntityModel.of(banner,
                selfLinkBuilder.withSelfRel(),
                linkTo(BannerApiController.class).slash("myPage").withRel("query_myPageBanner"),
                linkTo(BannerApiController.class).slash("myPage").slash(banner.getId()).withRel("update_banner"),
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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BannerApiController.class).slash("popup").withRel("query_popupBanners"));
        representationModel.add(linkTo(BannerApiController.class).slash("popup").slash(banner.getId()).withRel("update_banner"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-popupBanner").withRel("profile"));

        return ResponseEntity.created(createBannerUri).body(representationModel);
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
                linkTo(BannerApiController.class).slash("top").withRel("query_topBanner"),
                linkTo(BannerApiController.class).slash("top").slash(banner.getId()).withRel("update_banner"),
                profileRootUrlBuilder.slash("index.html#resources-create-topBanner").withRel("profile")
        );

        return ResponseEntity.created(createBannerUri).body(entityModel);
    }

    @GetMapping("/myPage")
    public ResponseEntity queryMyPageBanner() {

        List<MyPageBanner> myPageBanners = bannerRepository.findMyPageBanners();
        for (MyPageBanner myPageBanner : myPageBanners) {
            System.out.println(myPageBanner.getId() + " " + myPageBanner.getName());
        }

        Optional<MyPageBannerResponseDto> optional = bannerRepository.findFirstMyPageBanner();
        if (!optional.isPresent()) {
            return notFound();
        }

        MyPageBannerResponseDto responseDto = optional.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("myPage");

        EntityModel<MyPageBannerResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenamePc()).withRel("thumbnail_pc"),
                linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenameMobile()).withRel("thumbnail_mobile"),
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.slash(responseDto.getId()).withRel("update_banner"),
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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BannerApiController.class).slash("myPage").withRel("query_myPageBanner"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-myPageBanner").withRel("profile"));


        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/top")
    public ResponseEntity getTopBanner() {

        Optional<TopBannerResponseDto> optional = bannerRepository.findFirstTopBannerDto();
        if (!optional.isPresent()) {
            return notFound();
        }

        TopBannerResponseDto responseDto = optional.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("top");

        EntityModel<TopBannerResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.slash(responseDto.getId()).withRel("update_banner"),
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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BannerApiController.class).slash("top").withRel("query_topBanner"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-topBanner").withRel("profile"));


        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/main")
    public ResponseEntity queryMainBanners() {

        List<MainBannerListResponseDto> responseDtoList = bannerRepository.findMainBannersDtos();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("main");

        List<EntityModel<MainBannerListResponseDto>> entityModelList = new ArrayList<>();

        for (MainBannerListResponseDto responseDto : responseDtoList) {
            EntityModel<MainBannerListResponseDto> entityModel = EntityModel.of(responseDto,
                    linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenamePc()).withRel("thumbnail_pc"),
                    linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenameMobile()).withRel("thumbnail_mobile"),
                    selfLinkBuilder.slash("main").slash(responseDto.getId()).withRel("query_banner"),
                    selfLinkBuilder.slash(responseDto.getId()).withRel("update_banner"),
                    selfLinkBuilder.slash(responseDto.getId()).withRel("delete_banner"),
                    selfLinkBuilder.slash(responseDto.getId()).slash("up").withRel("mainBanner_leakedOrder_up"),
                    selfLinkBuilder.slash(responseDto.getId()).slash("down").withRel("mainBanner_leakedOrder_down")
            );

            entityModelList.add(entityModel);
        }


        CollectionModel<EntityModel<MainBannerListResponseDto>> collectionModel = CollectionModel.of(entityModelList,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("create_banner"),
                profileRootUrlBuilder.slash("index.html#resources-query-mainBanners").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/main/{id}")
    public ResponseEntity queryMainBanner(@PathVariable Long id) {

        Optional<MainBannerResponseDto> optionalResponseDto = bannerRepository.findMainBannerDtoById(id);
        if (!optionalResponseDto.isPresent()) {
            return notFound();
        }

        MainBannerResponseDto responseDto = optionalResponseDto.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("/main").slash(responseDto.getId());

        EntityModel<MainBannerResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenamePc()).withRel("thumbnail_pc"),
                linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenameMobile()).withRel("thumbnail_mobile"),
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update_banner"),
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

        RepresentationModel representationModel = new RepresentationModel();

        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("query_banner"));
        representationModel.add(linkTo(BannerApiController.class).slash("main").withRel("query_mainBanners"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-mainBanner").withRel("profile"));

        return ResponseEntity.ok(representationModel);
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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BannerApiController.class).slash("main").withRel("query_mainBanners"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-mainBanner-leakedOrder-up").withRel("profile"));

        return ResponseEntity.ok(representationModel);
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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BannerApiController.class).slash("main").withRel("query_mainBanners"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-mainBanner-leakedOrder-down").withRel("profile"));

        return ResponseEntity.ok(representationModel);
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
        representationModel.add(selfLinkBuilder.withRel("query_mainBanners"));
        representationModel.add(selfLinkBuilder.withRel("create_banner"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-mainBanner").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/popup")
    public ResponseEntity queryPopupBanners() {
        List<EntityModel<PopupBannerListResponseDto>> entityModelList = new ArrayList<>();
        List<PopupBannerListResponseDto> responseDtoList = bannerRepository.findPopupBannerDtos();

        for (PopupBannerListResponseDto responseDto : responseDtoList) {
            EntityModel<PopupBannerListResponseDto> entityModel = EntityModel.of(responseDto,
                    linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenamePc()).withRel("thumbnail_pc"),
                    linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenameMobile()).withRel("thumbnail_mobile"),
                    linkTo(BannerApiController.class).slash("popup").slash(responseDto.getId()).withRel("query_popupBanner"),
                    linkTo(BannerApiController.class).slash("popup").slash(responseDto.getId()).withRel("update_popupBanner"),
                    linkTo(BannerApiController.class).slash("popup").slash(responseDto.getId()).withRel("delete_popupBanner"),
                    linkTo(BannerApiController.class).slash("popup").slash(responseDto.getId()).slash("up").withRel("update_popupBanner_order_up"),
                    linkTo(BannerApiController.class).slash("popup").slash(responseDto.getId()).slash("down").withRel("update_popupBanner_order_down")
            );
            entityModelList.add(entityModel);
        }

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup");
        CollectionModel<EntityModel<PopupBannerListResponseDto>> collectionModel = CollectionModel.of(entityModelList,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("create_banner"),
                profileRootUrlBuilder.slash("index.html#resources-query-popupBanners").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/popup/{id}")
    public ResponseEntity queryPopupBanner(@PathVariable Long id) {

        Optional<PopupBannerResponseDto> optional = bannerRepository.findPopupBannerDtoById(id);
        if (!optional.isPresent()) {
            return notFound();
        }

        PopupBannerResponseDto responseDto = optional.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(BannerApiController.class).slash("popup").slash(id);

        EntityModel<PopupBannerResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenamePc()).withRel("thumbnail_pc"),
                linkTo(InfoController.class).slash("display").slash("banners?filename=s_" + responseDto.getFilenameMobile()).withRel("thumbnail_mobile"),
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update_banner"),
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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.withRel("query_banner"));
        representationModel.add(linkTo(BannerApiController.class).slash("popup").withRel("query_popupBanners"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-popupBanner").withRel("profile"));


        return ResponseEntity.ok(representationModel);
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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BannerApiController.class).slash("popup").withRel("query_popupBanners"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-popupBanner-leakedOrder-up").withRel("profile"));

        return ResponseEntity.ok(representationModel);
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

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(BannerApiController.class).slash("popup").withRel("query_popupBanners"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-popupBanner-leakedOrder-down").withRel("profile"));


        return ResponseEntity.ok(representationModel);
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
        representationModel.add(selfLinkBuilder.withRel("query_popupBanners"));
        representationModel.add(selfLinkBuilder.withRel("create_banner"));
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
