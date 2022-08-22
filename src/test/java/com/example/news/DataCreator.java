package com.example.news;

import com.example.news.dto.in.NewsInDto;
import com.example.news.dto.out.NewsOutDto;
import com.example.news.entity.NewsEntity;

public class DataCreator {

    public static NewsInDto getNewsInDto() {

        NewsInDto newsInDto = NewsInDto.builder()
                .newsTitle("newsTitle")
                .newsContent("<p>content</p>")
                .newsCate("IT")
                .build();

        return newsInDto;
    }

    public static NewsOutDto getNewsOutDto() {

        NewsOutDto newsOutDto = NewsOutDto.builder()
                .newsTitle("newsTitle")
                .newsProfile("profile")
                .newsContent("<p>content</p>")
                .newsCate("IT")
                .build();

        return newsOutDto;
    }

    public static NewsEntity getNewsEntity() {

        NewsEntity newsEntity = new NewsEntity();

        newsEntity.setNewsTitle("Title");
        newsEntity.setNewsCate("IT");
        newsEntity.setNewsContent("Content");
        newsEntity.setNewsProfile("profile");
        newsEntity.setNewsLike(0);
        newsEntity.setNewsView(0);

        return newsEntity;
    }

}


