package com.example.news.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "유저 회원가입 dto")
public class UserInDto {

    @Schema(description = "프로필 이미지 (MultiPart File)")
    private MultipartFile userProfile;

    @Schema(description = "유저 이메일")
    @Email(message = "이메일 형식이 아닙니다!")
    private String userEmail;

    @Schema(description = "유저 패스워드")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$",
            message = "비밀번호는 문자, 숫자, 특수문자 포함 8자 이상이어야 합니다!")
    private String userPw;

    @Schema(description = "유저 이름")
    @Size(max = 20, message = "유저 이름은 최대 20까지 가능합니다!")
    @Size(min = 1, message = "유저 이름은 최소 1자까지 가능합니다!")
    private String userName;

    @Schema(description = "유저 자기소개")
    @Size(max = 200, message = "소개는 최대 200자 까지 가능합니다!")
    private String userIntro;

//    @NotEmpty
//    private String code;
}
