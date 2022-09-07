package com.bi.barfdog.service.file;


import com.bi.barfdog.domain.banner.ImgFilenamePath;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init(String fileType);

    ImgFilenamePath storeBannerImg(MultipartFile file);

    ImgFilenamePath storeDogPictureImg(MultipartFile file);

    ImgFilenamePath storeRecipeImg(MultipartFile file);

    ImgFilenamePath storeItemImg(MultipartFile file);

    ImgFilenamePath storeBlogImg(MultipartFile file);

    ImgFilenamePath storeEventImg(MultipartFile file);

    ImgFilenamePath storeReviewImg(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String category, String filename);

    Resource loadAsResource(String category, String filename);

    void deleteAll();

    void deleteUnknownFiles(String folder);


}
