package com.qimok.sharding.shardingjdbctest;

import com.qimok.sharding.respository.ComplexRepo;
import com.qimok.sharding.respository.MessageShardRepo;
import com.qimok.sharding.respository.SessionRepo;
import db.tables.pojos.MessagePo;
import db.tables.pojos.SessionPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private MessageShardRepo messageShardRepo;

    @Autowired
    private ComplexRepo complexRepo;

    // ----------------测试不分片时的增、删、改、查------------------

    @Test
    @Transactional("noShardingTransactionManager")
    public void testNoShardingInsert() {
        Long id = 1000L;
        List<SessionPo> sessionPos1 = sessionRepo.findSession(id);
        assertEquals(0, sessionPos1.size());

        Byte uuid = id.byteValue();
        assertTrue(sessionRepo.insertSessionByNoShardDsl(id, uuid));

        List<SessionPo> sessionPos2 = sessionRepo.findSession(id);
        assertEquals(1, sessionPos2.size());
        SessionPo sessionPo = sessionPos2.get(0);
        assertEquals(id.byteValue(), sessionPo.getId().byteValue());
    }

    @Test
    public void testNoShardingFind() {
        Long id = 1L;
        List<SessionPo> sessionPos = sessionRepo.findSession(id);

        assertEquals(1, sessionPos.size());
        SessionPo sessionPo = sessionPos.get(0);
        assertEquals(id.byteValue(), sessionPo.getId().byteValue());
    }

    @Test
    @Transactional("noShardingTransactionManager")
    public void testNoShardingUpdate() {
        Long id = 2L;
        List<SessionPo> sessionPos1 = sessionRepo.findSession(id);
        assertEquals(1, sessionPos1.size());
        SessionPo sessionPo1 = sessionPos1.get(0);
        assertEquals((byte)0, sessionPo1.getUuid());

        Byte newUuid = (byte)999;
        assertTrue(sessionRepo.updateSession(id, newUuid));

        List<SessionPo> sessionPos2 = sessionRepo.findSession(id);
        assertEquals(1, sessionPos2.size());
        SessionPo sessionPo2 = sessionPos2.get(0);
        assertEquals(newUuid, sessionPo2.getUuid());
    }

    @Test
    @Transactional("noShardingTransactionManager")
    public void testNoShardingDelete() {
        Long id = 100L;
        assertEquals(1, sessionRepo.findSession(id).size());

        assertTrue(sessionRepo.deleteSession(id));

        assertEquals(0, sessionRepo.findSession(id).size());
    }

    // ----------------测试分片时的增、删、改、查------------------

    @Test
    @Transactional("shardingTransactionManager")
    public void testShardingInsert() {
        Long sessionId = 1000L;
        assertEquals(0, messageShardRepo.findMessages(sessionId).size());

        String content = "test test";
        assertTrue(messageShardRepo.insertMessage(sessionId, content));

        List<MessagePo> messagePos = messageShardRepo.findMessages(sessionId);
        assertEquals(1, messagePos.size());
        MessagePo messagePo = messagePos.get(0);
        assertEquals(content, messagePo.getContent());
        assertEquals(sessionId.byteValue(), messagePo.getStatus());
    }

    @Test
    public void testShardingFind() {
        Long sessionId = 1L;
        List<MessagePo> messagePos = messageShardRepo.findMessages(sessionId);

        assertEquals(1, messagePos.size());
        assertEquals("test1", messagePos.get(0).getContent());
        assertEquals((byte)0, messagePos.get(0).getStatus());
    }

    @Test
    @Transactional("shardingTransactionManager")
    public void testShardingUpdate() {
        Long sessionId = 2L;
        List<MessagePo> messagePos1 = messageShardRepo.findMessages(sessionId);
        assertEquals(1, messagePos1.size());
        MessagePo messagePo1 = messagePos1.get(0);
        assertEquals("test2", messagePo1.getContent());

        String newContent = "test test";
        assertTrue(messageShardRepo.updateMessages(sessionId, newContent));

        List<MessagePo> messagePos2 = messageShardRepo.findMessages(sessionId);
        assertEquals(1, messagePos2.size());
        MessagePo messagePo2 = messagePos2.get(0);
        assertEquals(newContent, messagePo2.getContent());
    }

    @Test
    @Transactional("shardingTransactionManager")
    public void testShardingDelete() {
        Long sessionId = 100L;
        List<MessagePo> messagePos1 = messageShardRepo.findMessages(sessionId);
        assertEquals(1, messagePos1.size());

        assertTrue(messageShardRepo.deleteMessages(sessionId));

        List<MessagePo> messagePos2 = messageShardRepo.findMessages(sessionId);
        assertEquals(0, messagePos2.size());
    }

    // ----------------测试分片表与不分片表的连表查询------------------

    @Test
    public void testShardAndNoShardJoinFind() {
        List<MessagePo> messagePos = complexRepo.findMessagesBySessionIds(Arrays.asList(1L, 2L));

        assertEquals(2, messagePos.size());
        for (int i = 0; i < messagePos.size(); i++) {
            assertEquals(1L, messagePos.get(i).getId());
            if (messagePos.get(i).getSessionId().equals(1L)) {
                assertEquals("test1", messagePos.get(i).getContent());
            }
            if (messagePos.get(i).getSessionId().equals(2L)) {
                assertEquals("test2", messagePos.get(i).getContent());
            }
        }
    }

    // ----------------测试分片表的分页查询------------------

    @Test
    public void testShardingPagingQuery() {
        Integer pageNumber = 0;

        List<MessagePo> messagePos = messageShardRepo.queryMessages(Arrays.asList(1L, 2L), 10, pageNumber);
        // 这里只验证分页时的 content
        assertEquals(2, messagePos.size());
        assertEquals(1L, messagePos.get(0).getId());
        assertEquals(2L, messagePos.get(0).getSessionId());
        assertEquals("test2", messagePos.get(0).getContent());
        assertEquals(1L, messagePos.get(1).getId());
        assertEquals(1L, messagePos.get(1).getSessionId());
        assertEquals("test1", messagePos.get(1).getContent());
    }

    // ----------------测试单路由单个字段的查询------------------
    @Test
    public void testFullRoutingSingleField() {
        String content = messageShardRepo.findContentBySessionId(100L);
        assertEquals("test100", content);
    }

    // ----------------测试全路由 count 查询------------------
    @Test
    public void testFullRoutingCount() {
        Long count = messageShardRepo.findAllCount();
        assertEquals(3L, count);
    }

    // ----------------测试全路由 max 查询------------------

    /**
     * 需要给求最大值的字段设置别名
     */
    @Test
    public void testFullRoutingMax() {
        Long maxId = messageShardRepo.findMaxId();
        assertEquals(2L, maxId);
    }

    // ----------------测试同时包含对分片、不分片表操作的事务------------------

    /**
     * 同一事务内，如果即包含对分片表的操作，又包含对不分片表的操作，如何保证事务？
     * <p>
     *     1、统一使用分片的事务管理器【@Transactional("shardingTransactionManager")】
     *              和 分片的 DSL【@Resource(name = "shardDsl") private final DSLContext shardDsl;】
     *     2、可能出现的问题：
     *          当使用分片的数据源（DSL）操作的时候，不分片的表操作有可能出现 SQL 不兼容的问题，
     *          此时，可以通过 SQL 改写完成，如果无法改写，或者业务也无法变更，可以考虑通过【AOP + 两阶段提交】保证事务
     */

    /**
     * 统一使用
     * <p>
     *     1、分片的事务管理器【@Transactional("shardingTransactionManager")】
     *     2、分片的 DSL【@Resource(name = "shardDsl") private final DSLContext shardDsl;】
     */
    @Test
    public void testTransactionalByShardingTM() {
        Long id = 99999L;
        assertSessionAndMessageByTM(id);
        try {
            complexRepo.complexInsertSessionAndMessageByShardingTM(id);
        } catch (Exception e) {
        } finally {
            // 验证数据是否正常回滚
            assertSessionAndMessageByTM(id);
        }
    }

    /**
     * AOP + 两阶段提交保证事务
     */
    @Test
    public void testTransactionalByMultiTM() {
        Long id = 999L;
        assertSessionAndMessageByTM(id);
        try {
            complexRepo.complexInsertSessionAndMessageByMultiTM(999L);
        } catch (Exception e) {
        } finally {
            // 验证数据是否正常回滚
            assertSessionAndMessageByTM(id);
        }
    }

    private void assertSessionAndMessageByTM(Long id) {
        List<SessionPo> sessionPos1 = sessionRepo.findSession(id);
        assertEquals(0, sessionPos1.size());
        List<MessagePo> messagePos1 = messageShardRepo.findMessages(id);
        assertEquals(0, messagePos1.size());
    }

}
