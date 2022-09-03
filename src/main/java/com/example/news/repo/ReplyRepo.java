package com.example.news.repo;

import com.example.news.entity.NewsEntity;
import com.example.news.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepo extends JpaRepository<ReplyEntity, Integer> {

    public Optional<ReplyEntity> findByReplyId(int replyId);
    public void deleteByReplyId(int replyId);
}
