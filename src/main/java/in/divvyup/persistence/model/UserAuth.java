package in.divvyup.persistence.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import org.springframework.jdbc.core.RowMapper;
import in.divvyup.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserAuth extends BaseModel {
    private final String provider;

    private final String ipUserId;

    private final String userId;

    private final String creatorUserId;

    private final Status status;

    @Builder
    private UserAuth(String id, ZonedDateTime created, ZonedDateTime updated, String provider, String ipUserId, String userId, String creatorUserId,
            Status status) {
        super(id, created, updated);
        this.ipUserId = ipUserId;
        this.userId = userId;
        this.creatorUserId = creatorUserId;
        this.provider = provider;
        this.status = status;
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO user_auth (id, ip_user_id, user_id, creator_user_id, provider, status, created) VALUES(:id, :ipUserId, :userId, " +
                ":creatorUserId, :provider, :status, :created)";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE user_auth SET user_id=:userId, updated=:updated WHERE id=:id";
    }

    public static class UserAuthRowMapper implements RowMapper<UserAuth> {
        @Override
        public UserAuth mapRow(ResultSet rs, int i) throws SQLException {
            String id = rs.getString("id");
            String provider = rs.getString("provider");
            String ipUserId = rs.getString("ip_user_id");
            String userId = rs.getString("user_id");
            String creatorUserId = rs.getString("creator_user_id");
            Status status = Status.valueOf(rs.getString("status"));
            ZonedDateTime created = DateTimeUtil.getTimeFromResultSet(rs, "created");
            ZonedDateTime updated = DateTimeUtil.getTimeFromResultSet(rs, "updated");
            return UserAuth.builder().id(id).provider(provider).ipUserId(ipUserId).userId(userId).creatorUserId(creatorUserId).status(status)
                           .created(created).updated(updated).build();
        }
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        PENDING_VERIFICATION
    }
}
