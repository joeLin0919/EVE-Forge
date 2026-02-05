package org.eveforge.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eveforge.repository.dto.ServerStatus;
import org.eveforge.service.IServerUpTimeService;
import org.eveforge.util.EsiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


@Service
@Slf4j
public class ServerUpTimeServiceImpl implements IServerUpTimeService {

    private final EsiClient esiClient;

    @Autowired
    public ServerUpTimeServiceImpl(EsiClient esiClient) {
        this.esiClient = esiClient;
    }

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Timestamp getServerUpTime() {
        String endpoint = "/status";
        String serverStartTime = "server_start_time";
        try {
            ServerStatus status = esiClient.get(endpoint, ServerStatus.class);
            String formerStartTime = redisTemplate.opsForValue().get(serverStartTime);
            if (formerStartTime == null){
                redisTemplate.opsForValue().set(serverStartTime, status.getStartTime().toString());
                return status.getStartTime();
            }
            if (!status.getStartTime().toString().equals(formerStartTime)&&status.getVip()==null){
                redisTemplate.opsForValue().set(serverStartTime, status.getStartTime().toString());
                return status.getStartTime();
            }
        } catch (Exception e) {
            // 捕获所有异常，记录日志，但不影响持续监测
            log.warn("获取服务器状态失败: {}", e.getMessage());
            return null;
        }
        return null;
    }
}
