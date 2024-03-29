package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.*;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.blog.*;
import com.bi.barfdog.repository.article.ArticleRepository;
import com.bi.barfdog.repository.blog.BlogImageRepository;
import com.bi.barfdog.repository.blog.BlogRepository;
import com.bi.barfdog.repository.blog.BlogThumbnailRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogService {

    private final StorageService storageService;

    private final BlogRepository blogRepository;
    private final BlogThumbnailRepository blogThumbnailRepository;
    private final BlogImageRepository blogImageRepository;
    private final ArticleRepository articleRepository;



    @Transactional
    public UploadedImageDto uploadThumbnail(MultipartFile file) {

        ImgFilenamePath path = storageService.storeBlogImg(file);

        UploadedImageDto blogImageDto = saveBlogThumbnailAndGetBlogImageDto(path);

        return blogImageDto;
    }


    @Transactional
    public UploadedImageDto uploadImage(MultipartFile file) {

        ImgFilenamePath path = storageService.storeBlogImg(file);

        UploadedImageDto blogImageDto = saveBlogImageAndGetBlogImageDto(path);

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


        Optional<Article> optionalArticle1 = articleRepository.findByNumber(1);
        if (optionalArticle1.isPresent()) {
            Article article = optionalArticle1.get();
            article.change(blog1);
        } else {
            saveArticle(blog1, 1);
        }

        Optional<Article> optionalArticle2 = articleRepository.findByNumber(2);
        if (optionalArticle2.isPresent()) {
            Article article = optionalArticle2.get();
            article.change(blog2);
        } else {
            saveArticle(blog2, 2);
        }
    }

    private void saveArticle(Blog blog1, int number) {
        Article article = Article.builder()
                .number(number)
                .blog(blog1)
                .build();
        articleRepository.save(article);
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

        BlogThumbnail blogThumbnail = blogThumbnailRepository.findById(requestDto.getThumbnailId()).get();
        blog.update(requestDto, blogThumbnail);

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


    private UploadedImageDto saveBlogImageAndGetBlogImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        BlogImage blogImage = BlogImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        BlogImage savedBlogImage = blogImageRepository.save(blogImage);

        String url = linkTo(InfoController.class).slash("display").slash("blogs?filename=" + filename).toString();

        UploadedImageDto blogImageDto = UploadedImageDto.builder()
                .id(savedBlogImage.getId())
                .url(url)
                .build();
        return blogImageDto;
    }

    private UploadedImageDto saveBlogThumbnailAndGetBlogImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        BlogThumbnail blogThumbnail = BlogThumbnail.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();
        BlogThumbnail savedThumbnail = blogThumbnailRepository.save(blogThumbnail);

        String url = linkTo(InfoController.class).slash("display").slash("blogs?filename=" + filename).toString();

        UploadedImageDto blogImageDto = UploadedImageDto.builder()
                .id(savedThumbnail.getId())
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
        BlogThumbnail blogThumbnail = blogThumbnailRepository.findById(requestDto.getThumbnailId()).get();

        Blog blog = Blog.builder()
                .status(requestDto.getStatus())
                .title(requestDto.getTitle())
                .category(requestDto.getCategory())
                .contents(requestDto.getContents())
                .blogThumbnail(blogThumbnail)
                .build();


        Blog savedBlog = blogRepository.save(blog);
        return savedBlog;
    }



}
