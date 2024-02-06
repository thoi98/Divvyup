package in.divvyup.controller.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OTPValidateRequest {
    @NotBlank
    private String requestId;

    @NotBlank
    private String otp;
}
