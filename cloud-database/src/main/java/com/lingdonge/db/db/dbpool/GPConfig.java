package com.lingdonge.db.db.dbpool;

import lombok.Data;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 *
 * 数据库连接池配置，从db.propertis里面取出配置项，进行管理
 */
@Data
public class GPConfig {
    private String driver;
    private String url;
    private String username;
    private String password;
    private String initSize;
    private String maxSize;
    private String health;
    private String delay;
    private String period;
    private String timeout;

    /**
     * 省略set和get方法//编写构造器，在构造器中对属性进行初始化
     */
    public GPConfig() {
        Properties prop = new Properties();
        //maven项目中读取文件好像只有这中方式
        InputStream stream = this.getClass().getResourceAsStream("/resource/database.properties");
        try {
            prop.load(stream);
            //在构造器中调用setter方法，这里属性比较多，我们肯定不是一步一步的调用，建议使用反射机制
            for (Object obj : prop.keySet()) {
                //获取形参，怎么获取呢?这不就是配置文件的key去掉，去掉什么呢？去掉"jdbc."
                String fieldName = obj.toString().replace("jdbc.", "");
                Field field = this.getClass().getDeclaredField(fieldName);
                Method method = this.getClass().getMethod(toUpper(fieldName), field.getType());
                method.invoke(this, prop.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取配置文件中的key,并把他转成正确的set方法
    public String toUpper(String fieldName) {
        char[] chars = fieldName.toCharArray();
        chars[0] -= 32;    //如何把一个字符串的首字母变成大写
        return "set" + new String(chars);
    }
}