package com.example.news.service;

import com.example.news.bean.ModelMapperBean;
import com.example.news.dto.req.LoginDto;
import com.example.news.dto.res.LoginResDto;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockUser;
import com.example.news.repo.UserRepo;
import com.example.news.token.TokenProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @InjectMocks
    LoginService loginService;

    @Mock
    UserRepo userRepo;

    @Spy
    @Autowired
    ModelMapperBean modelMapperBean;

    @Mock
    TokenProvider tokenProvider;

    @DisplayName("로그인 테스트")
    @Test
    public void LoginTest() {

        //given
        String token = "mockToken";

        int userId = 1;
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity.setUserId(userId);

        given(userRepo.findByUserEmail(userEntity.getUserEmail())).willReturn(userEntity);
        given(tokenProvider.createToken(userEntity.getUserRole(), userEntity.getUserId())).willReturn(token);

        //when
        LoginDto loginDto = LoginDto.builder()
                .userEmail(userEntity.getUserEmail())
                .userPw(userEntity.getUserPw())
                .build();

        LoginResDto loginResDto = loginService.login(loginDto);

        assertEquals(userId, loginResDto.getUserId());
        assertEquals(token, loginResDto.getToken());
    }
}
