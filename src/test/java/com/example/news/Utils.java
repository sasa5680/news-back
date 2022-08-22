package com.example.news;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.config.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static MockMultipartFile getMockMultipartFile(String name) throws IOException {

        ClassPathResource classPathResource = new org.springframework.core.io.ClassPathResource("test_image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(name,
                classPathResource.getFilename(), "image/jpeg", classPathResource.getInputStream().readAllBytes());

        return multipartFile;
    }

    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object obj) {
        MultiValueMap parameters = new LinkedMultiValueMap<String, String>();
        Map<String, String> maps = objectMapper.convertValue(obj, new TypeReference<Map<String, String>>() {});
        parameters.setAll(maps);

        return parameters;
    }

    public static MultiValueMap<String, Object> toMap(Object o) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        Field[] fields = o.getClass().getDeclaredFields();

        for(Field field : fields){
            field.setAccessible(true);
            try {
                map.add(field.getName(), field.get(o));
            } catch (IllegalAccessException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }


        return map;
    }
}
