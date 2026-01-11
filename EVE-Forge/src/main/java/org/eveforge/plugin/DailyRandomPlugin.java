package org.eveforge.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.eveforge.service.IDailyRandomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


@Component
public class DailyRandomPlugin extends BotPlugin {

    private static final Logger logger = LoggerFactory.getLogger(DailyRandomPlugin.class);

    @Autowired
    private IDailyRandomService dailyRandomService;



    @Override
    public int onPrivateMessage(Bot bot, PrivateMessageEvent event) {
        if(event.getMessage().startsWith("jrcd")){
            String message = dailyRandomService.getDailyLength(event.getUserId().toString());
            bot.sendPrivateMsg(event.getUserId(), message,false);
        }
        else if(event.getMessage().startsWith("jrrp")){
            String message = dailyRandomService.getDailyLuck(event.getUserId().toString());
            bot.sendPrivateMsg(event.getUserId(), message,false);
        }
        else if(event.getMessage().startsWith("jrqq")){
            String message = dailyRandomService.getDailyLuckChanges(event.getUserId().toString());
            bot.sendPrivateMsg(event.getUserId(), message,false);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        if(event.getMessage().startsWith("jrcd")){
            String message = "[CQ:at,qq="+event.getUserId()+"] "+dailyRandomService.getDailyLength(event.getUserId().toString());
            bot.sendGroupMsg(event.getGroupId(), message,false);
        }
        else if(event.getMessage().startsWith("jrrp")){
            String message = "[CQ:at,qq="+event.getUserId()+"] "+dailyRandomService.getDailyLuck(event.getUserId().toString());
            bot.sendGroupMsg(event.getGroupId(), message,false);
        }
        else if(event.getMessage().startsWith("jrqq")){
            String message = "[CQ:at,qq="+event.getUserId()+"] "+dailyRandomService.getDailyLuckChanges(event.getUserId().toString());
            bot.sendGroupMsg(event.getGroupId(), message,false);
        }
        else if(event.getMessage().startsWith("bbcd")){
            Long userId1 = event.getSender().getUserId();
            Long userId2 = null;
            String message = event.getMessage();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[CQ:at,qq=(\\d+)\\]");
            java.util.regex.Matcher matcher = pattern.matcher(message);
            if(matcher.find()) {
                try {
                    userId2 = Long.parseLong(matcher.group(1));
                } catch (NumberFormatException e) {
                    userId2 = null;
                }
            }
            if (userId2 != null){
                String result;
                Integer compare = dailyRandomService.compareDailyLength(userId1.toString(), userId2.toString());
                if (compare > 0){
                    result = "[CQ:at,qq="+userId1+"] 你的牛~牛长度比[CQ:at,qq="+userId2+"] 长"+compare+"cm,"+dailyRandomService.getCompareLengthDescription(compare);
                }else {
                    result = "[CQ:at,qq="+userId1+"] 你的牛~牛长度比[CQ:at,qq="+userId2+"] 短"+(-compare)+"cm,"+dailyRandomService.getCompareLengthDescription(compare);
                }
                bot.sendGroupMsg(event.getGroupId(), result,false);
            }else{
                logger.info("bbcd: userId2 is null");
            }
        }
        return MESSAGE_IGNORE;
    }
}
