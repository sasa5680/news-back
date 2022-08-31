package com.example.news.dto.req;

import lombok.*;

//kakao api 유저정보 요청시 데이터 받아오는 dto
@Data
public class KakaoUserDto {

    //고유 아이디
    private String id;

    private Properties properties;

    private Kakao_account kakao_account;

    @Data
    public class Properties {
        String nickname;
    }

    @Data
    public class Kakao_account {
        boolean hasEmail;
        String email;
    }

}
