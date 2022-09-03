package com.example.news.service;

import com.example.news.bean.ModelMapperBean;
import com.example.news.dto.req.ReplyInDto;
import com.example.news.dto.res.ReplyOutDto;
import com.example.news.entity.NewsEntity;
import com.example.news.entity.ReplyEntity;
import com.example.news.entity.UserEntity;
import com.example.news.repo.NewsRepo;
import com.example.news.repo.ReplyRepo;
import com.example.news.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ReplyService {

    @Autowired
    NewsRepo newsRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ReplyRepo replyRepo;

    @Autowired
    ModelMapperBean modelMapperBean;

    @Transactional
    public ReplyOutDto createReply(ReplyInDto reply, int userId) {

        NewsEntity newsEntity = newsRepo.findByNewsId(reply.getNewsId());
        UserEntity userEntity = userRepo.findByUserId(userId);
        ReplyEntity replyEntity = ReplyEntity.from(reply, userEntity, newsEntity, modelMapperBean.modelMapper());

        newsEntity.getReply().add(replyEntity);

        replyRepo.save(replyEntity);

        return ReplyOutDto.from(replyEntity, modelMapperBean.modelMapper());
    }

    @Transactional
    public void deleteReply(int replyId){

        replyRepo.deleteByReplyId(replyId);
    }
}
