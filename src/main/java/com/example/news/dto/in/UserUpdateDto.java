package com.example.news.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "유저 수정 dto")
public class UserUpdateDto {

    @Schema(description = "프로필 이미지 (MultiPart File)")
    private MultipartFile userProfile;

    @Schema(description = "유저 이름")
    @Size(max = 20, message = "유저 이름은 최대 20까지 가능합니다!")
    @Size(min = 1, message = "유저 이름은 최소 1자까지 가능합니다!")
    private String userName;

    @Schema(description = "유저 자기소개")
    @Size(max = 200, message = "소개는 최대 200자 까지 가능합니다!")
    private String userIntro;
}
