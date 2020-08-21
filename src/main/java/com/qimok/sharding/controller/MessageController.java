package com.qimok.sharding.controller;

import com.qimok.sharding.respository.MessageRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

/**
 * @author qimok
 * @since 2020-08-20
 */
@RestController
@Slf4j
public class MessageController {

    @Autowired
    private MessageRepo messageRepository;

    @PostMapping(value = "/api/messages/send")
    public String send() {
        Long sessionId = new Random().nextLong();
        messageRepository.insertMessage(sessionId, "test");
        return "消息发送成功...";
    }

}
