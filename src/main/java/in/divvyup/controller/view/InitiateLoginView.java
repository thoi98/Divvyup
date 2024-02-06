package in.divvyup.controller.view;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InitiateLoginView {
    private String requestId;

    private long expiresIn;
}
