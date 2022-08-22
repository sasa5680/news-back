package com.example.news;

import com.example.news.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class MockNewsApplicationTests {

    @Autowired
    UserRepo userRepo;

    @Test
    void contextLoads() throws IOException {

    }

}
