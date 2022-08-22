package com.example.news.repo;

import com.example.news.config.JpaAuditingConfig;
import com.example.news.entity.NewsEntity;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockNews;
import com.example.news.mockData.MockUser;
import com.example.news.types.NewsMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.junit5.SpringRunner;
import java.util.StringTokenizer;

import static org.junit.Assert.*;


//@RunWith(SpringRunner.class)
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NewsRepoTest {
    @Autowired
    NewsRepo newsRepo;

    @Autowired
    UserRepo userRepo;

    UserEntity userEntity;
    NewsEntity newsEntity;

    @BeforeEach
    void setUp() {
        //임시 유저 생성
        UserEntity userEntity = MockUser.getUserEntity();
        this.userEntity = userRepo.save(userEntity);

        //뉴스 생성
        NewsEntity newsEntity = MockNews.getNewsEntity();
        newsEntity.setUser(userEntity);
        this.newsEntity = newsRepo.save(newsEntity);
    }

    @Test
    public void save(){

        System.out.println(userEntity);
        NewsEntity newsEntity = MockNews.getNewsEntity();
        newsEntity.setUser(userEntity);

        NewsEntity savedEntity = newsRepo.save(newsEntity);

        assertEquals(savedEntity.getNewsTitle(), newsEntity.getNewsTitle());
        assertEquals(savedEntity.getNewsSubTitle(), newsEntity.getNewsSubTitle());
        assertEquals(savedEntity.getNewsContent(), newsEntity.getNewsContent());
        assertEquals(savedEntity.getNewsCate(), newsEntity.getNewsCate());
        assertEquals(savedEntity.getNewsProfile(), newsEntity.getNewsProfile());
        assertEquals(0, newsEntity.getNewsLike());
        assertEquals(0, newsEntity.getNewsView());
        assertEquals(MockNews.newsMain, savedEntity.getNewsMain());
        assertEquals(MockNews.newsApproved, savedEntity.isNewsApproved());
        assertNotNull(savedEntity.getCreatedAt());
        assertNotNull(savedEntity.getModifiedAt());

    }

    @Test
    public void findUserIdByNewsId(){
        int userId = newsRepo.findUserIdByNewsId(this.newsEntity.getNewsId());

        assertEquals(this.userEntity.getUserId(), userId);
    }

    @Test
    public void deleteByNewsId(){
        newsRepo.deleteByNewsId(newsEntity.getNewsId());
        NewsEntity newsEntity = newsRepo.findByNewsId(this.newsEntity.getNewsId());
        assertNull(newsEntity);
    }


    @Test
    public void getAllTest(){

        //뉴스를 전부 가져온다.
        Pageable pageable = PageRequest.of(0, 8);
        Page<NewsEntity> newsEntities = newsRepo.findAll(pageable);
        assertNotEquals(newsEntities.getTotalElements(), 0);

    }

    @Test
    public void getNewsByUserNameTest() {

        Pageable pageable = PageRequest.of(0, 8);

        newsEntity.setNewsApproved(true);

        Page<NewsEntity> newsEntities = newsRepo.getNewsEntitiesByUserName(this.userEntity.getUserName(), pageable);
        assertNotEquals(newsEntities.getTotalElements(), 0);
    }

    @Test
    public void getNewsUser() {

        Pageable pageable = PageRequest.of(0, 8);

//        //조건 없이 검색
        Page<NewsEntity> newsEntities = newsRepo.getNews(false, null, null, null, pageable);
        assertNotEquals(newsEntities.getTotalElements(), 0);

        //제목 검색
        String titleQuery = "Some";
        Page<NewsEntity> query = newsRepo.getNews(false, titleQuery, null, null, pageable);
        assertNotEquals(query.getTotalElements(), 0);


    }

    //뉴스의 메인 여부에 따라 가져온다.
    @Test
    public void getByMainTest(){

        //메인으로 설정
        newsEntity.setNewsMain(NewsMain.MAIN);
        newsRepo.save(newsEntity);

        NewsEntity mainNews = newsRepo.getByNewsMain(NewsMain.MAIN);
        assertEquals(NewsMain.MAIN, mainNews.getNewsMain());
    }
}
