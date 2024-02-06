package in.divvyup.pojo;

import java.util.Optional;
import in.divvyup.persistence.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestExecutionContext {
    private User user;

    private String sessionId;

    public String getUserId() {
        return Optional.ofNullable(this.user).map(User::getId).orElse(null);
    }
}
