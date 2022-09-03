package com.example.news.entity;


import com.example.news.dto.req.NewsInDto;
import com.example.news.types.NewsMain;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Set;

//유저가 뉴스 기사를 정의
@Entity
@Table(name = "news")
@Data
@EntityListeners(AuditingEntityListener.class)
public class NewsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int newsId;

    //뉴스 제목
    private String newsTitle;

    //뉴스 부제목
    private String newsSubTitle;

    //뉴스 프로필
    private String newsProfile;

    //뉴스 내용, HTMl 문서 형식
    private String newsContent;

    //뉴스 카테고리 (경제, 정치, 과학...)
    private String newsCate;

    //좋아요
    private int newsLike;

    //조회수
    private int newsView;

    //뉴스 승인됨 여부
    private boolean newsApproved;

    @Enumerated(EnumType.STRING)
    private NewsMain newsMain;

    //작성자 레퍼런스
    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity user;

    //작성자 레퍼런스
    @JoinColumn(name = "news_id")
    @OneToMany(fetch = FetchType.LAZY)
    private Set<ReplyEntity> reply;

    public static NewsEntity from(NewsInDto newsInDto, String newsProfile, UserEntity userEntity, ModelMapper modelMapper) {

        NewsEntity newsEntity = modelMapper.map(newsInDto, NewsEntity.class);
        if(userEntity != null) newsEntity.setNewsProfile(newsProfile);
        if(userEntity != null) newsEntity.setUser(userEntity);

        newsEntity.setNewsApproved(false);
        newsEntity.setNewsMain(NewsMain.NORMAL);

        return newsEntity;
    }


}
