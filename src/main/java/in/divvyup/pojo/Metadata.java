package in.divvyup.pojo;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import in.divvyup.util.JSONUtil;
import lombok.Builder;

@SuppressWarnings ({ "unchecked" })
public class Metadata {
    private String stringData;

    private Map<String, Object> data;

    private boolean dirty;

    @Builder
    public Metadata(String data) {
        setData(data);
        dirty = false;
    }

    private void setData(String stringData) {
        this.stringData = stringData;
        this.data = StringUtils.isNotBlank(stringData) ? JSONUtil.fromJson(stringData, Map.class) : new HashMap<>();
    }

    public Object get(String key) {
        return data.get(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    public void put(String key, Object value) {
        data.put(key, value);
        dirty = true;
    }

    public String getStringData() {
        if (dirty) {
            stringData = JSONUtil.toJson(data);
            dirty = false;
        }
        return this.stringData;
    }
}
