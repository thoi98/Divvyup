package in.divvyup.persistence.model;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class BaseModel {
    private String id;

    private ZonedDateTime created;

    private ZonedDateTime updated;

    public abstract String getInsertQuery();

    public abstract String getUpdateQuery();

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
