package com.example.news.entity;


import com.example.news.dto.in.UserInDto;
import com.example.news.types.UserRole;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

//유저가 뉴스 기사를 정의
@Entity
@Table(name = "user")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String userEmail;

    private String userPw;

    private String userName;

    private String userProfile;

    private String userIntro;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean isDeleted;

    public static UserEntity from(UserInDto userInDto, String userProfile, ModelMapper modelMapper){

        UserEntity userEntity = modelMapper.map(userInDto, UserEntity.class);
        userEntity.setUserRole(UserRole.USER);
        userEntity.setUserProfile(userProfile);
        userEntity.setDeleted(false);
        return userEntity;
    }

    public static UserEntity fromSNS(String userEmail){

        UserEntity userEntity = new UserEntity();
        userEntity.setUserEmail(userEmail);
        userEntity.setUserRole(UserRole.USER);
        //userEntity.setUserProfile(userProfile);
        userEntity.setDeleted(false);
        return userEntity;
    }


}
