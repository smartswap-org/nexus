package org.smartswap.log.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;
import org.smartswap.util.JsonMapUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Builder
public record LogDTO(
        UUID id,
        Timestamp date,
        String level,
        String service,
        String message,
        Map<String, String> data
) {
    /**
     * Returns a LogDTO formed using a ResultSet from database Query.
     * @param rs            The database query's response.
     * @return              A LogDTO formed by the database's response.
     * @throws SQLException If specified columns do not exist.
     */
    public static LogDTO fromResultSet(ResultSet rs) throws SQLException {
        String dataJson = rs.getString("data");
        Map<String,String> dataMap = JsonMapUtils.jsonStringToMap(dataJson);

        return LogDTO.builder()
                .id(UUID.fromString(rs.getString("id")))
                .date(rs.getTimestamp("date"))
                .level(rs.getString("level"))
                .service(rs.getString("service"))
                .message(rs.getString("message"))
                .data(dataMap)
                .build();
    }
}
