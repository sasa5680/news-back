package com.example.news.entity;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    //생성일자
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //변경일자
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

}