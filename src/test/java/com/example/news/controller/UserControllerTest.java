//package com.example.news.controller;
//
//import com.example.news.DataCreator;
//import com.example.news.Utils;
//import com.example.news.auth.User;
//import com.example.news.dto.in.NewsInDto;
//import com.example.news.dto.in.UserInDto;
//import com.example.news.mockData.MockUser;
//import com.example.news.service.NewsService;
//import com.example.news.service.UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.io.FileInputStream;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(UserController.class)
//public class UserControllerTest {
//
//    @Autowired
//    MockUser mockUser;
//
//    @Autowired
//    MockMvc mvc;
//
//    @MockBean
//    UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @DisplayName("Create User Controller Test")
//    @Test
//    public void createUser() throws Exception {
//
//        UserInDto userInDto = mockUser.getUserInDto();
//        when(userService.createUser(any())).thenReturn(mockUser.getUserOutDto());
//
//        FileInputStream fis = new FileInputStream("./src/test/resources/test_image.jpg");
//        MockMultipartFile newsProfile = new MockMultipartFile("file", fis);
//
//        //MultiValueMap<String, String> params = Utils.convert(objectMapper, userInDto);
//
//        mvc
//                .perform(multipart("/api/user/create")
//                        //.file("userProfile", newsProfile.getBytes()) //파일 설정
//
//                        .param("aa", "aa")
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .characterEncoding("UTF-8"))
//                .andExpect(status().isCreated())
//                .andDo(print());
//
//
//        //.andDo();
//
//    }
//}
