package com.qimok.sharding.interfaces;

import db.tables.pojos.SessionPo;
import java.util.List;

/**
 * @author qimok
 * @since 2020-08-20
 */
public interface SessionDao {

    Boolean insertSessionByNoShardDsl(Long id, Byte uuid);

    Boolean insertSessionByShardDsl(Long id, Byte uuid);

    Boolean deleteSession(Long id);

    Boolean updateSession(Long id, Byte newUuid);

    List<SessionPo> findSession(Long id);

}
