package com.example.news.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    USER("USER"),
    WRITER("WRITER"),
    ADMIN("ADMIN");

    private String userRoleType;
}
