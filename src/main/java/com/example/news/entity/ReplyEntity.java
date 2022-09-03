package com.example.news.entity;


import com.example.news.dto.req.ReplyInDto;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "reply")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ReplyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reply_id")
    private int replyId;

    @Column(name="news_id")
    private int newsId;

    private String replyContent;

    //작성자 레퍼런스
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    public static ReplyEntity from(ReplyInDto reply,UserEntity userEntity,NewsEntity newsEntity, ModelMapper modelMapper){
        ReplyEntity replyEntity = modelMapper.map(reply, ReplyEntity.class);

        replyEntity.setUser(userEntity);
        replyEntity.setNewsId(newsEntity.getNewsId());
        return replyEntity;
    }
}
