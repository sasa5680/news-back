package com.example.news.mockData;

import com.example.news.dto.req.ReplyInDto;
import lombok.Data;

@Data
public class MockReply {

    public static String replyContent = "some content";

    public static ReplyInDto getReplyInDto(int newsId){
        return ReplyInDto.builder().newsId(newsId).replyContent(replyContent).build();
    }
}
