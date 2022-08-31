package com.example.news.dto.res;

import com.example.news.entity.UserEntity;
import com.example.news.types.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.modelmapper.ModelMapper;

@Data
@Schema(description = "로그인 결과 dto")
public class LoginResDto {

    private String token;

    private int userId;

    private String userEmail;

    private UserRole userRole;

    private String userName;

    private String userProfile;

    public static LoginResDto from(String token, UserEntity userEntity, ModelMapper modelMapper){

        LoginResDto loginResDto = modelMapper.map(userEntity, LoginResDto.class);
        loginResDto.setToken(token);

        return loginResDto;
    }
}
