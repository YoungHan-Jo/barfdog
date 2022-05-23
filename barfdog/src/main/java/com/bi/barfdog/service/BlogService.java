package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.*;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.blog.Article;
import com.bi.barfdog.domain.blog.Blog;
import com.bi.barfdog.domain.blog.BlogCategory;
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
    public BlogImageAdminDto uploadFile(MultipartFile file) {

        ImgFilenamePath path = storageService.storeBlogImg(file);

        BlogImageAdminDto blogImageDto = saveBlogImageAndGetBlogImageDto(path);

        return blogImageDto;
    }

    @Transactional
    public void saveBlog(BlogSaveDto requestDto) {
        Blog savedBlog = saveBlogAndReturn(requestDto);
        setBlogToBlogImages(requestDto.getBlogImageIdList(), savedBlog);
    }



    public QueryArticlesAdminDto getArticlesAdmin() {
        List<ArticlesAdminDto> articlesAdminDtos = articleRepository.findArticlesAdminDto();
        List<BlogTitlesDto> titleDtos = blogRepository.findTitleDtosForArticles();
        QueryArticlesAdminDto queryArticlesAdminDto = QueryArticlesAdminDto.builder()
                .articlesAdminDtos(articlesAdminDtos)
                .blogTitlesDtos(titleDtos)
                .build();
        return queryArticlesAdminDto;
    }

    @Transactional
    public void updateArticles(UpdateArticlesRequestDto requestDto) {
        Blog blog1 = blogRepository.findById(requestDto.getFirstBlogId()).get();
        Blog blog2 = blogRepository.findById(requestDto.getSecondBlogId()).get();

        Article article1 = articleRepository.findByNumber(1).get();
        Article article2 = articleRepository.findByNumber(2).get();

        article1.change(blog1);
        article2.change(blog2);
    }

    public QueryAdminBlogDto findQueryAdminBlogDtoById(Long id) {

        BlogAdminDto blogAdminDto = blogRepository.findAdminDtoById(id);
        List<AdminBlogImageDto> adminBlogImageDtos = blogImageRepository.findAdminDtoByBlogId(id);


        QueryAdminBlogDto queryAdminBlogDto = QueryAdminBlogDto.builder()
                .blogAdminDto(blogAdminDto)
                .adminBlogImageDtos(adminBlogImageDtos)
                .build();

        return queryAdminBlogDto;
    }

    public QueryAdminNoticeDto findQueryAdminNoticeDtoById(Long id) {

        NoticeAdminDto noticeAdminDto = blogRepository.findAdminNoticeDtoById(id);
        List<AdminBlogImageDto> adminBlogImageDtos = blogImageRepository.findAdminDtoByBlogId(id);

        QueryAdminNoticeDto queryAdminNoticeDto = QueryAdminNoticeDto.builder()
                .noticeAdminDto(noticeAdminDto)
                .adminBlogImageDtos(adminBlogImageDtos)
                .build();

        return queryAdminNoticeDto;
    }

    @Transactional
    public void updateBlog(Long id, UpdateBlogRequestDto requestDto) {
        Blog blog = blogRepository.findById(id).get();

        blog.update(requestDto);

        setBlogToBlogImages(requestDto.getAddImageIdList(), blog);

        blogImageRepository.deleteAllById(requestDto.getDeleteImageIdList());

    }

    @Transactional
    public void updateNotice(Long id, UpdateNoticeRequestDto requestDto) {
        Blog blog = blogRepository.findById(id).get();

        blog.update(requestDto);

        setBlogToBlogImages(requestDto.getAddImageIdList(), blog);

        blogImageRepository.deleteAllById(requestDto.getDeleteImageIdList());

    }

    @Transactional
    public void deleteBlog(Long id) {

        blogImageRepository.deleteAllByBlogId(id);
        blogRepository.deleteById(id);

    }

    @Transactional
    public void saveNotice(NoticeSaveDto requestDto) {

        Blog savedBlog = saveNoticeAndReturn(requestDto);
        setBlogToBlogImages(requestDto.getNoticeImageIdList(), savedBlog);

    }

    private Blog saveNoticeAndReturn(NoticeSaveDto requestDto) {
        Blog notice = Blog.builder()
                .status(requestDto.getStatus())
                .title(requestDto.getTitle())
                .category(BlogCategory.NOTICE)
                .contents(requestDto.getContents())
                .build();
        return blogRepository.save(notice);
    }


    private BlogImageAdminDto saveBlogImageAndGetBlogImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        BlogImage blogImage = BlogImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        BlogImage savedBlogImage = blogImageRepository.save(blogImage);

        String url = linkTo(InfoController.class).slash("display").slash("blogs?filename=" + filename).toString();

        BlogImageAdminDto blogImageDto = BlogImageAdminDto.builder()
                .id(savedBlogImage.getId())
                .url(url)
                .build();
        return blogImageDto;
    }












    private void setBlogToBlogImages(List<Long> idList, Blog savedBlog) {
        for (Long blogImageId : idList) {
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
