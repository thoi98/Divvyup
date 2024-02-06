package in.divvyup.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ImmutableMap;
import in.divvyup.persistence.PersistenceManager;
import in.divvyup.persistence.model.UserAuth;

@Repository
public class UserAuthDao {
    @Autowired
    @Qualifier ("mysql")
    private PersistenceManager persistenceManager;

    private static final String QUERY_BY_PROVIDER_ID_AND_STATUS =
            "SELECT * FROM user_auth WHERE provider=:provider AND ip_user_id=:ipUserId AND status=:status";

    public void save(UserAuth userAuth) {
        this.persistenceManager.save(userAuth);
    }

    public void update(UserAuth userAuth) {
        this.persistenceManager.update(userAuth);
    }

    public UserAuth getByProviderIdAndStatus(String provider, String ipUserId, UserAuth.Status status) {
        return persistenceManager.queryOne(QUERY_BY_PROVIDER_ID_AND_STATUS,
                ImmutableMap.of("provider", provider, "ipUserId", ipUserId, "status", status.name()), UserAuth.class);
    }
}
