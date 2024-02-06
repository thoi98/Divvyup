package in.divvyup.persistence.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import org.springframework.jdbc.core.RowMapper;
import in.divvyup.pojo.Metadata;
import in.divvyup.util.DateTimeUtil;
import in.divvyup.util.MySQLUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Group extends BaseModel {
    private String name;

    private Status status;

    private Metadata metadata;

    @Builder
    public Group(String id, String name, Status status, String metadata, ZonedDateTime created, ZonedDateTime updated) {
        super(id, created, updated);
        this.name = name;
        this.status = status;
        this.metadata = Metadata.builder().data(metadata).build();
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO `groups`(id, name, status, metadata, created) VALUES(:id, :name, :status, :metadata, :created)";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE `groups` SET name=:name, metadata=:metadata, updated=:updated WHERE id=:id";
    }

    public static final class GroupRowMapper implements RowMapper<Group> {
        @Override
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            String name = rs.getString("name");
            Status status = Status.valueOf(rs.getString("status"));
            String metadata = MySQLUtil.getBlobAsString(rs, "metadata");
            ZonedDateTime created = DateTimeUtil.getTimeFromResultSet(rs, "created");
            ZonedDateTime updated = DateTimeUtil.getTimeFromResultSet(rs, "updated");
            return Group.builder().id(id).name(name).status(status).metadata(metadata).created(created).updated(updated).build();
        }
    }
}
