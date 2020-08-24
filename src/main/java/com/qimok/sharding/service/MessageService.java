package com.qimok.sharding.service;

import com.qimok.sharding.aop.annotation.MultiDataSourceTransactional;
import com.qimok.sharding.interfaces.MessageDao;
import com.qimok.sharding.interfaces.SessionDao;
import db.tables.pojos.MessagePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * @author qimok
 * @since 2020-08-20
 */
@Service
public class MessageService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SessionDao sessionDao;

    @MultiDataSourceTransactional(transactionManagers = {"noShardingTransactionManager", "shardingTransactionManager"})
    public void sendMessage(MessagePo messagePo) {
        Map<String, MessageDao> messageDaoMap = applicationContext.getBeansOfType(MessageDao.class);
        messageDaoMap.values().forEach(messageDao ->
                messageDao.dynamicInvokeMethod("sendMessage", new Class[]{messagePo.getClass()}, messagePo));
//        int i = 1 / 0;
        sessionDao.insertSessionByShardDsl(messagePo.getSessionId(), (byte)1);
//        int i = 1 / 0;
    }

}
