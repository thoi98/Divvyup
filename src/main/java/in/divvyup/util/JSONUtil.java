package in.divvyup.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class JSONUtil {
    private static final Gson GSON = new Gson();

    public static <T> T fromJson(String jsonText, Class<T> type) {
        return fromJson(GSON, jsonText, type);
    }

    public static <T> T fromJson(Gson gson, String jsonText, Class<T> type) {
        return gson.fromJson(jsonText, TypeToken.of(type).getType());
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static String toJson(Gson gson, Object obj) {
        return gson.toJson(obj);
    }

}
