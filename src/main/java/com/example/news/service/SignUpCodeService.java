package com.example.news.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SignUpCodeService {

    //20분의 유효시간
    private int validTime = 1200;

    @Autowired
    RedisTemplate<String, String> stringObjectRedisTemplate;

    @Transactional
    public String generateCode(String email) {

        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        String code = String.format("%06d", number);

        final ValueOperations<String, String> stringValueOperations = stringObjectRedisTemplate.opsForValue();
        stringValueOperations.set(email, code, validTime, TimeUnit.SECONDS);

        //signUpCodeRepository.save(signUpCodeEntity);

        return code;
    }

    public boolean isValidCode(String email, String code) {

        final ValueOperations<String, String> stringValueOperations = stringObjectRedisTemplate.opsForValue();
        String ccc = stringValueOperations.get(email);

        if(ccc == null) return false;

        if(ccc.equals(code)) return true;

        return false;
    }
}
