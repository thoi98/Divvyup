package in.divvyup.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ImmutableMap;
import in.divvyup.persistence.PersistenceManager;
import in.divvyup.persistence.model.User;

@Repository
public class UserDao {
    @Autowired
    @Qualifier ("mysql")
    PersistenceManager persistenceManager;

    private static final String QUERY_BY_ID = "SELECT * FROM users WHERE id=:id";

    public void save(User user) {
        persistenceManager.save(user);
    }

    public void update(User user) {
        persistenceManager.update(user);
    }

    public User getById(String userId) {
        return persistenceManager.queryOne(QUERY_BY_ID, ImmutableMap.of("id", userId), User.class);
    }
}
