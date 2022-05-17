package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.BlogImageDto;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.blog.BlogImage;
import com.bi.barfdog.repository.BlogImageRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogService {

    private final StorageService storageService;
    private final BlogImageRepository blogImageRepository;

    @Transactional
    public BlogImageDto uploadFile(MultipartFile file) {

        ImgFilenamePath path = storageService.storeBlogImg(file);

        String filename = path.getFilename();
        BlogImage blogImage = BlogImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        BlogImage savedBlogImage = blogImageRepository.save(blogImage);

        String url = linkTo(InfoController.class).slash("display").slash("blogs?filename=" + filename).toString();
        String thumbnailUrl = linkTo(InfoController.class).slash("display").slash("blogs?filename=s_" + filename).toString();

        BlogImageDto blogImageDto = BlogImageDto.builder()
                .id(savedBlogImage.getId())
                .url(url)
                .thumbnailUrl(thumbnailUrl)
                .build();

        return blogImageDto;
    }
}
