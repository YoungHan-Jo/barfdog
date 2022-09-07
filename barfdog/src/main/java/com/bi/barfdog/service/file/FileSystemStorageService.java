package com.bi.barfdog.service.file;

import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.repository.ReviewImageRepository;
import com.bi.barfdog.repository.banner.BannerRepository;
import com.bi.barfdog.repository.blog.BlogImageRepository;
import com.bi.barfdog.repository.blog.BlogThumbnailRepository;
import com.bi.barfdog.repository.dog.DogPictureRepository;
import com.bi.barfdog.repository.event.EventImageRepository;
import com.bi.barfdog.repository.event.EventThumbnailRepository;
import com.bi.barfdog.repository.item.ItemContentImageRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FileSystemStorageService implements StorageService {

    @Value("${spring.servlet.multipart.location}") // yml에 있는거 읽어 옴
    private String uploadRootPath;

    private final BlogImageRepository blogImageRepository;
    private final BlogThumbnailRepository blogThumbnailRepository;
    private final DogPictureRepository dogPictureRepository;
    private final RecipeRepository recipeRepository;
    private final ItemContentImageRepository itemContentImageRepository;
    private final ItemImageRepository itemImageRepository;
    private final EventImageRepository eventImageRepository;
    private final EventThumbnailRepository eventThumbnailRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final BannerRepository bannerRepository;

    @Override
    public void init(String fileType) {
        try {
            String uploadPath = uploadRootPath + "/" + fileType;
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    @Override
    public ImgFilenamePath storeBannerImg(MultipartFile file) {
        return getImgFilenamePath(file, FolderName.BANNER);
    }

    @Override
    public ImgFilenamePath storeDogPictureImg(MultipartFile file) {
        return getImgFilenamePath(file, FolderName.DOG);
    }

    @Override
    public ImgFilenamePath storeRecipeImg(MultipartFile file) {
        return getImgFilenamePath(file, FolderName.RECIPE);
    }

    @Override
    public ImgFilenamePath storeItemImg(MultipartFile file) {
        return getImgFilenamePath(file, FolderName.ITEM);
    }

    @Override
    public ImgFilenamePath storeBlogImg(MultipartFile file) {
        return getImgFilenamePath(file,FolderName.BLOG);
    }

    @Override
    public ImgFilenamePath storeEventImg(MultipartFile file) {
        return getImgFilenamePath(file, FolderName.EVENT);
    }

    @Override
    public ImgFilenamePath storeReviewImg(MultipartFile file) {
        return getImgFilenamePath(file, FolderName.REVIEW);
    }


    @Override
    public Stream<Path> loadAll() {
        try {
            Path root = Paths.get(uploadRootPath + "/" + FolderName.BANNER);
            return Files.walk(root, 1)
                    .filter(path -> !path.equals(root));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String category, String filename) {
        return Paths.get(uploadRootPath + "/" + category).resolve(filename);
    }


    @Override
    public Resource loadAsResource(String category, String filename) {
        try {
            Path file = load(category, filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void deleteUnknownFiles(String folder) {

        List<String> filenamesDB = getFilenamesDB(folder);

        List<String> fileNames = getFileNames(folder);

        List<String> removeFiles = new ArrayList<>();

        for (String fileName : fileNames) {
            if (!filenamesDB.contains(fileName)) {
                removeFiles.add(fileName);
            }
        }
    }

    private List<String> getFilenamesDB(String folder) {

        List<String> filenameDB = new ArrayList<>();

        if (folder.equals(FolderName.BLOG)) {
            filenameDB = blogImageRepository.findFilename();
            filenameDB.addAll(blogThumbnailRepository.findFilename());
        } else if (folder.equals(FolderName.DOG)) {
            filenameDB = dogPictureRepository.findFilename();
        } else if (folder.equals(FolderName.RECIPE)) {
            filenameDB = recipeRepository.findFilename1();
            filenameDB.addAll(recipeRepository.findFilename2());
        } else if (folder.equals(FolderName.ITEM)) {
            filenameDB = itemContentImageRepository.findFilename();
            filenameDB.addAll(itemImageRepository.findFilename());
        } else if (folder.equals(FolderName.EVENT)) {
            filenameDB = eventImageRepository.findFilename();
            filenameDB.addAll(eventThumbnailRepository.findFilename());
        } else if (folder.equals(FolderName.REVIEW)) {
            filenameDB = reviewImageRepository.findFilename();
        } else if (folder.equals(FolderName.BANNER)) {
            filenameDB = bannerRepository.findMainFilenameMobile();
            filenameDB.addAll(bannerRepository.findMainFilenamePC());
            filenameDB.addAll(bannerRepository.findMyPageFilenameMobile());
            filenameDB.addAll(bannerRepository.findMyPageFilenamePC());
            filenameDB.addAll(bannerRepository.findPopupFilenameMobile());
            filenameDB.addAll(bannerRepository.findPopupFilenamePC());
        }
        return filenameDB;
    }

    private List<String> getFileNames(String folder) {
        File dirFile = new File(uploadRootPath + "/" + folder);
        File[] fileList = dirFile.listFiles();

        List<String> retList = new ArrayList<>();

        for (File file : fileList) {
            retList.add(file.getName());
        }

        return retList;
    }

    private ImgFilenamePath getImgFilenamePath(String fileType, MultipartFile file) throws IOException {
        String uploadPath = uploadRootPath + "/"+ fileType;
        Path root = Paths.get(uploadPath);
        if (!Files.exists(root)) {
            init(fileType);
        }

        try (InputStream inputStream = file.getInputStream()) {
            UUID uuid = UUID.randomUUID();
            String originalFilename = file.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".");
            String imgType = originalFilename.substring(index);
            String filename = uuid.toString() + imgType;
            Files.copy(inputStream,
                    root.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);

            ImgFilenamePath imgFilenamePath = ImgFilenamePath.builder()
                    .folder(uploadPath)
                    .filename(filename)
                    .build();

            if (fileType.equals(FolderName.BANNER)) {
                File inFile = new File(uploadPath, filename);
                File outFile = new File(uploadPath, "s_" + filename);

                Thumbnailator.createThumbnail(inFile, outFile, 400, 400);
            }

            return imgFilenamePath;
        }
    }

    private ImgFilenamePath getImgFilenamePath(MultipartFile file, String imageType) {
        try {
            if (file.isEmpty()) {
                throw new Exception("ERROR : File is empty.");
            }
            return getImgFilenamePath(imageType, file);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }




}
