package org.smartswap.log;

import lombok.AllArgsConstructor;
import org.smartswap.log.dto.LogDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@AllArgsConstructor
public class LogController {

    private final LogService logService;

    /**
     * Returns 10 logs from database.
     *
     * @return a list of 10 LogDTO.
     */
    @GetMapping
    public ResponseEntity<List<LogDTO>> getSomeLogs() {
        return ResponseEntity.ok(logService.getSomeLogs());
    }

    @PostMapping
    public ResponseEntity<String> insertLog(@RequestBody LogDTO logDTO) {
        logService.insertLog(logDTO);
        return ResponseEntity.ok("Log successfully inserted into database.");
    }

}
