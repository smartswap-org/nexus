package org.smartswap.log;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartswap.log.dto.LogDTO;
import org.smartswap.util.JsonMapUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.RecordComponent;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            INSERT INTO logs (date, level, service, message, data)
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                logDTO.date(),
                logDTO.level(),
                logDTO.service(),
                logDTO.message(),
                JsonMapUtils.mapToJsonString(logDTO.data())
        );
    }

    /**
     * Makes a select request using specified params and returns the result as LogDTO.
     *
     * @param sql       The SQL request.
     * @param params    The params (list of objects).
     * @return          A list of LogDTO.
     */
    private List<LogDTO> select(String sql, Object... params ) {
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> LogDTO.fromResultSet(rs),
                params
        );
    }

    /**
     * Makes a select request using specified params and limit of elements
     * and adds default ORDER BY date DESC if not set.
     *
     * @param sql       The SQL request.
     * @param limit     The limit number of Log (could be null).
     * @param params    The request's parameters.
     * @return          A list of LogDTO.
     */
    private List<LogDTO> selectWithLimit(String sql, Integer limit, Object... params) {
        sql = sql.contains("ORDER BY") ? sql : sql.concat(" ORDER BY date DESC");
        if (limit != null && limit > 0) {
            return select(sql + " LIMIT ?", Stream.concat(Arrays.stream(params), Stream.of(limit)).toArray());
        }
        return select(sql, params);
    }

    /**
     * Makes a select request using specified params, number of maximum elements and order property.
     *
     * @param sql       The SQL request.
     * @param order     The order property.
     * @param limit     The limit number of Log (could be null).
     * @param params    The request's parameters.
     * @return          A list of LogDTO.
     */
    private List<LogDTO> selectWithLimit(String sql, String order, Integer limit, Object... params) {
        return selectWithLimit(sql + "ORDER BY ?", limit, Stream.concat(Arrays.stream(params), Stream.of(order)).toArray());
    }

    /**
     * Get a list of Log using a specified Log as filter.
     *
     * @param filter    The filtering Log.
     * @param limit     The limit number of Log (could be null).
     * @return          A list of LogDTO.
     */
    public List<LogDTO> getLogsByFilter(LogDTO filter, Integer limit) {
        StringBuilder sql = new StringBuilder("""
            SELECT id, date, level, service, message, data
            FROM logs
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        for (var component : LogDTO.class.getRecordComponents()) {
            try {
                Object value = component.getAccessor().invoke(filter);
                if (value != null) {
                    String name = component.getName();

                    if ("message".equals(name)) {
                        sql.append(" AND message LIKE ?");
                        params.add("%" + value + "%");
                    } else if ("data".equals(name)) {
                        sql.append(" AND ").append(name).append(" = ?");
                        params.add(JsonMapUtils.mapToJsonString((Map<String, String>) value));
                    } else {
                        sql.append(" AND ").append(name).append(" = ?");
                        params.add(value);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return selectWithLimit(sql.toString(), limit, params.toArray());
    }

    /**
     * Gets a List of logs which has been created before or after specified date.
     *
     * @param date      The date limit.
     * @param operator  Could be ">" (after specified date) or "<" (before specified date);
     * @param limit     The limit number of Log (could be null).
     * @return          A list of LogDTO.
     */
    public List<LogDTO> getByDate(Timestamp date, String operator, Integer limit) {
        String operatorToBeUsed = operator.trim();

        if (!(operatorToBeUsed.equals(">") || operatorToBeUsed.equals("<"))) {
            throw new IllegalArgumentException("Invalid date caller argument.");
        }

        String sql = String.format("""
            SELECT id, date, level, service, message, data
            FROM logs
            WHERE date %s ?
        """, operatorToBeUsed);

        return selectWithLimit(sql, limit, date);
    }
}
