package org.smartswap.log;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Log {

    @Id
    private UUID id;
    private Timestamp startTime;
    private Timestamp endTime;
    private String level;
    private String service;
    private String message;
    private String data;

}
