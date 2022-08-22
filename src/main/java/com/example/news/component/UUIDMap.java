package com.example.news.component;

import com.example.news.auth.User;
import com.example.news.dto.in.UserInDto;
import com.example.news.entity.UserEntity;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class UUIDMap {

    private final ExpiringMap<String, UserInDto> map = ExpiringMap.builder()
            .expiration(20, TimeUnit.MINUTES)   //20분 후 만료
            .maxSize(1000)                              //최대 크기 1000
            .expirationPolicy(ExpirationPolicy.CREATED) //생성 이후 시간 지나면 삭제
            .build();

    public void putObject(String UUID, UserInDto userInDto) {
        map.put(UUID, userInDto);
    }

    public Optional<UserInDto> getObject(String UUID) {
        return Optional.of(map.get(UUID));
    }
}
