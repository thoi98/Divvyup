package in.divvyup.persistence.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import org.springframework.jdbc.core.RowMapper;
import in.divvyup.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserGroup extends BaseModel {
    private final String userId;

    private final String groupId;

    @Builder
    public UserGroup(String id, String userId, String groupId, ZonedDateTime created, ZonedDateTime updated) {
        super(id, created, updated);
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO user_groups(id, user_id, group_id, created) VALUES(:id, :userId, :groupId, :created)";
    }

    @Override
    public String getUpdateQuery() {
        return null;
    }

    public static final class UserGroupRowMapper implements RowMapper<UserGroup> {
        @Override
        public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            String userId = rs.getString("user_id");
            String groupId = rs.getString("group_id");
            ZonedDateTime created = DateTimeUtil.getTimeFromResultSet(rs, "created");
            ZonedDateTime updated = DateTimeUtil.getTimeFromResultSet(rs, "updated");
            return UserGroup.builder().id(id).userId(userId).groupId(groupId).created(created).updated(updated).build();
        }
    }
}
