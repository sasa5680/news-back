package com.example.news.service;

import com.example.news.dto.out.NewNewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocketService {

    @Autowired
    private SimpMessagingTemplate webSocket;

    public void sendMessage(NewNewsDto newNewsDto) {

        webSocket.convertAndSend("/topic/new", newNewsDto);
    }
}
