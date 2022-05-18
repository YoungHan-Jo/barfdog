package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.ArticlesAdminDto;
import com.bi.barfdog.api.blogDto.BlogImageDto;
import com.bi.barfdog.api.blogDto.BlogSaveDto;
import com.bi.barfdog.api.blogDto.QueryArticlesAdminDto;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogImage;
import com.bi.barfdog.repository.ArticleRepository;
import com.bi.barfdog.repository.BlogImageRepository;
import com.bi.barfdog.repository.BlogRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogService {

    private final StorageService storageService;

    private final BlogRepository blogRepository;
    private final BlogImageRepository blogImageRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public BlogImageDto uploadFile(MultipartFile file) {

        ImgFilenamePath path = storageService.storeBlogImg(file);

        BlogImageDto blogImageDto = saveBlogImageAndGetBlogImageDto(path);

        return blogImageDto;
    }

    @Transactional
    public void saveBlog(BlogSaveDto requestDto) {
        Blog savedBlog = saveBlogAndReturn(requestDto);
        setBlogToBlogImages(requestDto, savedBlog);
    }



    public QueryArticlesAdminDto getArticlesAdmin() {
        List<ArticlesAdminDto> articlesAdminDto = articleRepository.findArticlesAdminDto();

        return null;
    }







    private BlogImageDto saveBlogImageAndGetBlogImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        BlogImage blogImage = BlogImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        BlogImage savedBlogImage = blogImageRepository.save(blogImage);

        String url = linkTo(InfoController.class).slash("display").slash("blogs?filename=" + filename).toString();

        BlogImageDto blogImageDto = BlogImageDto.builder()
                .id(savedBlogImage.getId())
                .url(url)
                .build();
        return blogImageDto;
    }














    private void setBlogToBlogImages(BlogSaveDto requestDto, Blog savedBlog) {
        for (Long blogImageId : requestDto.getBlogImageIdList()) {
            BlogImage blogImage = blogImageRepository.findById(blogImageId).get();
            blogImage.setBlog(savedBlog);
        }
    }

    private Blog saveBlogAndReturn(BlogSaveDto requestDto) {
        Blog blog = Blog.builder()
                .status(requestDto.getStatus())
                .title(requestDto.getTitle())
                .category(requestDto.getCategory())
                .contents(requestDto.getContents())
                .build();

        Blog savedBlog = blogRepository.save(blog);
        return savedBlog;
    }
}
