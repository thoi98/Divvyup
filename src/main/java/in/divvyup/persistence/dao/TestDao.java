package in.divvyup.persistence.dao;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.google.common.collect.ImmutableMap;
import in.divvyup.persistence.PersistenceManager;
import in.divvyup.persistence.model.Test;

@Repository
public class TestDao {
    @Autowired
    @Qualifier ("mysql")
    PersistenceManager persistenceManager;

    private static final String QUERY_ALL = "SELECT * FROM test";

    public void insert(Test obj) {
        persistenceManager.save(obj);
    }

    public List<Test> getAll() {
        return persistenceManager.query(QUERY_ALL, new HashMap<>(), Test.class);
    }
}
