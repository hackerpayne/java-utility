package com.lingdonge.redis.publish;

//@RestController
//@RequestMapping("redis")
//public class RedisPublish {
//    private int m = 0;
//    @Autowired
//    private StringRedisTemplate template;
//    @RequestMapping("publish")
//    public String publish(){
//        int index = m;
//        for(int i=m;i<index+10;i++){
//            template.convertAndSend("mytopic", "这是我发第"+i+"条的消息啊");
//        }
//        return "结束";
//    }
//}