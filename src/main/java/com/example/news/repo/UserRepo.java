package com.example.news.repo;

import com.example.news.auth.User;
import com.example.news.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUserId(int id);

    UserEntity findByUserEmail(String userEmail);

    UserEntity findByUserName(String userName);

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserName(String userName);

    boolean existsByUserEmailAndUserPw(String userEmail, String userPw);
}
