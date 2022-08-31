package com.example.news.dto.res;


import com.example.news.entity.NewsEntity;
import com.example.news.types.NewsMain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class NewsSimpleDto extends BaseDto{

    @Schema(description = "뉴스 고유 id")
    private int newsId;

    //뉴스 제목
    @Schema(description = "뉴스 제목")
    private String newsTitle;

    @Schema(description = "뉴스 부제목")
    private String newsSubTitle;

    @Schema(description = "뉴스 프로필 src")
    private String newsProfile;

    @Schema(description = "뉴스 카테고리")
    private String newsCate;

    @Schema(description = "작성자 이름")
    private String userName;

    @Schema(description = "뉴스 승인됨 여부")
    private boolean newsApproved;

    @Schema(description = "뉴스 메인 엽 : MAIN, MAINSUB, CATEMAIN, NORMAL")
    private NewsMain newsMain;

    public static NewsSimpleDto from(ModelMapper modelMapper, NewsEntity newsEntity){

        NewsSimpleDto newsOutDto = modelMapper.map(newsEntity, NewsSimpleDto.class);
        newsOutDto.setUserName(newsEntity.getUser().getUserName());
        return newsOutDto;
    }

    public static Page<NewsSimpleDto> from(ModelMapper modelMapper, Page<NewsEntity> newsEntities){
        return newsEntities.map(newsEntity -> NewsSimpleDto.from(modelMapper, newsEntity));
    }

    public static Set<NewsSimpleDto> from(ModelMapper modelMapper, Set<NewsEntity> newsEntities) {
        return newsEntities.stream().map(newsEntity -> NewsSimpleDto.from(modelMapper, newsEntity)).collect(Collectors.toSet());
    }
}
