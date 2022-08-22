package com.example.news.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NewsMain {

    NORMAL("NORMAL"),
    MAIN("MAIN"),
    MAINSUB("MAINSUB"),
    CATEMAIN("CATEMAIN");

    private String newsType;



}
