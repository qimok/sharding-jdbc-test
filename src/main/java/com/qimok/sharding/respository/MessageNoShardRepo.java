package com.qimok.sharding.respository;

import com.qimok.sharding.interfaces.MessageDao;
import db.tables.MessageTable;
import db.tables.pojos.MessagePo;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author qimok
 * @since 2020-08-20
 */
@Repository
@RequiredArgsConstructor
public class MessageNoShardRepo implements MessageDao {

    @Resource(name = "noShardDsl")
    private final DSLContext noShardDsl;

    private final MessageTable table = MessageTable.MESSAGE;

    @Override
    public void sendMessage(MessagePo messagePo) {
        noShardDsl.insertInto(table)
                .set(table.ID, 7777L)
                .set(table.SESSION_ID, messagePo.getSessionId())
                .set(table.CONTENT, messagePo.getContent())
                .set(table.STATUS, messagePo.getStatus())
                .set(table.CREATED, LocalDateTime.now())
                .set(table.UPDATED, LocalDateTime.now())
                .execute();
    }

}
