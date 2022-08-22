package com.example.news.controller;

import com.example.news.DataCreator;
import com.example.news.dto.in.NewsInDto;
import com.example.news.service.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsController.class)
class NewsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    NewsService newsService;

    @Autowired
    private ObjectMapper objectMapper;

//    @DisplayName("Create News Controller Test")
//    @Test
//    public void createNewsTest() throws Exception {
//
//        NewsInDto newsInDto = DataCreator.getNewsInDto();
//        when(newsService.createNews(newsInDto)).thenReturn(DataCreator.getNewsOutDto());
//
//        FileInputStream fis = new FileInputStream("./src/test/resources/test_image.jpg");
//        MockMultipartFile newsProfile = new MockMultipartFile("file", fis);
//        mvc
//                .perform(multipart("/api/news/create")
//                        .file("newsProfile", newsProfile.getBytes()) //파일 설정
//                        .param("newsTitle", "title")
//                        .param("newsCate", "IT")
//                        .param("newsContent", "<p>content<p>")
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .characterEncoding("UTF-8"))
//                .andExpect(status().isCreated())
//                .andDo(print());
//
//                //.andDo();
//
//    }


}