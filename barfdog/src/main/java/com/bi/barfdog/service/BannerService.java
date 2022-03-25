package com.bi.barfdog.service;

import com.bi.barfdog.api.dto.MainBannerSaveRequestDto;
import com.bi.barfdog.api.dto.MyPageBannerSaveRequestDto;
import com.bi.barfdog.api.dto.PopupBannerSaveRequestDto;
import com.bi.barfdog.api.dto.TopBannerSaveRequestDto;
import com.bi.barfdog.domain.banner.*;
import com.bi.barfdog.repository.BannerRepository;
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

        ImgFilenamePath pcStore = storageService.store(pcFile);
        ImgFilenamePath mobileStore = storageService.store(mobileFile);

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

        ImgFilenamePath pcStore = storageService.store(pcFile);
        ImgFilenamePath mobileStore = storageService.store(mobileFile);

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

        ImgFilenamePath pcStore = storageService.store(pcFile);
        ImgFilenamePath mobileStore = storageService.store(mobileFile);

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
    public MyPageBanner updateMyPageBanner(Long id, MyPageBannerSaveRequestDto requestDto, MultipartFile pcFile, MultipartFile mobileFile) {
        Optional<Banner> banner = bannerRepository.findById(id);

        ImgFile imgFile = getImgFile(pcFile, mobileFile);


        MyPageBanner myPageBanner = null;
        if(imgFile != null){
            myPageBanner = banner.updateBanner(requestDto, imgFile);
        }else{
            myPageBanner = banner.updateBanner(requestDto);
        }

        return myPageBanner;
    }

    private ImgFile getImgFile(MultipartFile pcFile, MultipartFile mobileFile) {

        if(pcFile != null && mobileFile != null){
            ImgFilenamePath pcStore = storageService.store(pcFile);
            ImgFilenamePath mobileStore = storageService.store(mobileFile);
            return new ImgFile(pcStore.getFolder(),pcStore.getFilename(),mobileStore.getFilename());
        }else if(pcFile != null){
            ImgFilenamePath pcStore = storageService.store(pcFile);
            return new ImgFile(pcStore.getFolder(),pcStore.getFilename(),null);
        }else if(mobileFile != null){
            ImgFilenamePath mobileStore = storageService.store(mobileFile);
            return new ImgFile(mobileStore.getFolder(),null,mobileStore.getFilename());
        }

        return null;
    }

    @Transactional
    public Banner updateTopBanner(Long id, TopBannerSaveRequestDto requestDto) {
        TopBanner banner = bannerRepository.findTopBannerById(id);

        banner.update(requestDto);

        return banner;
    }
}
