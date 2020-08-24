package com.qimok.sharding.respository;

import com.qimok.sharding.aop.annotation.MultiDataSourceTransactional;
import db.tables.MessageTable;
import db.tables.SessionTable;
import db.tables.pojos.MessagePo;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author qimok
 * @since 2020-08-20
 */
@Repository
@RequiredArgsConstructor
public class ComplexRepo {

    @Resource(name = "shardDsl")
    private final DSLContext shardDsl;

    private final SessionTable table1 = SessionTable.SESSION;
    private final MessageTable table2 = MessageTable.MESSAGE;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private MessageShardRepo messageRepo;

    @Transactional("shardingTransactionManager")
    public void complexInsertSessionAndMessageByShardingTM(Long id) {
        sessionRepo.insertSessionByShardDsl(id, id.byteValue());
        // int i = 1 / 0;
        messageRepo.insertMessage(id, id.toString());
        int i = 1 / 0;
    }

    @MultiDataSourceTransactional(transactionManagers = {"noShardingTransactionManager", "shardingTransactionManager"})
    public void complexInsertSessionAndMessageByMultiTM(Long id) {
        sessionRepo.insertSessionByNoShardDsl(id, id.byteValue());
        // int i = 1 / 0;
        messageRepo.insertMessage(id, id.toString());
        int i = 1 / 0;
    }

    public List<MessagePo> findMessagesBySessionIds(List<Long> sessionIds) {
        return shardDsl.select(table2.fields()).from(table2).innerJoin(table1)
                .on(table1.ID.eq(table2.SESSION_ID))
                .where(table2.SESSION_ID.in(sessionIds))
                .fetchInto(MessagePo.class);
    }
}
