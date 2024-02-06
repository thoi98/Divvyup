package in.divvyup.persistence.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ImmutableMap;
import in.divvyup.persistence.PersistenceManager;
import in.divvyup.persistence.model.UserGroup;

@Repository
public class UserGroupDao {
    @Autowired
    @Qualifier ("mysql")
    private PersistenceManager persistenceManager;

    private static final String QUERY_BY_USER_ID = "SELECT * FROM user_groups WHERE user_id=:userId";

    private static final String QUERY_BY_GROUP_ID = "SELECT * FROM user_groups WHERE group_id=:groupId";

    public void save(UserGroup userGroup) {
        persistenceManager.save(userGroup);
    }

    public List<UserGroup> getByUserId(String userId) {
        return persistenceManager.query(QUERY_BY_USER_ID, ImmutableMap.of("userId", userId), UserGroup.class);
    }

    public List<UserGroup> getByGroupId(String groupId) {
        return persistenceManager.query(QUERY_BY_GROUP_ID, ImmutableMap.of("groupId", groupId), UserGroup.class);
    }
}
