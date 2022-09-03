package com.example.news.service;


import com.example.news.bean.ModelMapperBean;
import com.example.news.dto.req.ReplyInDto;
import com.example.news.dto.res.ReplyOutDto;
import com.example.news.mockData.MockNews;
import com.example.news.mockData.MockUser;
import com.example.news.repo.NewsRepo;
import com.example.news.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {

    @InjectMocks
    ReplyService replyService;

    @Mock
    UserRepo userRepo;

    @Mock
    NewsRepo newsRepo;

    @Spy
    @Autowired
    ModelMapperBean modelMapperBean;

    @Test
    public void createReplyTest() {

        given(userRepo.findByUserName(any())).willReturn(MockUser.getUserEntity());
        given(newsRepo.findByNewsId(anyInt())).willReturn(MockNews.getNewsEntity());

        ReplyInDto reply = ReplyInDto.builder().newsId(1).replyContent("asdasd").build();

        ReplyOutDto replyOutDto = replyService.createReply(reply, 0);

        System.out.println(replyOutDto);
    }
}
