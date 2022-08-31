package com.example.news.dto.res;

import com.example.news.entity.UserEntity;
import com.example.news.types.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@Schema(description = "유저 정보를 내보내는 Dto")
public class UserOutDto extends BaseDto{

    @Schema(description = "유저 고유 id")
    private int userId;

    @Schema(description = "유저 이메일")
    private String userEmail;

    @Schema(description = "유저명")
    private String userName;

    @Schema(description = "유저 프로파일 링크 (src)")
    private String userProfile;

    @Schema(description = "유저 소개")
    private String userIntro;

    @Schema(description = "유저 역할")
    private UserRole userRole;

    //작성시간
    @Schema(description = "뉴스 작성시간")
    private LocalDateTime createdAt;

    @Schema(description = "탈퇴한 유저 여부")
    private boolean isDeleted;

    public static UserOutDto from(UserEntity userEntity, ModelMapper modelMapper) {

        UserOutDto userOutDto = modelMapper.map(userEntity, UserOutDto.class);
        return userOutDto;
    }
}
