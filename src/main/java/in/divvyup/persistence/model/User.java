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
public class User extends BaseModel {
    private String firstName;

    private String lastName;

    private String mobile;

    private String email;

    private Status status;

    private boolean newUser;

    @Builder
    public User(String id, String firstName, String lastName, String mobile, String email, Status status, ZonedDateTime created,
            ZonedDateTime updated) {
        super(id, created, updated);
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.status = status;
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO users(id, first_name, last_name, mobile, email, status, created) VALUES(:id, :firstName, :lastName, :mobile, :email, " + ":status, :created)";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE users SET first_name=:firstName, last_name=:lastName, updated=NOW() WHERE id=:id";
    }

    public static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            String id = resultSet.getString("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String mobile = resultSet.getString("mobile");
            String email = resultSet.getString("email");
            Status status = Status.valueOf(resultSet.getString("status"));
            ZonedDateTime created = DateTimeUtil.getTimeFromResultSet(resultSet, "created");
            ZonedDateTime updated = DateTimeUtil.getTimeFromResultSet(resultSet, "updated");
            return User.builder().id(id).firstName(firstName).lastName(lastName).mobile(mobile).email(email).created(created).updated(updated)
                       .status(status).build();
        }
    }
}
