package org.eveforge.repository.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ServerStatus {

    @JsonProperty("players")
    private Integer players;

    @JsonProperty("server_version")
    private String serverVersion;

    @JsonProperty("start_time")
    private Timestamp startTime;

    @JsonProperty("vip")
    private Boolean vip = false;
}
