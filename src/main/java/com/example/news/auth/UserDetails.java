package com.example.news.auth;

import com.example.news.entity.UserEntity;
import com.example.news.types.UserRole;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDetails {

    private int userId;
    private UserRole userRole;

    public static UserDetails from(UserEntity userEntity){

        UserDetails userDetails = new UserDetails();

        userDetails.setUserId(userEntity.getUserId());
        userDetails.setUserRole(userEntity.getUserRole());

        return userDetails;
    }
}
