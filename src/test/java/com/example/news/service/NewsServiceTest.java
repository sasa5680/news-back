package com.example.news.service;

import com.example.news.Utils;
import com.example.news.bean.ModelMapperBean;
import com.example.news.dto.req.NewsInDto;
import com.example.news.dto.res.NewsOutDto;
import com.example.news.dto.res.NewsSimpleDto;
import com.example.news.entity.NewsEntity;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockNews;
import com.example.news.mockData.MockUser;
import com.example.news.repo.NewsRepo;
import com.example.news.repo.UserRepo;
import com.example.news.types.NewsMain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class NewsServiceTest {

    @InjectMocks
    NewsService newsService;

    @Mock
    S3Service s3Service;

    @Mock
    UserRepo userRepo;

    @Mock
    NewsRepo newsRepo;

    @Mock
    SocketService socketService;

    @Spy
    @Autowired
    ModelMapperBean modelMapperBean;

    //테스트용 엔티티, DB 저장됨
    UserEntity userEntity;
    NewsEntity newsEntity;

    @BeforeEach
    public void setUp(){

        int userId = 1;
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity.setUserId(userId);
        this.userEntity = userEntity;

        int newsId = 1;
        NewsEntity newsEntity = MockNews.getNewsEntity();
        newsEntity.setNewsId(newsId);
        newsEntity.setUser(userEntity);
        this.newsEntity = newsEntity;
    }

    @Test
    @DisplayName("create news service test")
    public void createNewsTest() throws IOException {

        //given
        int userId = 1;
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity.setUserId(userId);

        NewsEntity newsEntity = MockNews.getNewsEntity();
        newsEntity.setUser(userEntity);

        //S3 프로필 업로드 가정
        given(s3Service.upload(any())).willReturn("profileValue");
        given(userRepo.findByUserId(userId)).willReturn(userEntity);
        given(newsRepo.save(any())).willReturn(newsEntity);

        //when
        NewsInDto newsInDto = MockNews.getNewsInDto();
        NewsOutDto newsOutDto = newsService.createNews(userId, newsInDto);

        //then
        assertEquals(newsInDto.getNewsTitle(), newsOutDto.getNewsTitle());
        assertEquals(newsInDto.getNewsSubTitle(), newsOutDto.getNewsSubTitle());
        assertEquals(newsInDto.getNewsContent(), newsOutDto.getNewsContent());
        assertEquals(newsInDto.getNewsCate(), newsOutDto.getNewsCate());
        assertEquals(MockNews.newsProfile, newsOutDto.getNewsProfile());
        assertEquals(newsOutDto.getNewsLike(), 0);
        assertEquals(newsOutDto.getNewsView(), 0);
        assertNotNull(newsOutDto.getCreatedAt());
        assertNotNull(newsOutDto.getModifiedAt());
        assertNotNull(newsOutDto.getUser());
    }

    @DisplayName("뉴스 읽기 테스트")
    @Test
    public void newsReadTest(){

        //given
        int newsId = newsEntity.getNewsId();

        Set<NewsEntity> newsEntities = Set.of(newsEntity);

        given(newsRepo.findByNewsId(newsId)).willReturn(newsEntity);
        given(newsRepo.findFirst4ByNewsCateAndNewsIdNotOrderByNewsId(any(), anyInt())).willReturn(newsEntities);

        //when
        NewsOutDto newsOutDto = newsService.readNews(newsId);

        //then
        assertNotNull(newsOutDto);
        assertEquals(newsEntity.getNewsTitle(), newsOutDto.getNewsTitle());
        assertEquals(newsEntity.getNewsProfile(), newsOutDto.getNewsProfile());
        assertEquals(newsEntity.getNewsContent(), newsOutDto.getNewsContent());
        assertEquals(newsEntity.getNewsCate(), newsOutDto.getNewsCate());
        assertEquals(newsOutDto.getNewsLike(), 0);
        assertEquals(newsOutDto.getNewsView(), 0);
        assertEquals(newsEntity.getCreatedAt(), newsOutDto.getCreatedAt());
        assertEquals(newsEntity.getModifiedAt(), newsOutDto.getModifiedAt());
        assertNotNull(newsOutDto.getUser());
        assertEquals(newsEntity.isNewsApproved(), newsOutDto.isNewsApproved());
        assertEquals(newsEntity.getNewsMain(), newsOutDto.getNewsMain());
    }

    @DisplayName("뉴스 수정 테스트")
    @Test
    public void newsUpdateTest() throws IOException {

        //given
        int newsId = 1;

        String newTitle = "new Title";
        String newContent = "new Content";
        String newCate = "new Cate";
        String newProfile = " new Profile";

        NewsInDto newsInDto = NewsInDto.builder()
                .newsContent(newContent)
                .newsTitle(newTitle)
                .newsCate(newCate)
                .newsProfile(Utils.getMockMultipartFile("newsProfile"))
                .build();

        given(newsRepo.findByNewsId(newsId)).willReturn(newsEntity);
        given(newsRepo.save(any())).willReturn(newsEntity);
        given(s3Service.upload(any())).willReturn(newProfile);
        doNothing().when(s3Service).delete(any());

        //when
        NewsOutDto newsOutDto = newsService.updateNews(newsId, newsInDto);

        //then
        System.out.println(newsOutDto);
        assertEquals(newTitle, newsOutDto.getNewsTitle());
        assertEquals(newContent, newsOutDto.getNewsContent());
        assertEquals(newProfile, newsOutDto.getNewsProfile());
        assertEquals(newCate, newsOutDto.getNewsCate());
    }

    @DisplayName("뉴스 삭제 테스트")
    @Test
    public void newsDeleteTest() throws IOException {

        //given
        int newsId = newsEntity.getNewsId();
        doNothing().when(s3Service).delete(any());
        given(newsRepo.findByNewsId(newsId)).willReturn(newsEntity);
        doAnswer(invocation -> {
            given(newsRepo.findByNewsId(newsId)).willReturn(null);
            return null;
        }).when(newsRepo).delete(newsEntity);

        //when
        newsService.deleteNews(newsEntity.getNewsId());

        //then
        Assertions.assertNull(newsRepo.findByNewsId(newsId));
    }

    @DisplayName("뉴스 전체 가져오기 테스트")
    @Test
    public void readAllTest(){
        //given
        NewsEntity newsEntity = MockNews.getNewsEntity();
        newsEntity.setUser(MockUser.getUserEntity());
        List<NewsEntity> newsEntities = new ArrayList<>();
        newsEntities.add(newsEntity);
        Page<NewsEntity> newsEntityPage = new PageImpl(newsEntities);

        Pageable pageable = PageRequest.of(0, 10);

        given(newsRepo.findAll(pageable)).willReturn(newsEntityPage);

        //when
        Page<NewsSimpleDto> newsSimpleDtos = newsService.readAllNews(pageable);
        assertNotEquals(newsSimpleDtos.getTotalElements(), 0);
    }

    @Test
    public void readNewsTest() {

        //given
        NewsEntity newsEntity = MockNews.getNewsEntity();
        newsEntity.setUser(MockUser.getUserEntity());
        List<NewsEntity> newsEntities = new ArrayList<>();
        newsEntities.add(newsEntity);
        Page<NewsEntity> newsEntityPage = new PageImpl(newsEntities);

        given(newsRepo.getNews(anyBoolean(), any(), any(), any(), any())).willReturn(newsEntityPage);

        //when
        String query = MockNews.getNewsEntity().getNewsTitle();
        Page<NewsSimpleDto> newsSimpleDtos = newsService.readBySearch(false, null, null, null, PageRequest.of(0, 8));

        assertNotEquals(newsSimpleDtos.getTotalElements(), 0);

        newsSimpleDtos.map(newsSimpleDto -> {
            System.out.println(newsSimpleDto);
            assertEquals(newsSimpleDto.getUserName(), MockUser.userName);
            return null;
        });
    }

    @Test
    public void readMainNewsTest(){

        //given
        NewsEntity newsEntity = MockNews.getNewsEntity();
        newsEntity.setNewsMain(NewsMain.MAIN);
        newsEntity.setUser(MockUser.getUserEntity());
        List<NewsEntity> newsEntities = new ArrayList<>();
        newsEntities.add(newsEntity);

        Set<NewsEntity> entitySet = newsEntities.stream().collect(Collectors.toSet());

        given(newsRepo.getNewsEntitiesByNewsMainNot(any())).willReturn(entitySet);

        //when
        Set<NewsSimpleDto> newsSimpleDtos = newsService.readMainNews();
        assertNotEquals(newsSimpleDtos.size(), 0);
    }
}
