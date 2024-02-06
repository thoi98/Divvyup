package in.divvyup.controller.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OTPLoginRequest {
    @NotBlank
    private String phone;

    private String previousRequestId;
}
