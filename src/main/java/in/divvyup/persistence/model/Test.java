package in.divvyup.persistence.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import org.springframework.jdbc.core.RowMapper;
import in.divvyup.util.DateTimeUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings ({ "unused" })
public class Test extends BaseModel {
    private String details;

    @Builder
    public Test(String id, String details, ZonedDateTime created, ZonedDateTime updated) {
        super(id, created, updated);
        this.details = details;
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO test(id, details, created) VALUES(:id, :details, :created)";
    }

    @Override
    public String getUpdateQuery() {
        return null;
    }

    public static final class TestRowMapper implements RowMapper<Test> {
        @Override
        public Test mapRow(ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            String details = rs.getString("details");
            ZonedDateTime created = DateTimeUtil.getTimeFromResultSet(rs, "created");
            ZonedDateTime updated = DateTimeUtil.getTimeFromResultSet(rs, "updated");
            return Test.builder().id(id).details(details).created(created).updated(updated).build();
        }
    }
}
