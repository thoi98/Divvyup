package in.divvyup.persistence.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import org.springframework.jdbc.core.RowMapper;
import in.divvyup.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSession extends BaseModel {
    private final String userId;

    private final String sessionId;

    private final ZonedDateTime activeUntil;

    @Builder
    private UserSession(String id, ZonedDateTime created, ZonedDateTime updated, String userId, String sessionId, ZonedDateTime activeUntil) {
        super(id, created, updated);
        this.userId = userId;
        this.sessionId = sessionId;
        this.activeUntil = activeUntil;
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO user_session (id, user_id, session_id, active_until, created) VALUES(:id, :userId, :sessionId, :activeUntil, :created)";
    }

    @Override
    public String getUpdateQuery() {
        return null;
    }

    public static class UserSessionRowMapper implements RowMapper<UserSession> {
        @Override
        public UserSession mapRow(ResultSet rs, int i) throws SQLException {
            String id = rs.getString("id");
            String userId = rs.getString("user_id");
            String sessionId = rs.getString("session_id");
            ZonedDateTime activeUntil = DateTimeUtil.getTimeFromResultSet(rs, "active_until");
            ZonedDateTime created = DateTimeUtil.getTimeFromResultSet(rs, "created");
            ZonedDateTime updated = DateTimeUtil.getTimeFromResultSet(rs, "updated");
            return UserSession.builder().id(id).userId(userId).sessionId(sessionId).activeUntil(activeUntil).created(created).updated(updated)
                              .build();
        }
    }
}
