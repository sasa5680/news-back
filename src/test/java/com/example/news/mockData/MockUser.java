package com.example.news.mockData;

import com.example.news.dto.in.UserInDto;
import com.example.news.dto.in.UserUpdateDto;
import com.example.news.entity.UserEntity;
import com.example.news.repo.UserRepo;
import com.example.news.types.UserRole;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MockUser {

    public static String code = "123456";
    public static String userEmail = "someUser@naver.com";
    public static String userPw = "1234@dddddd";
    public static String userName = "John Doe";
    public static String userIntro = "some Intro";
    public static String userProfile = "profile";
    public static UserRole userRole = UserRole.ADMIN;

    //user Update
    public static String newIntro = "new Intro";
    public static String newName = "new name";
    public static String newProfile = "new Profile";

    public static MultiValueMap<String, Object> getRequestMap() {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        map.add("userEmail", userEmail);
        map.add("userPw", userPw);
        map.add("userName", userName);
        map.add("userIntro", userIntro);

        ClassPathResource classPathResource = new org.springframework.core.io.ClassPathResource("test_image.jpg");
        map.add("userProfile", classPathResource);

        return map;
    }

    public static UserInDto getUserInDto() throws IOException {

        UserInDto userInDto = new UserInDto();

        userInDto.setUserEmail(userEmail);
        userInDto.setUserPw(userPw);
        userInDto.setUserName(userName);
        userInDto.setUserIntro(userIntro);

        ClassPathResource classPathResource = new org.springframework.core.io.ClassPathResource("test_image.jpg");

        MockMultipartFile multipartFile = new MockMultipartFile(classPathResource.getFilename(),
                classPathResource.getFilename(), "image/jpeg", classPathResource.getInputStream().readAllBytes());

        userInDto.setUserProfile(multipartFile);

        return userInDto;
    }
//
//    public UserOutDto getUserOutDto(ModelMapper modelMapper) {
//
//        UserOutDto userOutDto = modelMapper.map(mockUser, UserOutDto.class);
//        return userOutDto;
//    }

    public static UserEntity getUserEntity() {

        UserEntity userEntity = new UserEntity();

        userEntity.setUserEmail(userEmail);
        userEntity.setUserPw(userPw);
        userEntity.setUserName(userName);
        userEntity.setUserIntro(userIntro);
        userEntity.setUserProfile(userProfile);
        userEntity.setUserRole(userRole);
        userEntity.setDeleted(false);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setModifiedAt(LocalDateTime.now());

        return userEntity;
    }

    public static UserEntity getUserEntity(UserRepo userRepo) {

        UserEntity userEntity = getUserEntity();
        userEntity = userRepo.save(userEntity);
        return userEntity;
    }

    public static UserUpdateDto getUserUpdateDto() throws IOException {

        UserUpdateDto userUpdateDto = new UserUpdateDto();

        userUpdateDto.setUserName(newName);
        userUpdateDto.setUserIntro(newIntro);

        ClassPathResource classPathResource = new org.springframework.core.io.ClassPathResource("test_image.jpg");

        MockMultipartFile multipartFile = new MockMultipartFile(classPathResource.getFilename(),
                classPathResource.getFilename(), "image/jpeg", classPathResource.getInputStream().readAllBytes());

        userUpdateDto.setUserProfile(multipartFile);

        return userUpdateDto;
    }
}

