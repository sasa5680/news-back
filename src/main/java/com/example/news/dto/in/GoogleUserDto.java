package com.example.news.dto.in;

import lombok.*;


//구글 api 유저정보 요청시 데이터 받아오는 dto
@Data
public class GoogleUserDto {

    String email;
    String name;
}
