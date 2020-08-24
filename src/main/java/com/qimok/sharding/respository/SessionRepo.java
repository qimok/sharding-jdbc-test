package com.qimok.sharding.respository;

import com.qimok.sharding.interfaces.SessionDao;
import db.tables.SessionTable;
import db.tables.pojos.SessionPo;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author qimok
 * @since 2020-08-20
 */
@Repository
@RequiredArgsConstructor
public class SessionRepo implements SessionDao {

    @Resource(name = "noShardDsl")
    private final DSLContext noShardDsl;

    @Resource(name = "shardDsl")
    private final DSLContext shardDsl;

    private final SessionTable table = SessionTable.SESSION;

    @Override
    public Boolean insertSessionByNoShardDsl(Long id, Byte uuid) {
        return noShardDsl.insertInto(table).set(table.ID, id).set(table.UUID, uuid).execute() == 1;
    }

    @Override
    public Boolean insertSessionByShardDsl(Long id, Byte uuid) {
        return shardDsl.insertInto(table).set(table.ID, id).set(table.UUID, uuid).execute() == 1;
    }

    @Override
    public Boolean deleteSession(Long id) {
        return noShardDsl.deleteFrom(table).where(table.ID.eq(id)).execute() == 1;
    }

    @Override
    public Boolean updateSession(Long id, Byte newUuid) {
        return noShardDsl.update(table).set(table.UUID, newUuid).where(table.ID.eq(id)).execute() == 1;
    }

    @Override
    public List<SessionPo> findSession(Long id) {
        return noShardDsl.selectFrom(table).where(table.ID.eq(id)).fetchInto(SessionPo.class);
    }

}
