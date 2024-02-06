package in.divvyup.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ImmutableMap;
import in.divvyup.persistence.PersistenceManager;
import in.divvyup.persistence.model.UserSession;

@Repository
public class UserSessionDao {
    @Autowired
    @Qualifier ("mysql")
    private PersistenceManager persistenceManager;

    private static final String QUERY_BY_SESSION_ID = "SELECT * FROM user_session WHERE session_id=:sessionId AND active_until>NOW()";

    public void save(UserSession userSession) {
        this.persistenceManager.save(userSession);
    }

    public UserSession getBySessionId(String sessionId) {
        return this.persistenceManager.queryOne(QUERY_BY_SESSION_ID, ImmutableMap.of("sessionId", sessionId), UserSession.class);
    }
}
