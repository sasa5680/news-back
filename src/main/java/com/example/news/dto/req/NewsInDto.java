package com.example.news.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ToString
@Builder
@Schema(description = "뉴스 입력 dto")
public class NewsInDto {

    @Schema(description = "프로필 이미지 (MultiPart File)")
    @NotNull(message = "프로필 이미지 파일이 필요합니다!")
    private MultipartFile newsProfile;

    @Schema(description = "뉴스 제목")
    @Size(min = 5, max = 200, message = "뉴스 제목은 최소 5자 최대 200자 제한입니다!")
    private String newsTitle;

    @Schema(description = "뉴스 부제목")
    @Size(min = 10, max = 300, message = "뉴스 부제목은 최소 10자 최대 300자 제한입니다!")
    private String newsSubTitle;

    @Schema(description = "뉴스 카테고리")
    @NotBlank(message = "뉴스 카테고리를 입력해야 합니다!")
    private String newsCate;

    @Schema(description = "뉴스 내용, html 컨텐트")
    @Size(min = 100, message = "뉴스 내용은 최소 100자 이상입니다!")
    private String newsContent;
}
