package in.divvyup.config;

import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Config {
    private Map<String, Object> configuration;

    public Object get(String configKey) {
        return this.configuration.getOrDefault(configKey, null);
    }
}
