package in.divvyup.util;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;

public class MySQLUtil {
    public static String getBlobAsString(ResultSet rs, String columnName) {
        try {
            return new String(rs.getBinaryStream(columnName).readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
