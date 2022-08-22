package com.example.news.Integration;

import com.example.news.DataCreator;
import com.example.news.auth.UserDetails;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockUser;
import com.example.news.repo.UserRepo;
import com.example.news.token.TokenProvider;
import com.example.news.types.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class AuthIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepo userRepo;

    @Autowired
    TokenProvider tokenProvider;


    @DisplayName("auth fail test")
    @Test
    public void authFailTest() throws Exception {

        //인증이 필요한 컨트롤러 메서드에 접근한다.
        //실패를 반환해야 한다.

        mvc.perform(get("/api/test/auth") )
                .andExpect(status().is4xxClientError());      // 1, 2
    }

    @DisplayName("auth success test")
    @Test
    public void authSuccessTest() throws Exception {

        //인증이 필요한 컨트롤러 메서드에 접근한다.
        //성공을 반환해야 한다.

        //임시 유저 생성
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity = userRepo.save(userEntity);

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        mvc.perform(get("/api/test/auth")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk());      // 1, 2
    }
    @DisplayName("계정레벨 테스트")
    @Test
    public void adminAuthTest() throws Exception {

        //유저 권한으로 admin 권한 메서드에 접근한다
        //실패를 반환해야 한다.

        //임시 유저 생성
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity.setUserRole(UserRole.USER);
        userEntity = userRepo.save(userEntity);

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

         mvc.perform(get("/api/test/admin")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().is4xxClientError());      // 1, 2
    }

    @DisplayName("getUserDetailTest")
    @Test
    public void getUserDetailTest() throws Exception {

        //유저의 정보를 UserDetails 객체를 이용해 받아온다.
        //유저 정보를 받아와야 한다.

        //임시 유저 생성
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity = userRepo.save(userEntity);

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        MvcResult result = mvc.perform(get("/api/test/userdetails")
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        //결과 String을 객체로 매핑
        ObjectMapper objectMapper = new ObjectMapper();
        UserDetails userDetails = objectMapper.readValue(result.getResponse().getContentAsString(), UserDetails.class);

        System.out.println(userDetails);
        assertEquals(userDetails.getUserId(), userEntity.getUserId());
        assertEquals(userDetails.getUserRole(), userEntity.getUserRole());


    }

}
