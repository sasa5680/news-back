package com.example.news.Integration;

import com.example.news.dto.out.LoginResDto;
import com.example.news.dto.out.UserOutDto;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockUser;
import com.example.news.repo.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class LoginIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepo userRepo;

    @Test
    public void loginTest() throws Exception {

        //로그인 테스트
        //성공해야 한다.

        //임시 유저 생성
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity = userRepo.save(userEntity);

        String requestUrl = "/api/login/email";

        MvcResult result = mvc.perform(post(requestUrl)
                        .param("userEmail", userEntity.getUserEmail())
                        .param("userPw", userEntity.getUserPw()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        LoginResDto loginResDto = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResDto.class);

        assertEquals(userEntity.getUserId(), loginResDto.getUserId());
        assertNotNull(loginResDto.getToken());

        //로그인 실패 케이스 (이메일 오류)
        MvcResult resultFailEmail = mvc.perform(post(requestUrl)
                        .param("userEmail", "sasa5680@wrong.com")
                        .param("userPw", userEntity.getUserPw()))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();

        //로그인 실패 케이스 (비밀번호 오류)
        MvcResult resultFailPw = mvc.perform(post(requestUrl)
                        .param("userEmail", userEntity.getUserEmail())
                        .param("userPw", "some wrong value"))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }
}
