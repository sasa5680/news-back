package com.example.news.Integration;

import com.example.news.Utils;
import com.example.news.dto.res.NewsOutDto;
import com.example.news.entity.NewsEntity;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockNews;
import com.example.news.mockData.MockUser;
import com.example.news.repo.NewsRepo;
import com.example.news.repo.UserRepo;
import com.example.news.service.S3Service;
import com.example.news.token.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class NewsIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepo userRepo;

    @Autowired
    NewsRepo newsRepo;

    UserEntity userEntity;
    NewsEntity newsEntity;

    @Autowired
    TokenProvider tokenProvider;

    @MockBean
    S3Service s3Service;

    @BeforeEach
    public void setUp(){

        //테스트용 news 를 만든다. (DB에 저장된 상태)
        userEntity = MockUser.getUserEntity(userRepo);
        newsEntity = MockNews.getNewsEntity(userEntity, newsRepo);
    }

    @Test
    public void NewsCreateTest() throws Exception {

        //뉴스 생성 통합 테스트

        //given
        given(s3Service.upload(any())).willReturn(MockNews.newsProfile);

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        //POST 매핑 + Multipart file 설정 + 헤더 설정
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/news/create");
        builder.with(request -> {
            request.setMethod("POST");
            request.addHeader("Authorization", "Bearer "+token);

            return request;
        });

        //when
        MvcResult result = mvc.perform(builder
                .file(Utils.getMockMultipartFile("newsProfile"))
                        .param("newsTitle", MockNews.newsTitle)
                        .param("newsSubTitle", MockNews.newsSubTitle)
                        .param("newsContent", MockNews.newContent)
                        .param("newsCate", MockNews.newsCate)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isCreated())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        NewsOutDto userOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), NewsOutDto.class);

        //then
        System.out.println(userOutDto);
        assertEquals(MockNews.newsTitle, userOutDto.getNewsTitle());
        assertEquals(MockNews.newContent, userOutDto.getNewsContent());
        assertEquals(MockNews.newsProfile, userOutDto.getNewsProfile());
        assertEquals(MockNews.newsCate, userOutDto.getNewsCate());
        assertNotNull(userOutDto.getUser());
    }

    @Test
    public void newsReadTest() throws Exception {

        MvcResult result = mvc.perform(get("/api/news/"+newsEntity.getNewsId()))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        NewsOutDto userOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), NewsOutDto.class);
        System.out.println(userOutDto);

        assertEquals(MockNews.newsTitle, userOutDto.getNewsTitle());
        assertEquals(MockNews.newsSubTitle, userOutDto.getNewsSubTitle());
        assertEquals(MockNews.newContent, userOutDto.getNewsContent());
        assertEquals(MockNews.newsProfile, userOutDto.getNewsProfile());
        assertEquals(MockNews.newsCate, userOutDto.getNewsCate());
        assertNotNull(userOutDto.getUser());
    }

    @Test
    public void newsUpdateTest() throws Exception {

        //뉴스 수정 통합 테스트

        //given
        String newTitle = "new Title bla bla bla bla bla bla bla bla";
        String newSubTitle = "new Subtitle bla bla bla bla bla bla bla bla";
        String newContent = "<p>new Content new Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Contentnew Content<p>";
        String newCate = "new Cate";
        String newProfile = " new Profile";

        given(s3Service.upload(any())).willReturn(newProfile);
        doNothing().when(s3Service).delete(any());

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        //PUT 매핑 + Multipart file 설정 + 헤더 설정
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/news/"+newsEntity.getNewsId());
        builder.with(request -> {
            request.setMethod("PUT");
            request.addHeader("Authorization", "Bearer "+token);

            return request;
        });

        //when
        MvcResult result = mvc.perform(builder
                        .file(Utils.getMockMultipartFile("newsProfile"))
                        .param("newsId", String.valueOf(this.newsEntity.getNewsId()))
                        .param("newsTitle", newTitle)
                        .param("newsSubTitle", newSubTitle)
                        .param("newsContent", newContent)
                        .param("newsCate", newCate)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        NewsOutDto newsOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), NewsOutDto.class);
        System.out.println(newsOutDto);

        assertEquals(newTitle, newsOutDto.getNewsTitle());
        assertEquals(newContent, newsOutDto.getNewsContent());
        assertEquals(newProfile, newsOutDto.getNewsProfile());
        assertEquals(newCate, newsOutDto.getNewsCate());
        assertNotNull(newsOutDto.getUser());
    }

    @Test
    public void newsDeleteTest() throws Exception {

        //given
        doNothing().when(s3Service).delete(any());

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        //when
        MvcResult result = mvc.perform(delete("/api/news/"+newsEntity.getNewsId())
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertNull(newsRepo.findByNewsId(newsEntity.getNewsId()));
    }

    @Test
    public void readNewsTest() throws Exception {

        MvcResult result = mvc.perform(get("/api/news/user"))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        System.out.println(result.getResponse().getContentAsString());
    }

    //일반 유저가 뉴스 가져오는 테스트
    @Test
    public void readAllTestUser() throws Exception {

        MvcResult result = mvc.perform(get("/api/news/user"))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        System.out.println(result.getResponse().getContentAsString());

        result = mvc.perform(get("/api/news/user?cate=IT"))
                .andExpect(status().isOk())
                .andReturn();// 1, 2
    }

    //어드민 권한으로 뉴스 가져오는 테스트
    @Test
    public void readAllTestAdmin() throws Exception {
        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        //when (조건 안 준 상태)
        MvcResult result = mvc.perform(get("/api/news/admin")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andReturn();
    }

    //뉴스를 승인하는 테스트
    @Test
    public void newsApproveTest() throws Exception {

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        //PUT 매핑 + Multipart file 설정 + 헤더 설정
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/news/approve/"+newsEntity.getNewsId()+"/?approved=true");
        builder.with(request -> {
            request.setMethod("PUT");
            request.addHeader("Authorization", "Bearer "+token);

            return request;
        });

        //when
        MvcResult result = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        NewsOutDto newsOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), NewsOutDto.class);
        System.out.println(newsOutDto);

        assertEquals(true, newsOutDto.isNewsApproved());
    }
    @Test
    public void readMainNewsTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/news/main"))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        //Set<NewsOutDto> newsOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), Set<NewsOutDto>.class);
        //System.out.println(newsOutDto);

        System.out.println(result.getResponse().getContentAsString());
    }

    //유저명으로 가져오는 테스트
    @Test
    public void readByUserNameTest() throws Exception {

        MvcResult result = mvc.perform(get("/api/news/usernews/"+MockUser.userName))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        //Page<NewsOutDto> newsOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), Page<NewsOutDto>.class);

    }
}
