package in.divvyup.persistence.managers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import in.divvyup.DivvyupApplication;
import in.divvyup.persistence.PersistenceManager;
import in.divvyup.persistence.model.BaseModel;
import in.divvyup.pojo.Metadata;
import in.divvyup.util.DateTimeUtil;

@Repository (value = "mysql")
@SuppressWarnings ({ "unchecked" })
public class MySQLPersistenceManager implements PersistenceManager {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private final Map<Type, Class<?>> rowMappers;

    public MySQLPersistenceManager() {
        rowMappers = new HashMap<>();
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(DivvyupApplication.class.getPackage().getName()));
        Set<Class<? extends RowMapper>> rowMapperClasses = reflections.getSubTypesOf(RowMapper.class);
        for (Class<?> c : rowMapperClasses) {
            Type type = ((ParameterizedType) c.getGenericInterfaces()[0]).getActualTypeArguments()[0];
            this.rowMappers.put(type, c);
        }
    }

    public static final Gson GSON =
            new GsonBuilder().serializeNulls().registerTypeAdapter(ZonedDateTime.class, (JsonSerializer<ZonedDateTime>) (src, typeOfSrc, context) -> {
                                 return context.serialize(Optional.ofNullable(src).map(x -> DateTimeUtil.formattedDate(x, "yyyy-MM-dd HH:mm:ss")).orElse(null));
                             }).registerTypeAdapter(ZonedDateTime.class, (JsonDeserializer<ZonedDateTime>) (src, typeOfSrc, context) -> {
                                 return Optional.ofNullable(src).map(x -> ZonedDateTime.parse(x.getAsString())).orElse(null);
                             }).registerTypeAdapter(Metadata.class,
                                     (JsonSerializer<Metadata>) (src, typeOfSrc, context) -> context.serialize(src.getStringData()))
                             .enableComplexMapKeySerialization().create();

    @Override
    public <T extends BaseModel> void save(T object) {
        // Set id on the object if not already set
        if (StringUtils.isBlank(object.getId())) {
            object.setId(RandomStringUtils.randomAlphanumeric(12));
        }
        object.setCreated(DateTimeUtil.currentTime());
        this.jdbcTemplate.update(object.getInsertQuery(), GSON.fromJson(GSON.toJson(object), Map.class));
    }

    @Override
    public <T extends BaseModel> void update(T object) {
        object.setUpdated(DateTimeUtil.currentTime());
        this.jdbcTemplate.update(object.getUpdateQuery(), GSON.fromJson(GSON.toJson(object), Map.class));
    }

    @Override
    public <T extends BaseModel> List<T> query(String query, Map<String, Object> queryParams, Class<T> entity) {
        try {
            return this.jdbcTemplate.query(query, queryParams, (RowMapper<T>) this.rowMappers.get(entity).newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
