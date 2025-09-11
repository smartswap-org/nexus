package org.smartswap.log;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartswap.log.dto.LogDTO;
import org.smartswap.util.JsonMapUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.List;

@Slf4j
@Service
@Configuration
@AllArgsConstructor
public class LogService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Inserts a Log into database.
     *
     * @param logDTO The log DTO to be inserted.
     */
    public void insertLog(LogDTO logDTO) {
        String sql = """
            INSERT INTO logs (startTime, endTime, level, service, message, data)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                logDTO.startTime(),
                logDTO.endTime(),
                logDTO.level(),
                logDTO.service(),
                logDTO.message(),
                JsonMapUtils.mapToJsonString(logDTO.data())
        );
    }

    /**
     * Returns 10 logs from database.
     *
     * @return a list of 10 LogDTO.
     */
    public List<LogDTO> getSomeLogs() {
        String sql = "SELECT id, startTime, endTime, level, service, message, data FROM logs LIMIT 10";

        return jdbcTemplate.query(sql, (rs, rowNum) -> LogDTO.fromResultSet(rs));
    }

}
