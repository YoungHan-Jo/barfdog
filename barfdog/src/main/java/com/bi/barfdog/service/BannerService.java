package com.bi.barfdog.service;

import com.bi.barfdog.api.bannerDto.MainBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.MyPageBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.PopupBannerSaveRequestDto;
import com.bi.barfdog.api.bannerDto.TopBannerSaveRequestDto;
import com.bi.barfdog.domain.banner.*;
import com.bi.barfdog.repository.banner.BannerRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    private final ModelMapper modelMapper;

    private final StorageService storageService;

    /*
     * 배너 저장
     * */
    @Transactional
    public Banner saveMainBanner(MainBannerSaveRequestDto requestDto, MultipartFile pcFile, MultipartFile mobileFile) {

        ImgFilenamePath pcStore = storageService.storeBannerImg(pcFile);
        ImgFilenamePath mobileStore = storageService.storeBannerImg(mobileFile);

        String folder = pcStore.getFolder();
        String filenamePc = pcStore.getFilename();
        String filenameMobile = mobileStore.getFilename();

        List<MainBanner> results = bannerRepository.findMainBanners();

        int nextNum = results.size() + 1;

        MainBanner mainbanner = MainBanner.builder()
                .name(requestDto.getName())
                .targets(requestDto.getTargets())
                .status(requestDto.getStatus())
                .leakedOrder(nextNum)
                .pcLinkUrl(requestDto.getPcLinkUrl())
                .mobileLinkUrl(requestDto.getMobileLinkUrl())
                .imgFile(new ImgFile(folder,filenamePc,filenameMobile))
                .build();

        return bannerRepository.save(mainbanner);
    }


    @Transactional
    public Banner saveMyPageBanner(MyPageBannerSaveRequestDto requestDto, MultipartFile pcFile, MultipartFile mobileFile) {

        ImgFilenamePath pcStore = storageService.storeBannerImg(pcFile);
        ImgFilenamePath mobileStore = storageService.storeBannerImg(mobileFile);

        String folder = pcStore.getFolder();
        String filenamePc = pcStore.getFilename();
        String filenameMobile = mobileStore.getFilename();

        Banner myPageBanner = MyPageBanner.builder()
                .name(requestDto.getName())
                .status(requestDto.getStatus())
                .pcLinkUrl(requestDto.getPcLinkUrl())
                .mobileLinkUrl(requestDto.getMobileLinkUrl())
                .imgFile(new ImgFile(folder, filenamePc, filenameMobile))
                .build();

        return bannerRepository.save(myPageBanner);
    }

    @Transactional
    public Banner savePopupBanner(PopupBannerSaveRequestDto requestDto, MultipartFile pcFile, MultipartFile mobileFile) {

        ImgFilenamePath pcStore = storageService.storeBannerImg(pcFile);
        ImgFilenamePath mobileStore = storageService.storeBannerImg(mobileFile);

        String folder = pcStore.getFolder();
        String filenamePc = pcStore.getFilename();
        String filenameMobile = mobileStore.getFilename();

        List<PopupBanner> results = bannerRepository.findPopupBanners();

        int nextNum = results.size() + 1;

        PopupBanner popupBanner = PopupBanner.builder()
                .name(requestDto.getName())
                .status(requestDto.getStatus())
                .leakedOrder(nextNum)
                .position(requestDto.getPosition())
                .pcLinkUrl(requestDto.getPcLinkUrl())
                .mobileLinkUrl(requestDto.getMobileLinkUrl())
                .imgFile(new ImgFile(folder, filenamePc, filenameMobile))
                .build();

        return bannerRepository.save(popupBanner);
    }

    @Transactional
    public Banner saveTopBanner(TopBannerSaveRequestDto requestDto) {

        TopBanner topBanner = TopBanner.builder()
                .name(requestDto.getName())
                .pcLinkUrl(requestDto.getPcLinkUrl())
                .mobileLinkUrl(requestDto.getMobileLinkUrl())
                .status(requestDto.getStatus())
                .backgroundColor(requestDto.getBackgroundColor())
                .fontColor(requestDto.getFontColor())
                .build();

        return bannerRepository.save(topBanner);
    }

    @Transactional
    public TopBanner updateTopBanner(Long id, TopBannerSaveRequestDto requestDto) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);

        TopBanner banner = (TopBanner) optionalBanner.get();

        banner.update(requestDto);

        return banner;
    }

    @Transactional
    public MyPageBanner updateMyPageBanner(Long id, MyPageBannerSaveRequestDto requestDto, MultipartFile pcFile, MultipartFile mobileFile) {

        MyPageBanner banner = (MyPageBanner) bannerRepository.findById(id).get();

        ImgFile imgFile = saveFilesAndGetImgFile(pcFile, mobileFile);

        MyPageBanner myPageBanner = null;

        if(imgFile != null){
            myPageBanner = banner.updateBanner(requestDto, imgFile);
        }else{
            myPageBanner = banner.updateBanner(requestDto);
        }

        return myPageBanner;
    }

    @Transactional
    public MainBanner updateMainBanner(Long id, MainBannerSaveRequestDto requestDto, MultipartFile pcFile, MultipartFile mobileFile) {
        MainBanner banner = (MainBanner) bannerRepository.findById(id).get();

        ImgFile imgFile = saveFilesAndGetImgFile(pcFile, mobileFile);

        MainBanner mainBanner = null;

        if (imgFile != null) {
            mainBanner = banner.updateBanner(requestDto, imgFile);
        }else{
            mainBanner = banner.updateBanner(requestDto);
        }

        return mainBanner;
    }

    @Transactional
    public Banner mainBannerUp(Long id) {
        MainBanner bannerToUp = (MainBanner) bannerRepository.findById(id).get();
        MainBanner bannerToDown = bannerRepository.findMainBannerByOrder(bannerToUp.getLeakedOrder() - 1);

        bannerToUp.orderUp();
        bannerToDown.orderDown();

        return bannerToUp;
    }

    @Transactional
    public Banner mainBannerDown(Long id) {
        MainBanner bannerToDown = (MainBanner) bannerRepository.findById(id).get();
        MainBanner bannerToUp = bannerRepository.findMainBannerByOrder(bannerToDown.getLeakedOrder() + 1);

        bannerToDown.orderDown();
        bannerToUp.orderUp();

        return bannerToDown;

    }

    @Transactional
    public int deleteMainBanner(Long id) {
        MainBanner banner = (MainBanner) bannerRepository.findById(id).get();
        int order = banner.getLeakedOrder();
        bannerRepository.delete(banner);
        int count = bannerRepository.increaseOrdersUnderDeleteMainBanner(order);

        return count;
    }

    @Transactional
    public PopupBanner updatePopupBanner(Long id, PopupBannerSaveRequestDto requestDto, MultipartFile pcFile, MultipartFile mobileFile) {

        PopupBanner banner = (PopupBanner) bannerRepository.findById(id).get();


        ImgFile imgFile = saveFilesAndGetImgFile(pcFile, mobileFile);

        PopupBanner popupBanner = null;

        if (imgFile != null) {
            popupBanner = banner.updateBanner(requestDto, imgFile);
        }else{
            popupBanner = banner.updateBanner(requestDto);
        }

        return popupBanner;
    }

    @Transactional
    public Banner popupBannerUp(Long id) {
        PopupBanner bannerToUp = (PopupBanner) bannerRepository.findById(id).get();
        PopupBanner bannerToDown = bannerRepository.findPopupBannerByOrder(bannerToUp.getLeakedOrder() - 1);

        bannerToUp.orderUp();
        bannerToDown.orderDown();

        return bannerToUp;
    }

    @Transactional
    public Banner popupBannerDown(Long id) {
        PopupBanner bannerToDown = (PopupBanner) bannerRepository.findById(id).get();
        PopupBanner bannerToUp = bannerRepository.findPopupBannerByOrder(bannerToDown.getLeakedOrder() + 1);

        bannerToDown.orderDown();
        bannerToUp.orderUp();

        return bannerToDown;
    }

    @Transactional
    public int deletePopupBanner(Long id) {
        PopupBanner banner = (PopupBanner) bannerRepository.findById(id).get();
        int order = banner.getLeakedOrder();
        bannerRepository.delete(banner);
        int count = bannerRepository.increaseOrdersUnderDeletePopupBanner(order);

        return count;
    }

    private ImgFile saveFilesAndGetImgFile(MultipartFile pcFile, MultipartFile mobileFile) {

        if(pcFile != null && mobileFile != null){
            ImgFilenamePath pcStore = storageService.storeBannerImg(pcFile);
            ImgFilenamePath mobileStore = storageService.storeBannerImg(mobileFile);
            return new ImgFile(pcStore.getFolder(),pcStore.getFilename(),mobileStore.getFilename());
        }else if(pcFile != null){
            ImgFilenamePath pcStore = storageService.storeBannerImg(pcFile);
            return new ImgFile(pcStore.getFolder(),pcStore.getFilename(),null);
        }else if(mobileFile != null){
            ImgFilenamePath mobileStore = storageService.storeBannerImg(mobileFile);
            return new ImgFile(mobileStore.getFolder(),null,mobileStore.getFilename());
        }

        return null;
    }


}
