package org.eveforge.plugin;


import com.mikuac.shiro.action.OneBot;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.eveforge.service.IServerUpTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ServerUpTimeCheckPlugin extends BotPlugin {

    @Autowired
    private IServerUpTimeService serverUpTimeService;

    private static Map<String,Bot> groups = new ConcurrentHashMap<>();




    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        if(event.getMessage().startsWith("servercheck")){
            String groupId = event.getGroupId().toString();
            if(groups.get(groupId) != null){
                bot.sendGroupMsg(Long.parseLong(groupId), "该群已添加到服务器状态监控", false);
                return MESSAGE_IGNORE;
            }
            groups.put(groupId, bot);
            log.info("群聊 {} 已添加到服务器状态监控", groupId);
            bot.sendGroupMsg(Long.parseLong(groupId), "已添加到服务器状态监控", false);
        }
        return MESSAGE_IGNORE;
    }

    @Async
    @Scheduled(cron = "30 0 19 * * ?")
    public void sendServerStatus(){
        log.info("开始检测服务器开服时间");
        Timestamp serverUpTime = serverUpTimeService.getServerUpTime();
        while(serverUpTime == null){
            serverUpTime = serverUpTimeService.getServerUpTime();
            try{
                Thread.sleep(20000);
            }catch(InterruptedException e){
                log.error("线程休眠被中断: {}", e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (!groups.isEmpty()) {
            for(Map.Entry<String, Bot> entry : groups.entrySet()) {
                String groupId = entry.getKey();
                Bot bot = entry.getValue();
                try {
                    bot.sendGroupMsg(Long.parseLong(groupId), "服务器已运行\n启动时间:" + serverUpTime, false);
                } catch (Exception e) {
                    log.error("发送消息到群组 {} 失败: {}", groupId, e.getMessage());
                }
            }
        } else {
            log.info("没有配置监控的群组");
        }
    }
}
