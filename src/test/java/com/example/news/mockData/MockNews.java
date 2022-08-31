package com.example.news.mockData;

import com.example.news.dto.req.NewsInDto;
import com.example.news.entity.NewsEntity;
import com.example.news.entity.UserEntity;
import com.example.news.repo.NewsRepo;
import com.example.news.types.NewsMain;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

public class MockNews {

    public static String newsTitle = "Some News Title bla bla bla bla bla";
    public static String newsSubTitle = "News Sub Title bla bla bla bla bla";
    public static String newContent = "<p>text text text texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext texttext text</p>";
    public static String newsProfile = "profile";
    public static String newsCate = "IT";
    public static boolean newsApproved = false;
    public static NewsMain newsMain = NewsMain.NORMAL;

    public static NewsInDto getNewsInDto() throws IOException {

        ClassPathResource classPathResource = new org.springframework.core.io.ClassPathResource("test_image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(classPathResource.getFilename(),
                classPathResource.getFilename(), "image/jpeg", classPathResource.getInputStream().readAllBytes());

        NewsInDto newsInDto = NewsInDto.builder()
                .newsTitle(newsTitle)
                .newsSubTitle(newsSubTitle)
                .newsProfile(multipartFile)
                .newsContent(newContent)
                .newsCate(newsCate)
                .build();

        return newsInDto;
    }

    public static NewsEntity getNewsEntity() {

        NewsEntity newsEntity = new NewsEntity();

        newsEntity.setNewsTitle(newsTitle);
        newsEntity.setNewsSubTitle(newsSubTitle);
        newsEntity.setNewsContent(newContent);
        newsEntity.setNewsProfile(newsProfile);
        newsEntity.setNewsCate(newsCate);
        newsEntity.setNewsLike(0);
        newsEntity.setNewsView(0);
        newsEntity.setCreatedAt(LocalDateTime.now());
        newsEntity.setModifiedAt(LocalDateTime.now());
        newsEntity.setNewsApproved(newsApproved);
        newsEntity.setNewsMain(newsMain);

        return newsEntity;
    }

    public static NewsEntity getNewsEntity(UserEntity userEntity, NewsRepo newsRepo) {

        NewsEntity newsEntity = getNewsEntity();
        newsEntity.setUser(userEntity);
        newsEntity = newsRepo.save(newsEntity);
        return newsEntity;
    }

}
