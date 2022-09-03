package com.example.news.dto.res;

import com.example.news.entity.NewsEntity;
import com.example.news.types.NewsMain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.Set;

@Data
@NoArgsConstructor
@Schema(description = "뉴스 상세 out dto")
public class NewsOutDto extends BaseDto{

    @Schema(description = "뉴스 고유 id")
    private int newsId;

    //뉴스 제목
    @Schema(description = "뉴스 제목")
    private String newsTitle;

    @Schema(description = "뉴스 부제목")
    private String newsSubTitle;

    @Schema(description = "뉴스 프로필 src")
    private String newsProfile;

    @Schema(description = "뉴스 내용 (html String)")
    private String newsContent;

    @Schema(description = "뉴스 카테고리")
    private String newsCate;

    //좋아요
    @Schema(description = "뉴스 좋아요 수")
    private int newsLike;

    //조회수
    @Schema(description = "뉴스 조회수")
    private int newsView;

    //뉴스 승인됨 여부
    @Schema(description = "뉴스 승인됨 여부")
    private boolean newsApproved;

    //뉴스 메인 여부
    @Schema(description = "뉴스 메인 여부")
    private NewsMain newsMain;

    //작성자 정보
    @Schema(description = "작성자")
    private UserOutDto user;

    //연관 뉴스들
    @Schema(description = "연관 뉴스들")
    private Set<NewsSimpleDto> relatedNews;

    //댓글들
    @Schema(description = "댓글들")
    private Set<ReplyOutDto> reply;


    public static NewsOutDto from(ModelMapper modelMapper, NewsEntity newsEntity, Set<NewsEntity> relatedNews){

        NewsOutDto newsOutDto = modelMapper.map(newsEntity, NewsOutDto.class);
        newsOutDto.setUser(modelMapper.map(newsEntity.getUser(), UserOutDto.class));
        if(relatedNews != null) newsOutDto.setRelatedNews(NewsSimpleDto.from(modelMapper, relatedNews));
        newsOutDto.setReply(ReplyOutDto.from(newsEntity.getReply(), modelMapper));
        return newsOutDto;
    }

}
