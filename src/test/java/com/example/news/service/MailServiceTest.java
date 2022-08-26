package com.example.news.service;

import com.example.news.NewsApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;

import java.util.concurrent.Executor;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest
public class MailServiceTest {

    @Autowired
    MailService mailService;

    @Test
    public void sendMailConfirmMail() throws MessagingException, InterruptedException {
        mailService.sendConfirmEmail( "123456", "sasa5680@naver.com", "sasa5680");

        //스프링 컨텍스트가 모두 종료되기 때문에 sleep을 걸어준다.
        Thread.sleep(15000);

    }
}
