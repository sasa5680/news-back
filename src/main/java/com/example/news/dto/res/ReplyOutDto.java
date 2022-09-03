package com.example.news.dto.res;

import com.example.news.dto.req.ReplyInDto;
import com.example.news.entity.ReplyEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Schema(description = "댓글 출력 dto")
public class ReplyOutDto {

    @Schema(description = "댓글 고유 번호")
    private int replyId;

    @Schema(description = "댓글 내용")
    private String replyContent;

    //작성자 정보
    @Schema(description = "작성자")
    private UserOutDto user;

    //작성시간
    @Schema(description = "댓글 작성시간")
    private LocalDateTime createdAt;

    public static ReplyOutDto from(ReplyEntity reply, ModelMapper modelMapper) {
        ReplyOutDto replyOutDto = modelMapper.map(reply, ReplyOutDto.class);
        replyOutDto.setUser(UserOutDto.from(reply.getUser(), modelMapper));

        return replyOutDto;
    }

    public static Set<ReplyOutDto> from(Set<ReplyEntity> replyEntities, ModelMapper modelMapper){

        Set<ReplyOutDto> replyOutDtos = new HashSet<>();

        for(ReplyEntity reply : replyEntities) {
            replyOutDtos.add(ReplyOutDto.from(reply, modelMapper));
        }

        return replyOutDtos;
    }
}


