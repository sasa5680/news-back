package com.example.news.service;

import com.example.news.bean.ModelMapperBean;
import com.example.news.dto.in.NewsInDto;
import com.example.news.dto.out.NewNewsDto;
import com.example.news.dto.out.NewsOutDto;
import com.example.news.dto.out.NewsSimpleDto;
import com.example.news.entity.NewsEntity;
import com.example.news.entity.UserEntity;
import com.example.news.exception.NoAuthException;
import com.example.news.repo.NewsRepo;
import com.example.news.repo.UserRepo;
import com.example.news.types.NewsMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Set;

@Service
public class NewsService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    NewsRepo newsRepo;

    @Autowired
    S3Service s3Service;

    @Autowired
    SocketService socketService;

    @Autowired
    ModelMapperBean modelMapperBean;

    @Transactional
    public NewsOutDto createNews(int userId, NewsInDto newsInDto) throws IOException {

        UserEntity userEntity = userRepo.findByUserId(userId);

        //이미지 생성
        String profile = s3Service.upload(newsInDto.getNewsProfile());

        NewsEntity newsEntity = NewsEntity.from(newsInDto, profile, userEntity, modelMapperBean.modelMapper());
        newsEntity = newsRepo.save(newsEntity);

        //새 뉴스 알림
        socketService.sendMessage(NewNewsDto.from(newsEntity, modelMapperBean.modelMapper()));

        NewsOutDto newsOutDto = NewsOutDto.from(modelMapperBean.modelMapper(), newsEntity, null);
        return newsOutDto;
    }

    public NewsOutDto readNews(int newsId) {

        NewsEntity newsEntity = newsRepo.findByNewsId(newsId);
        Set<NewsEntity> relatedNewsEntities = newsRepo.findFirst4ByNewsCateAndNewsIdNotOrderByNewsId(newsEntity.getNewsCate(), newsId);
        return NewsOutDto.from(modelMapperBean.modelMapper(), newsEntity, relatedNewsEntities);
    }

    public Page<NewsSimpleDto> readByUserName(String userName, Pageable pageable) {

        Page<NewsEntity> newsEntities = newsRepo.getNewsEntitiesByUserName(userName, pageable);
        return NewsSimpleDto.from(modelMapperBean.modelMapper(), newsEntities);
    }


    public Page<NewsSimpleDto> readBySearch(Boolean approved, String query, String newsCate, String newsMain, Pageable pageable){

        Page<NewsEntity> newsEntities = newsRepo.getNews(approved, query, newsCate, newsMain, pageable);
        return NewsSimpleDto.from(modelMapperBean.modelMapper(), newsEntities);
    }

    public Set<NewsSimpleDto> readMainNews(){
        Set<NewsEntity> newsEntities = newsRepo.getNewsEntitiesByNewsMainNot(NewsMain.NORMAL);
        return NewsSimpleDto.from(modelMapperBean.modelMapper(), newsEntities);

    }

    public Page<NewsSimpleDto> readAllNews(Pageable pageable){

        Page<NewsEntity> newsEntities = newsRepo.findAll(pageable);
        return NewsSimpleDto.from(modelMapperBean.modelMapper(), newsEntities);
    }

    @Transactional
    public NewsOutDto updateNews(int newsId, NewsInDto newsInDto) throws IOException {

        NewsEntity newsEntity = newsRepo.findByNewsId(newsId);

        String newsProfile = newsEntity.getNewsProfile();

        if(newsInDto.getNewsProfile() != null) {
            s3Service.delete(newsProfile);
            newsProfile = s3Service.upload(newsInDto.getNewsProfile());
        }

        newsEntity.setNewsTitle(newsInDto.getNewsTitle());
        newsEntity.setNewsSubTitle(newsInDto.getNewsSubTitle());
        newsEntity.setNewsContent(newsInDto.getNewsContent());
        newsEntity.setNewsCate(newsInDto.getNewsCate());
        newsEntity.setNewsProfile(newsProfile);
        newsRepo.save(newsEntity);

        return NewsOutDto.from(modelMapperBean.modelMapper(), newsEntity, null);
    }

    @Transactional
    public void deleteNews(int newsId) throws IOException {

        NewsEntity newsEntity = newsRepo.findByNewsId(newsId);

        //프로필 삭제
        s3Service.delete(newsEntity.getNewsProfile());
        newsRepo.delete(newsEntity);
    }

    //뉴스를 승인한다.
    @Transactional
    public NewsOutDto setNewsApproved(int newsId, boolean approved) {

        NewsEntity newsEntity = newsRepo.findByNewsId(newsId);
        newsEntity.setNewsApproved(approved);

        if(approved == true) {socketService.sendMessage(NewNewsDto.from(newsEntity, modelMapperBean.modelMapper()));}

        return NewsOutDto.from(modelMapperBean.modelMapper(), newsEntity, null);
    }

    //뉴스 메인 여부를 변경한다.
    @Transactional
    public NewsOutDto setNewsMain(int newsId, NewsMain newsMain) throws Exception {

        NewsEntity newsEntity = newsRepo.findByNewsId(newsId);
        //승인되지 않은 뉴스면
        if(!newsEntity.isNewsApproved()) {
            throw new Exception();
        }

        NewsEntity main;

        if(newsMain == NewsMain.MAIN) {
            main = newsRepo.getByNewsMain(newsMain);
            main.setNewsMain(NewsMain.NORMAL);
        }
        else if(newsMain == NewsMain.CATEMAIN) {
            main = newsRepo.getByNewsMainAndNewsCate(newsMain, newsEntity.getNewsCate());
            main.setNewsMain(NewsMain.NORMAL);
        }

        newsEntity.setNewsMain(newsMain);

        return NewsOutDto.from(modelMapperBean.modelMapper(), newsEntity, null);
    }
}
