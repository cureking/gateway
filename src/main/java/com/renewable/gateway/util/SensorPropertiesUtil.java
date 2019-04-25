package com.renewable.gateway.util;

import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class SensorPropertiesUtil {
    private static Properties props;

    //要在tomcat启动时，读取到其中的配置  //7-3

    static {

        String filename = "sensor.properties";

        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(filename), "UTF-8"));
        } catch (Exception e) {
            log.error("配置文件读取异常", e);
        }

    }


    public static String getProperty(String key) {
        //trim()消去空格，避免空格误事
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    //进行方法重载，便于业务的不同需求
    public static String getProperty(String key, String defaultValue) {
        //trim()消去空格，避免空格误事
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }


    //todo 目前修改配置，修改的只是内存中的数据（断电或重启就GG了）。两个方案：一者增加对本地文件的修改，在refresh静态块初始化内容，二者，直接修改内存，再将内存内容写入本地文件（推荐后者，最好使用prop自身方法）。
    public static String setProperty(@NotNull String key, String value) {

        String filename = "sensor.properties";
        String root = SensorPropertiesUtil.class.getClass().getResource("/").getPath();
        String filePath = root + filename;

        // 写入内存中
        String oldValue = (String) props.setProperty(key, value);

        //尝试将对应内存写入本地文件
        try {
            FileWriter fileWriter = new FileWriter(new File(filePath));
            props.store(fileWriter, "sensor-properties update");

            fileWriter.close();
        } catch (IOException e) {
            log.error("配置文件写入异常", e);
        }
        return oldValue;
    }


    //test
    public static void main(String[] args) {

        System.out.println(getProperty("sensor.interval"));
    }

}
