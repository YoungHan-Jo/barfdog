package com.bi.barfdog.service;

import com.bi.barfdog.api.dto.MainBannerSaveRequestDto;
import com.bi.barfdog.domain.banner.ImgFile;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.banner.MainBanner;
import com.bi.barfdog.repository.BannerRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public Long saveMainBanner(MainBannerSaveRequestDto requestDto, MultipartFile pcFile, MultipartFile mobileFile) {

        ImgFilenamePath pcStore = storageService.store(pcFile);
        ImgFilenamePath mobileStore = storageService.store(mobileFile);

        String folder = pcStore.getFolder();
        String filenamePc = pcStore.getFilename();
        String filenameMobile = mobileStore.getFilename();

        List<MainBanner> results = bannerRepository.findAllMain();
        for (MainBanner result : results) {
            System.out.println("result = " + result.toString());
        }

        System.out.println("count = " + results.size());
        int nextNum = results.size() + 1;

        MainBanner mainbanner = MainBanner.builder()
                .name(requestDto.getName())
                .targets(requestDto.getTargets())
                .status(requestDto.getStatus())
                .leakedOrder(nextNum)
                .pcLinkUrl(requestDto.getPcLinkUrl())
                .mobileLinkUrl(requestDto.getMobileLinkUrl())
                .imgfile(new ImgFile(folder,filenamePc,filenameMobile))
                .build();

        return bannerRepository.save(mainbanner);
    }




}
