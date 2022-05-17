package com.bi.barfdog.service.file;

import com.bi.barfdog.domain.banner.ImgFilenamePath;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${spring.servlet.multipart.location}") // yml에 있는거 읽어 옴
    private String uploadRootPath;

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
        return getImgFilenamePath(file, "banners");
    }

    @Override
    public ImgFilenamePath storeDogProfilePic(MultipartFile file) {
        return getImgFilenamePath(file, "dogProfiles");
    }

    @Override
    public ImgFilenamePath storeRecipeImg(MultipartFile file) {
        return getImgFilenamePath(file, "recipes");
    }

    @Override
    public ImgFilenamePath storeItemImg(MultipartFile file) {
        return getImgFilenamePath(file, "items");
    }

    @Override
    public ImgFilenamePath storeItemContentImg(MultipartFile file) {
        return getImgFilenamePath(file, "itemContents");
    }

    @Override
    public ImgFilenamePath storeBlogImg(MultipartFile file) {
        return getImgFilenamePath(file,"blogs");
    }


    @Override
    public Stream<Path> loadAll() {
        try {
            Path root = Paths.get(uploadRootPath+ "/banners");
            return Files.walk(root, 1)
                    .filter(path -> !path.equals(root));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String category, String filename) {
        return Paths.get(uploadRootPath+ "/"+category).resolve(filename);
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

    private ImgFilenamePath getImgFilenamePath(String fileType, MultipartFile file) throws IOException {
        String uploadPath = uploadRootPath + "/"+ fileType;
        Path root = Paths.get(uploadPath);
        if (!Files.exists(root)) {
            init(fileType);
        }

        try (InputStream inputStream = file.getInputStream()) {
            UUID uuid = UUID.randomUUID();
            String filename = uuid.toString() + "_" + file.getOriginalFilename();
            Files.copy(inputStream,
                    root.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);

            ImgFilenamePath imgFilenamePath = ImgFilenamePath.builder()
                    .folder(uploadPath)
                    .filename(filename)
                    .build();

            if (fileType.equals("banners") || fileType.equals("blogs")) {
                File inFile = new File(uploadPath, uuid.toString() + "_" + file.getOriginalFilename());
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
