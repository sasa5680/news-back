package com.example.news.Integration;

import com.example.news.Utils;
import com.example.news.component.UUIDMap;
import com.example.news.dto.in.UserInDto;
import com.example.news.dto.out.UserOutDto;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockUser;
import com.example.news.repo.UserRepo;
import com.example.news.service.MailService;
import com.example.news.service.S3Service;
import com.example.news.service.SignUpCodeService;
import com.example.news.token.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserIntegrationTest {

    //    @Autowired
//    TestRestTemplate testRestTemplate;
    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepo userRepo;

    @MockBean
    SignUpCodeService signUpCodeService;

    @MockBean
    MailService mailService;

    @MockBean
    UUIDMap uuidMap;

    @MockBean
    S3Service s3Service;

    @Autowired
    TokenProvider tokenProvider;

    @Test
    public void signUpConfirmTest() throws Exception {

        //유저 생성 통합 테스트
        //given
        given(s3Service.upload(any())).willReturn(MockUser.userProfile);
        doNothing().when(mailService).sendConfirmEmail(any(), any());
        doNothing().when(uuidMap).putObject(any(), any());
        //given(signUpCodeService.isValidCode(MockUser.userEmail, MockUser.code)).willReturn(true);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/api/user/create")
                        .file(Utils.getMockMultipartFile("userProfile"))
                        .param("code", MockUser.code)
                        .param("userEmail", MockUser.userEmail)
                        .param("userName", MockUser.userName)
                        .param("userPw", MockUser.userPw)
                        .param("userIntro", MockUser.userIntro)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
//        UserOutDto userOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserOutDto.class);
//
//        assertNotEquals(0, userOutDto.getUserId());
//        assertEquals(MockUser.userEmail, userOutDto.getUserEmail());
//        assertEquals(MockUser.userName, userOutDto.getUserName());
//        assertEquals(MockUser.userProfile, userOutDto.getUserProfile());
//        assertEquals(MockUser.userIntro, userOutDto.getUserIntro());
//        assertEquals(false, userOutDto.isDeleted());
    }

    @DisplayName("email confirm test")
    @Test
    public void emailConfirmTest() throws Exception {

        //given
        UserInDto userInDto = MockUser.getUserInDto();
        given(uuidMap.getObject(any())).willReturn(Optional.of(userInDto));

        UserEntity userEntity = MockUser.getUserEntity();
        given(s3Service.upload(any())).willReturn(MockUser.userProfile);
        //given(userRepo.save(any())).willReturn(userEntity);

        String UUID = java.util.UUID.randomUUID().toString();

        MvcResult result = mvc.perform(get("/api/user/confirm/" + UUID))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserOutDto userOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserOutDto.class);

        assertNotEquals(0, userOutDto.getUserId());
        assertEquals(MockUser.userEmail, userOutDto.getUserEmail());
        assertEquals(MockUser.userName, userOutDto.getUserName());
        assertEquals(MockUser.userProfile, userOutDto.getUserProfile());
        assertEquals(MockUser.userIntro, userOutDto.getUserIntro());
        assertEquals(false, userOutDto.isDeleted());
    }

    @DisplayName("user read test")
    @Test
    public void userReadTest() throws Exception {

        //유저 읽기 테스트
        //set temp user
        UserEntity userEntity = userRepo.save(MockUser.getUserEntity());
        userEntity = userRepo.findByUserId(userEntity.getUserId());

        MvcResult result = mvc.perform(get("/api/user/" + userEntity.getUserId()))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserOutDto userOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserOutDto.class);

        assertEquals(userEntity.getUserId(), userOutDto.getUserId());
        assertEquals(userEntity.getUserEmail(), userOutDto.getUserEmail());
        assertEquals(userEntity.getUserName(), userOutDto.getUserName());
        assertEquals(userEntity.getUserProfile(), userOutDto.getUserProfile());
        assertEquals(userEntity.getUserIntro(), userOutDto.getUserIntro());
        assertEquals(userEntity.isDeleted(), userOutDto.isDeleted());

    }

    @DisplayName("user update test")
    @Test
    public void userUpdateTest() throws Exception {

        //givne
        //set temp user
        UserEntity userEntity = userRepo.save(MockUser.getUserEntity());
        userEntity = userRepo.findByUserId(userEntity.getUserId());

        when(s3Service.upload(any())).thenReturn(MockUser.newProfile);
        doNothing().when(s3Service).delete(any());

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        //PUT 매핑 + Multipart file 설정 + 헤더 설정
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/user/" + userEntity.getUserId());
        builder.with(request -> {
            request.setMethod("PUT");
            request.addHeader("Authorization", "Bearer " + token);

            return request;
        });

        ClassPathResource classPathResource = new org.springframework.core.io.ClassPathResource("test_image.jpg");
        MockMultipartFile userProfile = new MockMultipartFile("userProfile",
                classPathResource.getFilename(), "image/jpeg", classPathResource.getInputStream().readAllBytes());

        MvcResult result = mvc.perform(builder
                        .file(userProfile)
                        .param("userName", MockUser.newName)
                        .param("userIntro", MockUser.newIntro)
                )
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserOutDto userOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserOutDto.class);

        assertNotEquals(0, userOutDto.getUserId());
        assertEquals(MockUser.newName, userOutDto.getUserName());
        assertEquals(MockUser.newProfile, userOutDto.getUserProfile());
        assertEquals(MockUser.newIntro, userOutDto.getUserIntro());
    }

    @DisplayName("user Delete Test")
    @Test
    public void userDeleteTest() throws Exception {

        //givne
        //set temp user
        UserEntity userEntity = userRepo.save(MockUser.getUserEntity());
        userEntity = userRepo.findByUserId(userEntity.getUserId());

        //토큰 생성
        String token =
                tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId());

        MvcResult result = mvc.perform(delete("/api/user/" + userEntity.getUserId())
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        ObjectMapper objectMapper = new ObjectMapper();
        UserOutDto userOutDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserOutDto.class);

        assertEquals(true, userOutDto.isDeleted());
    }

    @DisplayName("이메일 중복 확인")
    @Test
    public void checkDupEmail() throws Exception {

        //중복 케이스
        //givne
        //set temp user
        UserEntity userEntity = userRepo.save(MockUser.getUserEntity());
        userEntity = userRepo.findByUserId(userEntity.getUserId());

        MvcResult result = mvc.perform(get("/api/user/email/" + userEntity.getUserEmail())
                )
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        String res = result.getResponse().getContentAsString();

        assertEquals("true", res);

        //중복이 아닌 케이스
        //given
        String email = "some random email";
        result = mvc.perform(get("/api/user/email/" + email))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        assertEquals("false", result.getResponse().getContentAsString());
    }

    @DisplayName("유저명 중복 확인")
    @Test
    public void checkDupName() throws Exception {

        //중복 케이스
        //givne
        //set temp user
        UserEntity userEntity = userRepo.save(MockUser.getUserEntity());
        userEntity = userRepo.findByUserId(userEntity.getUserId());

        MvcResult result = mvc.perform(get("/api/user/name/" + userEntity.getUserName())
                )
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        String res = result.getResponse().getContentAsString();

        assertEquals("true", res);

        //중복이 아닌 케이스
        //given
        String name = "some random name";
        result = mvc.perform(get("/api/user/name/" + name))
                .andExpect(status().isOk())
                .andReturn();// 1, 2

        assertEquals("false", result.getResponse().getContentAsString());
    }
}
