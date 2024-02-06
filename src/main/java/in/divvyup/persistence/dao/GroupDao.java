package in.divvyup.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ImmutableMap;
import in.divvyup.persistence.PersistenceManager;
import in.divvyup.persistence.model.Group;

@Repository
public class GroupDao {
    @Autowired
    @Qualifier ("mysql")
    PersistenceManager persistenceManager;

    private static final String QUERY_BY_ID = "SELECT * FROM `groups` WHERE id=:id";

    public void save(Group group) {
        persistenceManager.save(group);
    }

    public void update(Group group) {
        persistenceManager.update(group);
    }

    public Group getById(String groupId) {
        return persistenceManager.queryOne(QUERY_BY_ID, ImmutableMap.of("id", groupId), Group.class);
    }
}
