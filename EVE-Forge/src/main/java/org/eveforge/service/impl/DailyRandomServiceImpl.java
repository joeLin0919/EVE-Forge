package org.eveforge.service.impl;

import org.codehaus.plexus.util.StringUtils;
import org.eveforge.service.IDailyRandomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;


@Service
public class DailyRandomServiceImpl implements IDailyRandomService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String getDailyLength(String userId) {
        String key = "daily_length_" + userId;
        String value = redisTemplate.opsForValue().get(key);
        if(value != null){
            return getLengthDescription(Integer.parseInt(value));
        }
        Integer dailyLength = FormDailyLength(userId);
        return getLengthDescription(dailyLength);
    }

    @Override
    public Integer compareDailyLength(String userId1, String userId2) {
        String key1 = "daily_length_" + userId1;
        String key2 = "daily_length_" + userId2;
        String value1 = redisTemplate.opsForValue().get(key1);
        String value2 = redisTemplate.opsForValue().get(key2);
        if(value1==null){
            value1=FormDailyLength(userId1).toString();
        }
        if(value2==null){
            value2=FormDailyLength(userId2).toString();
        }
        return Integer.parseInt(value1)-Integer.parseInt(value2);
    }

    @Override
    public String getDailyLuck(String userId) {
        String key = "daily_luck_" + userId;
        String value =redisTemplate.opsForValue().get(key);
        if(value != null){
            return getLuckDescription(Integer.parseInt(value));
        }
        Integer dailyLength = FormDailyLuck(userId);
        return getLuckDescription(dailyLength);
    }

    @Override
    public String getDailyLuckChanges(String userId) {
        String key = "daily_luck_" + userId;
        String value =redisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(value)){
            return "你还没获取今天的幸运指数哦~快输入jrrp试试吧！";
        }
        String changeKey = "daily_luck_change_" + userId;
        if(redisTemplate.opsForValue().get(changeKey) != null){
            return "你已经求过签辣！等明天吧~";
        }
        Random random = new Random();
        Integer dailyChange = random.nextInt(5);
        switch(dailyChange) {
            case 0:
                value = String.valueOf(Integer.parseInt(value)-20);
                break;
            case 1:
                value = String.valueOf(Integer.parseInt(value)-10);
                break;
            case 2:
                value = String.valueOf(Integer.parseInt(value)-5);
                break;
            case 3:
                break;
            case 4:
                value = String.valueOf(Integer.parseInt(value)+10);
        }
        if(Integer.parseInt(value)<0){
            value = "0";
        }
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.opsForValue().set(changeKey, "1");
        long expireTime = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
        redisTemplate.expireAt(changeKey, Instant.ofEpochSecond(expireTime));
        return getChangeDescription(dailyChange);
    }

    @Override
    public String getCompareLengthDescription(Integer compare) {
        if (compare == 0){
            return "真是针尖对锋芒，土匪遇流氓啊！";
        }else if(compare>0&&compare<=10){
            return "不错的成绩，努力一下或许可以喔(○｀ 3′○)";
        }else if (compare>10){
            return "大胜利！ta一定会屈服于你的淫~威之下(╯▔皿▔)╯";
        }else if(compare<0&&compare>=-10){
            return "可惜可惜，只差一点了(T_T)";
        }else{
            return "快跑！！！！！";
        }
    }

    private Integer FormDailyLength(String userId){
        String key = "daily_length_" + userId;
        Random random = new Random();
        long expireTime = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
        Integer dailyLength = random.nextInt(31);
        redisTemplate.opsForValue().set(key, dailyLength.toString());
        redisTemplate.expireAt(key, Instant.ofEpochSecond(expireTime));
        return dailyLength;
    }

    private Integer FormDailyLuck(String userId){
        String key = "daily_luck_" + userId;
        Random random = new Random();
        long expireTime = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
        Integer dailyLuck = random.nextInt(101);
        redisTemplate.opsForValue().set(key, dailyLuck.toString());
        redisTemplate.expireAt(key, Instant.ofEpochSecond(expireTime));
        return dailyLuck;
    }

    private String getLuckDescription(Integer value) {
        String description;
        if(value==0){
            description="大吉霸！";
        }else if(value<15){
            description="大吉";
        }else if(value<40){
            description="吉";
        }else if(value<65){
            description="小吉";
        }else if(value<75){
            description="小凶";
        }else{
            description="迷信是不对的哦~祝你有美好的一天！";
            return description;
        }
        return ("您今日的幸运指数是"+value+"/100(越低越好)，为\""+description+"\"");
    }

    private String getChangeDescription(Integer value){
        String description;
        switch(value) {
            case 0:
                description = "邦邦个邦！恭喜你抽中上上签！幸运指数-20！";
                break;
            case 1:
                description = "芜湖！恭喜你抽中上吉签！幸运指数-10！";
                break;
            case 2:
                description = "哦耶~你抽中了中吉签！幸运指数-5！";
                break;
            case 3:
                description = "呃呃呃，你抽中了中平签，运气好像没有发生变化呢~";
                break;
            case 4:
                description = "呜呜呜TAT，你抽到了下下签，幸运指数+10！QAQ";
                break;
            default:
                description = "你好像还没求签呢，快输入jrqq试试吧~";
        }
        description+="\n再输入jrrp看看成果吧~";
        return description;
    }


    private String getLengthDescription(Integer value){
        String description;
        if (value == 0) {
            description="♂今天的牛~牛长度是...什么嘛，今天你是小姐姐呀~(●ˇ∀ˇ●)";
        }else if (value < 5){
            description="♂今天的牛~牛长度是"+value+"cm, 什么嘛~原来是小~豆~丁~(•̀ ω •́)✧";
        }else if (value < 10){
            description="♂今天的牛~牛长度是"+value+"cm, 小小的也很可爱哦~o(*￣▽￣*)ブ";
        }else if (value < 15){
            description="♂今天的牛~牛长度是"+value+"cm, 还行，也不是不能接受(*/ω＼*)";
        }else if (value < 20){
            description="♂今天的牛~牛长度是"+value+"cm，斯哈~斯哈~快来和我愉快地玩耍(❤ ω ❤)";
        }else if (value < 25){
            description="♂今天的牛~牛长度是"+value+"cm, 哦齁齁齁❤中嘞~额滴娘~中嘞~(p≧w≦q)";
        }else {
            description="♂今天的牛~牛长度是"+value+"cm, 这就是阿姆斯特朗回旋加速喷气式阿姆斯特朗炮吗，还原度真高啊！（⊙ｏ⊙）";
        }
        return description;
    }
}
