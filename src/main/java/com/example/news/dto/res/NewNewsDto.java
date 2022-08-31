package com.example.news.dto.res;

import com.example.news.entity.NewsEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
@Schema(description = "새 뉴스가 들어왔을 경우 알림")
public class NewNewsDto {

    private String newsTitle;
    private String newsId;

    public static NewNewsDto from(NewsEntity newsEntity, ModelMapper modelMapper){

        return modelMapper.map(newsEntity, NewNewsDto.class);
    }
}


