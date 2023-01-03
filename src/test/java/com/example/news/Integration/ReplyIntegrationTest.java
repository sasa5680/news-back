package com.example.news.Integration;

import com.example.news.dto.res.NewsOutDto;
import com.example.news.dto.res.ReplyOutDto;
import com.example.news.entity.NewsEntity;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockNews;
import com.example.news.mockData.MockReply;
import com.example.news.mockData.MockUser;
import com.example.news.repo.NewsRepo;
import com.example.news.repo.UserRepo;
import com.example.news.token.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class ReplyIntegrationTest {

    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    MockMvc mvc;
    @Autowired
    UserRepo userRepo;
    @Autowired
    NewsRepo newsRepo;

    UserEntity userEntity;
    NewsEntity newsEntity;

    @BeforeEach
    public void setUp(){

        //테스트용 news 를 만든다. (DB에 저장된 상태)
        userEntity = MockUser.getUserEntity(userRepo);
        newsEntity = MockNews.getNewsEntity(userEntity, newsRepo);
    }

    @Test
    public void createReplyTest() throws Exception {
        //토큰 생성
        String token =
                tokenProvider.createToken(this.userEntity.getUserRole(), userEntity.getUserId());

        System.out.println(newsEntity.getNewsId());

        //when
        MvcResult result = mvc.perform(post("/api/reply/create")
                        .header("Authorization", "Bearer "+token)
                        .param("replyContent", MockReply.replyContent)
                        .param("newsId", String.valueOf(newsEntity.getNewsId())))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ReplyOutDto replyOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), ReplyOutDto.class);

        assertEquals(MockReply.replyContent, replyOutDto.getReplyContent());

    }

}
