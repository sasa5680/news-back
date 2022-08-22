package com.example.news.auth;

import com.example.news.types.UserRole;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {

    UserRole userRole() default UserRole.USER;
}
