package in.divvyup.config;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SuppressWarnings ({ "unchecked" })
@Configuration
public class PersistenceConfig {
    @Autowired
    Config applicationConfig;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        Map<String, Object> databaseConfig = (Map<String, Object>) applicationConfig.get("database");
        dataSource.setDriverClassName((String) databaseConfig.get("driverClassName"));
        dataSource.setUrl((String) databaseConfig.get("host"));
        dataSource.setUsername((String) databaseConfig.get("userName"));
        dataSource.setPassword((String) databaseConfig.get("password"));
        return dataSource;
    }
}
