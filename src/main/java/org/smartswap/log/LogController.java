package org.smartswap.log;

import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.smartswap.log.dto.LogDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogController {

    private final LogService logService;

    /**
     * Inserts a Log into database.
     *
     * @param logDTO    The new Log parameters.
     * @return          Insertion confirmation.
     */
    @PostMapping
    public ResponseEntity<String> insertLog(@RequestBody LogDTO logDTO) {
        logService.insertLog(logDTO);
        return ResponseEntity.ok("Log successfully inserted into database.");
    }

    /**
     * Get a list of Log using a specified Log as filter.
     *
     * @param filter    The filtering Log.
     * @param limit     The limit number of Log (could be null).
     * @return          A list of LogDTO.
     */
    @GetMapping
    public ResponseEntity<List<LogDTO>> getLogsByFilter(
            @RequestBody LogDTO filter,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(logService.getLogsByFilter(filter, limit));
    }

    /**
     * Returns a list of Log that has been inserted before specified date.
     *
     * @param date  The date limit for Log's list.
     * @param limit The limit number of Log (could be null).
     * @return      A list of LogDTO.
     */
    @GetMapping("/before/{date}")
    public ResponseEntity<List<LogDTO>> getBeforeDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(logService.getByDate(Timestamp.valueOf(date), "<", limit));
    }

    /**
     * Returns a list of Log that has been inserted after specified date.
     *
     * @param date  The date limit for Log's list.
     * @param limit The limit number of Log (could be null).
     * @return      A list of LogDTO.
     */
    @GetMapping("/after/{date}")
    public ResponseEntity<List<LogDTO>> getAfterDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(logService.getByDate(Timestamp.valueOf(date), ">", limit));
    }

}
