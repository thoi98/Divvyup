package in.divvyup.controller;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import in.divvyup.annotation.NoLogin;
import in.divvyup.controller.filter.RequestProcessingFilter;
import in.divvyup.controller.request.OTPLoginRequest;
import in.divvyup.controller.request.OTPValidateRequest;
import in.divvyup.controller.view.InitiateLoginView;
import in.divvyup.controller.view.UserIdView;
import in.divvyup.exception.InvalidRequestException;
import in.divvyup.persistence.model.OTP;
import in.divvyup.persistence.model.User;
import in.divvyup.persistence.model.UserSession;
import in.divvyup.pojo.RequestExecutionContext;
import in.divvyup.service.AuthService;
import in.divvyup.service.UserService;
import in.divvyup.util.DateTimeUtil;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping ("v1/auth")
//@CrossOrigin (allowCredentials = "true")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping ("/mlogin")
    @NoLogin
    public InitiateLoginView mobileLogin(@RequestBody @Valid OTPLoginRequest loginRequest) {
        String phone = loginRequest.getPhone();
        Map<String, Object> metadata = new HashMap<>();
        if (!phone.contains("-")) {
            throw new InvalidRequestException("Invalid phone number");
        }
        if (StringUtils.isNotBlank(loginRequest.getPreviousRequestId())) {
            metadata.getOrDefault("previous_request_id", loginRequest.getPreviousRequestId());
            this.authService.invalidateOTP(loginRequest.getPreviousRequestId());
        }
        // To add: Check for phone number validity
        if (false) {
            throw new InvalidRequestException("Invalid phone number");
        }
        OTP otp = this.authService.sendLoginOTP(phone, metadata);
        long expiresAt = otp.getExpiresAt().toEpochSecond();
        long currentTime = DateTimeUtil.currentTime().toEpochSecond();
        return InitiateLoginView.builder().requestId(otp.getId()).expiresIn(expiresAt - currentTime).build();
    }

    @PostMapping ("/otp/validate")
    @NoLogin
    public UserIdView validateOTP(HttpServletResponse response, @RequestBody @Valid OTPValidateRequest validateRequest) {
        RequestExecutionContext executionContext = RequestProcessingFilter.REQUEST_EXECUTION_CONTEXT.get();
        OTP otp = authService.validateOTP(validateRequest.getOtp(), validateRequest.getRequestId());
        UserSession userSession = createUserSession(response, otp.getUser(), otp.getIdentifier(), otp.getIdentityProvider());
        return UserIdView.builder().userId(userSession.getUserId()).build();
    }

    private UserSession createUserSession(HttpServletResponse response, User user, String identityProviderUserId, String identityProvider) {
        UserSession userSession = authService.createUserSession(user, identityProviderUserId, identityProvider);
        authService.addAuthCookies(response, userSession);
        return userSession;
    }
}
