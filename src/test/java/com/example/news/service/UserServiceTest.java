package com.example.news.service;

import com.example.news.bean.ModelMapperBean;
import com.example.news.component.UUIDMap;
import com.example.news.dto.in.UserInDto;
import com.example.news.dto.in.UserUpdateDto;
import com.example.news.dto.out.UserOutDto;
import com.example.news.entity.UserEntity;
import com.example.news.mockData.MockUser;
import com.example.news.repo.UserRepo;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepo userRepo;

    @Mock
    S3Service s3Service;

    @Mock
    SignUpCodeService signUpCodeService;

    @Mock
    MailService mailService;

    @Spy
    @Autowired
    ModelMapperBean modelMapperBean;

    @Mock
    UUIDMap uuidMap;

    @DisplayName("create news service test")
    @Test
    public void acceptSignUpTest() throws IOException, MessagingException {

        //given
        UserInDto userInDto = MockUser.getUserInDto();

        doNothing().when(mailService).sendConfirmEmail(any(), any(), any());
        doNothing().when(uuidMap).putObject(any(), any());

        given(uuidMap.getObject(any())).willReturn(Optional.of(userInDto));

        //when
        String uuid = userService.acceptSignUp(userInDto);

        //then
        UserInDto userDtoFromMap = uuidMap.getObject(uuid).get();

        //then
        assertNotNull(userDtoFromMap);

        assertEquals(userInDto.getUserEmail(), userDtoFromMap.getUserEmail());
        assertEquals(userInDto.getUserName(), userDtoFromMap.getUserName());
        assertEquals(userInDto.getUserPw(), userDtoFromMap.getUserPw());
        assertEquals(userInDto.getUserIntro(), userDtoFromMap.getUserIntro());
        assertEquals(userInDto.getUserProfile(), userDtoFromMap.getUserProfile());



//        //given
//        //S3 프로필 업로드 가정
//        UserEntity userEntity = MockUser.getUserEntity();
//
//        given(s3Service.upload(any())).willReturn(MockUser.userProfile);
//        given(userRepo.save(any())).willReturn(userEntity);
//        given(signUpCodeService.isValidCode(any(), any())).willReturn(true);
//
//
//        //when
////        UserInDto userInDto = MockUser.getUserInDto();
////        UserOutDto userOutDto = userService.acceptSignUp(userInDto);
//
//        //then
//        assertEquals(userInDto.getUserEmail(), userOutDto.getUserEmail());
//        assertEquals(userInDto.getUserName(), userOutDto.getUserName());
//        assertEquals(userInDto.getUserIntro(), userOutDto.getUserIntro());
//        assertEquals(userEntity.getUserRole(), userOutDto.getUserRole());
//        assertEquals(userEntity.getUserProfile(), userOutDto.getUserProfile());
//        assertEquals(userEntity.isDeleted(), userOutDto.isDeleted());
//        assertEquals(userEntity.getCreatedAt(), userOutDto.getCreatedAt());
//        assertEquals(userEntity.getModifiedAt(), userOutDto.getModifiedAt());
    }

    @Test
    public void emailVerifyTest() throws MessagingException, IOException {

        //given
        //유저 등록
        UserInDto userInDto = MockUser.getUserInDto();
        given(uuidMap.getObject(any())).willReturn(Optional.of(userInDto));

        UserEntity userEntity = MockUser.getUserEntity();
        given(s3Service.upload(any())).willReturn(MockUser.userProfile);
        given(userRepo.save(any())).willReturn(userEntity);

        //when
        UUID uuid = UUID.randomUUID();
        UserOutDto userOutDto = userService.emailVerifyUUID(uuid.toString()).get();

        //then
        assertEquals(userInDto.getUserEmail(), userOutDto.getUserEmail());
        assertEquals(userInDto.getUserName(), userOutDto.getUserName());
        assertEquals(userInDto.getUserIntro(), userOutDto.getUserIntro());

    }

    @DisplayName("create news service test")
    @Test
    public void readUserTest(){

        //given
        int userId = 1;
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity.setUserId(userId);

        given(userRepo.findByUserId(userId)).willReturn(userEntity);

        UserOutDto userOutDto = userService.readUser(userEntity.getUserName());

        assertEquals(userOutDto.getUserId(), userEntity.getUserId());
        assertEquals(userOutDto.getUserEmail(), userEntity.getUserEmail());
        assertEquals(userOutDto.getUserName(), userEntity.getUserName());
        assertEquals(userOutDto.getUserIntro(), userEntity.getUserIntro());
        assertEquals(userOutDto.getUserRole(), userEntity.getUserRole());
        assertEquals(userOutDto.getUserProfile(), userEntity.getUserProfile());
        assertEquals(userOutDto.isDeleted(), userEntity.isDeleted());
        assertEquals(userEntity.getCreatedAt(), userOutDto.getCreatedAt());
        assertEquals(userEntity.getModifiedAt(), userOutDto.getModifiedAt());
    }

    @DisplayName("create news service test")
    @Test
    public void userUpdateTest() throws IOException {

        //given
        int userId = 1;
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity.setUserId(userId);

        when(s3Service.upload(any())).thenReturn(MockUser.newProfile);
        when(userRepo.findByUserId(userId)).thenReturn(userEntity);
        doNothing().when(s3Service).delete(any());

        //when
        UserUpdateDto userUpdateDto = MockUser.getUserUpdateDto();
        UserOutDto userOutDto = userService.updateUser(userId, userUpdateDto);

        //then
        assertEquals(userOutDto.getUserName(), userUpdateDto.getUserName());
        assertEquals(userOutDto.getUserIntro(), userUpdateDto.getUserIntro());
        assertEquals(userOutDto.getUserProfile(), MockUser.newProfile);

    }

    @DisplayName("유저 삭제 테스트")
    @Test
    public void deleteUserTest() {

        //given
        int userId = 1;
        UserEntity userEntity = MockUser.getUserEntity();
        userEntity.setUserId(userId);

        //when
        when(userRepo.findByUserId(userId)).thenReturn(userEntity);

        UserOutDto userOutDto = userService.deleteUser(userId);
    }
}
