package in.divvyup.service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.divvyup.config.ApplicationConfig;
import in.divvyup.config.AuthConfig;
import in.divvyup.config.ENV;
import in.divvyup.exception.InvalidRequestException;
import in.divvyup.persistence.dao.OTPDao;
import in.divvyup.persistence.dao.UserAuthDao;
import in.divvyup.persistence.dao.UserSessionDao;
import in.divvyup.persistence.model.OTP;
import in.divvyup.persistence.model.User;
import in.divvyup.persistence.model.UserAuth;
import in.divvyup.persistence.model.UserSession;
import in.divvyup.util.CookieUtil;
import in.divvyup.util.DateTimeUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
    public static final String COOKIE_PATH = "/";

    @Autowired
    private OTPDao otpDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserSessionDao userSessionDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationConfig applicationConfig;

    private final boolean useSecureCookie;

    private final String cookieDomain;

    private final String sessionCookieName;

    @Autowired
    public AuthService(AuthConfig authConfig) {
        this.cookieDomain = authConfig.getCookieDomain();
        this.sessionCookieName = authConfig.getCookieName();
        this.useSecureCookie = authConfig.getUseSecureCookie();
    }

    public void invalidateOTP(String requestId) {
        OTP persistedOTP = this.otpDao.getById(requestId);
        if (Objects.isNull(persistedOTP)) {
            throw new InvalidRequestException("Invalid request for OTP verification");
        }
        persistedOTP.setStatus(OTP.OTPStatus.INVALIDATED);
        this.otpDao.update(persistedOTP);
    }

    public OTP sendLoginOTP(String phone, Map<String, Object> metadata) {
        String storedOTP = applicationConfig.getApplicationEnvironment().equals(ENV.DEV) ? "000000" : RandomStringUtils.randomNumeric(6);
        //TODO: Use service to trigger an OTP
        String standardPhoneNumber = MobileUtil.getStandardPhoneNumber(phone);
        OTP otp = OTP.builder().reason(OTP.Reason.LOGIN).mode(OTP.OTPMode.MOBILE).identifier(standardPhoneNumber)
                     .notificationProvider(OTP.OTPProvider.AWS_SNS).notificationId("").otp(storedOTP).expiresAt(DateTimeUtil.withMinutesAdjusted(5))
                     .metadata(metadata).status(OTP.OTPStatus.UNVERIFIED).build();
        this.otpDao.save(otp);
        return otp;
    }

    public OTP validateOTP(String storedOTP, String otpRequestId) {
        OTP persistedOTP = this.otpDao.getById(otpRequestId);
        if (Objects.isNull(persistedOTP)) {
            throw new InvalidRequestException("Invalid request id");
        }
//        if (DateTimeUtil.currentTime().isAfter(persistedOTP.getExpiresAt())) {
//            throw new InvalidRequestException("Request has expired");
//        }
        if (!persistedOTP.getOtp().equals(storedOTP)) {
            throw new InvalidRequestException("Invalid OTP");
        }
        if (!persistedOTP.isActive()) {
            throw new InvalidRequestException("Invalid OTP");
        }
        persistedOTP.setStatus(OTP.OTPStatus.VERIFIED);
        persistedOTP.setUpdated(DateTimeUtil.currentTime());
        this.otpDao.update(persistedOTP);
        return persistedOTP;
    }

    public UserSession createUserSession(User user, String identityProviderUserId, String identityProvider) {
        UserAuth userAuth = userAuthDao.getByProviderIdAndStatus(identityProvider, identityProviderUserId, UserAuth.Status.ACTIVE);
        String userId;
        if (Objects.isNull(userAuth)) {
            if (Objects.isNull(user.getFirstName())) {
                user.setFirstName(RandomStringUtils.randomAlphanumeric(10));
            }
            userService.createUser(user, identityProvider, identityProviderUserId);
            userId = user.getId();
        } else {
            userId = userAuth.getUserId();
        }
        UserSession userSession = UserSession.builder().id(UUID.randomUUID().toString()).sessionId(UUID.randomUUID().toString()).userId(userId)
                                             .activeUntil(DateTimeUtil.withDaysAdjusted(7)).created(DateTimeUtil.currentTime()).build();
        userSessionDao.save(userSession);
        return userSession;
    }

    public void addAuthCookies(HttpServletResponse response, UserSession userSession) {
        ZonedDateTime cookieActiveUntil = userSession.getActiveUntil();
        //  session id cookie
        Cookie sessionIdCookie = CookieUtil.createCookie(
                CookieUtil.CookieConfig.builder().name(this.sessionCookieName).value(Base64.encodeBase64String(userSession.getSessionId().getBytes()))
                                       .secure(useSecureCookie).path(COOKIE_PATH).domain(this.cookieDomain).expiresAt(cookieActiveUntil).build());
        response.addCookie(sessionIdCookie);
    }
}

