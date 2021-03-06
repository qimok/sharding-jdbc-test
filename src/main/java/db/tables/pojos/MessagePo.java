/*
 * This file is generated by jOOQ.
 */
package db.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MessagePo implements Serializable {

    private static final long serialVersionUID = 1728391904;

    private final Long          id;
    private final Long          sessionId;
    private final String        content;
    private final Byte          status;
    private final LocalDateTime created;
    private final LocalDateTime updated;

    public MessagePo(MessagePo value) {
        this.id = value.id;
        this.sessionId = value.sessionId;
        this.content = value.content;
        this.status = value.status;
        this.created = value.created;
        this.updated = value.updated;
    }

    public MessagePo(
        Long          id,
        Long          sessionId,
        String        content,
        Byte          status,
        LocalDateTime created,
        LocalDateTime updated
    ) {
        this.id = id;
        this.sessionId = sessionId;
        this.content = content;
        this.status = status;
        this.created = created;
        this.updated = updated;
    }

    public Long getId() {
        return this.id;
    }

    public Long getSessionId() {
        return this.sessionId;
    }

    public String getContent() {
        return this.content;
    }

    public Byte getStatus() {
        return this.status;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public LocalDateTime getUpdated() {
        return this.updated;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MessagePo (");

        sb.append(id);
        sb.append(", ").append(sessionId);
        sb.append(", ").append(content);
        sb.append(", ").append(status);
        sb.append(", ").append(created);
        sb.append(", ").append(updated);

        sb.append(")");
        return sb.toString();
    }
}
