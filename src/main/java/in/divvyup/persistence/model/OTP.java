package in.divvyup.persistence.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import in.divvyup.util.DateTimeUtil;
import in.divvyup.util.JSONUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@SuppressWarnings ({ "unused", "unchecked" })
public class OTP extends BaseModel {
    private final Reason reason;

    private final OTPMode mode;

    private final String identifier;

    private final String otp;

    private final OTPProvider notificationProvider;

    private final String notificationId;

    @Setter
    private OTPStatus status;

    private final ZonedDateTime expiresAt;

    @Getter (AccessLevel.NONE)
    private final String metadataString;

    private final Map<String, Object> metadata;

    @Builder
    private OTP(String id, ZonedDateTime created, ZonedDateTime updated, Reason reason, OTPMode mode, String identifier, String otp,
            OTPProvider notificationProvider, String notificationId, OTPStatus status, ZonedDateTime expiresAt, Map<String, Object> metadata) {
        super(id, created, updated);
        this.reason = reason;
        this.mode = mode;
        this.identifier = identifier;
        this.otp = otp;
        this.status = status;
        this.expiresAt = expiresAt;
        this.notificationProvider = notificationProvider;
        this.notificationId = notificationId;
        this.metadataString = Optional.ofNullable(metadata).map(JSONUtil::toJson).orElse("");
        this.metadata = metadata;
    }

    @Override
    public String getInsertQuery() {
        return "INSERT INTO otp (id, reason, mode, identifier, otp, notification_provider, notification_id, metadata, status, expires_at, created) "
                + "VALUES (:id, :reason, :mode, :identifier, :otp, :notificationProvider, :notificationId, :metadataString, :status, " +
                ":expiresAt, :created)";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE otp SET status=:status, updated=:updated WHERE id=:id";
    }

    public String getRequestId() {
        return (String) this.metadata.getOrDefault("previous_request_id", "");
    }

    public boolean isActive() {
        return OTPStatus.UNVERIFIED.equals(this.status);
    }

    public User getUser() {
        return User.builder().mobile(this.identifier).build();
    }

    public String getIdentityProvider() {
        return this.mode == OTP.OTPMode.EMAIL ? "email" : "mobile";
    }

    public static class OTPRowMapper implements RowMapper<OTP> {
        @Override
        public OTP mapRow(ResultSet rs, int i) throws SQLException {
            String id = rs.getString("id");
            Reason reason = Reason.valueOf(rs.getString("reason"));
            OTPMode mode = OTPMode.valueOf(rs.getString("mode"));
            String identifier = rs.getString("identifier");
            String otp = rs.getString("otp");
            OTPProvider notificationProvider = OTPProvider.valueOf(rs.getString("notification_provider"));
            String notificationId = rs.getString("notification_id");
            OTPStatus status = OTPStatus.valueOf(rs.getString("status"));
            ZonedDateTime expiresAt = DateTimeUtil.getTimeFromResultSet(rs, "expires_at");
            String metadataString = rs.getString("metadata");
            Map<String, Object> metadata = new HashMap<>();
            if (StringUtils.isNotBlank(metadataString)) {
                metadata = JSONUtil.fromJson(metadataString, Map.class);
            }
            ZonedDateTime created = DateTimeUtil.getTimeFromResultSet(rs, "created");
            ZonedDateTime updated = DateTimeUtil.getTimeFromResultSet(rs, "updated");
            return OTP.builder().id(id).id(id).reason(reason).mode(mode).identifier(identifier).otp(otp).notificationProvider(notificationProvider)
                      .notificationId(notificationId).status(status).expiresAt(expiresAt).metadata(metadata).created(created).updated(updated)
                      .build();
        }
    }

    public enum Reason {
        LOGIN
    }

    public enum OTPMode {
        MOBILE,
        EMAIL
    }

    public enum OTPStatus {
        UNVERIFIED,
        VERIFIED,
        EXPIRED,
        INVALIDATED
    }

    public enum OTPProvider {
        AWS_SNS,
        AWS_SES
    }
}

