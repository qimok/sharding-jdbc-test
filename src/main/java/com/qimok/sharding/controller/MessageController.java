package com.qimok.sharding.controller;

import com.qimok.sharding.service.MessageService;
import db.tables.pojos.MessagePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

/**
 * @author qimok
 * @since 2020-08-20
 */
@RestController
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping(value = "/api/messages/send")
    public String sendMessage() {
        MessagePo messagePo = new MessagePo(0L, 200L, "测试", (byte)1,
                LocalDateTime.now(), LocalDateTime.now());
        messageService.sendMessage(messagePo);
        return "消息发送成功...";
    }

}
