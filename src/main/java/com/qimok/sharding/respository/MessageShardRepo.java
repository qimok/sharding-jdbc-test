package com.qimok.sharding.respository;

import com.google.common.collect.Lists;
import com.qimok.sharding.interfaces.MessageDao;
import db.tables.MessageTable;
import db.tables.pojos.MessagePo;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import static org.jooq.impl.DSL.max;

/**
 * @author qimok
 * @since 2020-08-20
 */
@Repository
@RequiredArgsConstructor
public class MessageShardRepo implements MessageDao {

    @Resource(name = "shardDsl")
    private final DSLContext shardDsl;

    private final MessageTable table = MessageTable.MESSAGE;

    @Override
    public void sendMessage(MessagePo messagePo) {
        shardDsl.insertInto(table)
                .set(table.ID, 777L)
                .set(table.SESSION_ID, messagePo.getSessionId())
                .set(table.CONTENT, messagePo.getContent())
                .set(table.STATUS, messagePo.getStatus())
                .set(table.CREATED, LocalDateTime.now())
                .set(table.UPDATED, LocalDateTime.now())
                .execute();
    }

    public Boolean insertMessage(Long sessionId, String content) {
        Boolean execute = shardDsl.insertInto(table)
                .set(table.SESSION_ID, sessionId)
                .set(table.CONTENT, content)
                .set(table.STATUS, sessionId.byteValue())
                .set(table.CREATED, LocalDateTime.now())
                .set(table.UPDATED, LocalDateTime.now())
                .execute() == 1;
        return execute;
    }

    public Boolean deleteMessages(Long sessionId) {
        return shardDsl.deleteFrom(table).where(table.SESSION_ID.eq(sessionId)).execute() == 1;
    }

    public Boolean updateMessages(Long sessionId, String content) {
        return shardDsl.update(table).set(table.CONTENT, content).where(table.SESSION_ID.eq(sessionId)).execute() == 1;
    }

    public List<MessagePo> findMessages(Long sessionId) {
        return shardDsl.selectFrom(table).where(table.SESSION_ID.eq(sessionId)).fetchInto(MessagePo.class);
    }

    public List<MessagePo> queryMessages(List<Long> sessionIds, Integer pageSize, Integer pageNumber) {
        List<SortField<?>> sortFields = Lists.newArrayList();
        SortOrder sortOrder = SortOrder.DESC;
        SortField<?> sortField = table.SESSION_ID.sort(sortOrder);
        sortFields.add(sortField);
        return shardDsl.selectFrom(table).where(table.SESSION_ID.in(sessionIds))
                .orderBy(sortFields)
                .limit(Long.parseLong(String.valueOf(pageNumber * pageSize)), pageSize)
                .fetchInto(MessagePo.class);
    }

    public Long findAllCount() {
        return shardDsl.selectCount().from(table).fetchOneInto(Long.class);
    }

    public Long findMaxId() {
        return shardDsl.select(max(table.ID).as("maxId")).from(table).fetchOneInto(Long.class);
    }

    public String findContentBySessionId(Long sessionId) {
        return shardDsl.select(table.CONTENT)
                .from(table)
                .where(table.SESSION_ID.eq(sessionId))
                .limit(1)
                .fetchOneInto(String.class);
    }

}
