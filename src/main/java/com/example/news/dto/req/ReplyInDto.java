package com.example.news.dto.req;

import com.example.news.dto.res.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ToString
@Builder
@Schema(description = "댓글 입력 dto")
public class ReplyInDto extends BaseDto {

    private int newsId;

    @Schema(description = "댓글 내용")
    @NotEmpty(message = "답글 내용이 있어야 합니다!")
    //@Max(message = "최대 200자 입니다!", value = 200)
    private String replyContent;
}
