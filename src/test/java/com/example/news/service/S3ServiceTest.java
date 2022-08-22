package com.example.news.service;

import com.example.news.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
public class S3ServiceTest {

    @Autowired
    S3Service s3Service;

    @DisplayName("s3 upload and delete test")
    @Test
    public void UploadAndDeleteTest() throws IOException {

        String fileName = "fileName";

        MultipartFile multipartFile = Utils.getMockMultipartFile(fileName);

        String url = s3Service.upload(multipartFile);

        assertNotNull(url);

        s3Service.delete(url);


    }
}
