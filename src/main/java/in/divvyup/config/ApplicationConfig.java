package in.divvyup.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings ({ "unchecked" })
@Configuration
@Slf4j
public class ApplicationConfig {
    @Value ("${ENV_VARIABLE_ENVIRONMENT}")
    private String env;

    Yaml yaml = new Yaml();

    @Bean (name = "config")
    public Config config() {

        // load common properties from config/properties.yml
        InputStream commonPropertiesFileStream = this.getClass().getClassLoader().getResourceAsStream("config/common/properties.yml");
        Map<String, Object> commonConfig = yaml.load(commonPropertiesFileStream);
        Map<String, Object> configMap = new HashMap<>(commonConfig);

        ENV environment = this.getApplicationEnvironment();
        log.info("Application execution environment: {}", environment);
        InputStream environmentPropertiesFileStream;

        // For test environment, first load dev/properties.yml to avoid redundant entries between dev/properties.yml and common/properties.yml
        if (environment.equals(ENV.TEST)) {
            environmentPropertiesFileStream =
                    this.getClass().getClassLoader().getResourceAsStream(String.format("config/%s/properties.yml", ENV.DEV.getName()));
            getConfigMapFromPropertiesFile(environment.getName(), configMap, environmentPropertiesFileStream);
        }
        environmentPropertiesFileStream =
                this.getClass().getClassLoader().getResourceAsStream(String.format("config/%s/properties.yml", environment.getName()));
        getConfigMapFromPropertiesFile(environment.getName(), configMap, environmentPropertiesFileStream);
        Config config = new Config();
        config.setConfiguration(configMap);
        return config;
    }

    private void getConfigMapFromPropertiesFile(String envName, Map<String, Object> configMap, InputStream environmentPropertiesFileStream) {
        Map<String, Object> environmentSpecificConfig = yaml.load(environmentPropertiesFileStream);
        for (Map.Entry<String, Object> entries : environmentSpecificConfig.entrySet()) {
            String key = entries.getKey();
            Object value = entries.getValue();
            if (configMap.containsKey(key)) {
                Object configMapValue = configMap.get(key);
                if ((configMapValue instanceof String && !(value instanceof String)) || (configMapValue instanceof Integer && !(value instanceof Integer)) || (configMapValue instanceof Double && !(value instanceof Double)) || (configMapValue instanceof List && !(value instanceof List)) || (configMapValue instanceof Map && !(value instanceof Map))) {
                    throw new RuntimeException(
                            String.format("Conflicting types for property key %s in common/properties.yml and %s/properties.yml", key, envName));
                }
                if (!(value instanceof Map)) {
                    configMap.put(key, value);
                } else {
                    Map<String, Object> nestedConfig =
                            getConsolidatedMapConfig((Map<String, Object>) configMap.get(key), (Map<String, Object>) value, envName);
                    configMap.put(key, nestedConfig);
                }
            } else {
                configMap.put(key, value);
            }
        }
    }

    public ENV getApplicationEnvironment() {
        if (StringUtils.isBlank(this.env)) {
            return ENV.DEV;
        }
        return Stream.of(ENV.values()).filter(e -> e.getName().equalsIgnoreCase(this.env)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private Map<String, Object> getConsolidatedMapConfig(Map<String, Object> configMap, Map<String, Object> value, String environmentFileName) {
        Map<String, Object> consolidatedMap = new HashMap<>(configMap);
        for (Map.Entry<String, Object> entries : value.entrySet()) {
            String k = entries.getKey();
            Object v = entries.getValue();
            if (configMap.containsKey(k)) {
                Object configMapValue = configMap.get(k);
                if ((configMapValue instanceof String && !(v instanceof String)) || (configMapValue instanceof Integer && !(v instanceof Integer)) || (configMapValue instanceof Double && !(v instanceof Double)) || (configMapValue instanceof List && !(v instanceof List)) || (configMapValue instanceof Map && !(v instanceof Map))) {
                    throw new RuntimeException(
                            String.format("Conflicting types for property key %s in common/properties.yml and %s/properties.yml", k,
                                    environmentFileName));
                }
                if (!(v instanceof Map)) {
                    consolidatedMap.put(k, v);
                } else {
                    assert configMapValue instanceof Map;
                    consolidatedMap.put(k,
                            getConsolidatedMapConfig((Map<String, Object>) configMapValue, (Map<String, Object>) v, environmentFileName));
                }
            } else {
                consolidatedMap.put(k, v);
            }
        }
        return consolidatedMap;
    }
}
