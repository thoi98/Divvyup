package in.divvyup.persistence;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import in.divvyup.persistence.model.BaseModel;

public interface PersistenceManager {
    <T extends BaseModel> void save(T object);

    <T extends BaseModel> void update(T object);

    <T extends BaseModel> List<T> query(String query, Map<String, Object> queryParams, Class<T> entity);

    default <T extends BaseModel> T queryOne(String query, Map<String, Object> queryParams, Class<T> entity) {
        List<T> queryResultSet = query(query, queryParams, entity);
        if (CollectionUtils.isNotEmpty(queryResultSet)) {
            return queryResultSet.get(0);
        }
        return null;
    }
}
