package com.renewable.gateway.common.constant;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class WarningConstant {

    // Global
    // Warning中level的设置
    public enum WarningLevelEnum {

        BlueAlert(1,"蓝色警报"),
        YellowAlert(2,"黄色警报"),
        OrangeAlert(3,"橙色警报"),
        RedAlert(4,"红色警报");

        private String value;
        private Integer code;

        WarningLevelEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static WarningLevelEnum codeOf(int code) {
            for (WarningLevelEnum warningLevelEnum : values()) {
                if (warningLevelEnum.getCode() == code) {
                    return warningLevelEnum;
                }
            }
            log.warn("没有找到对应的枚举", code);
            throw new RuntimeException("没有找到对应的枚举");    //这里由于是在java程序内，无页面响应。故将相关信息导入到日志中。这里抛出的异常由调用方处理
        }
    }

    // Warning中SensorType的设置
    public enum SensorTypeEnum {

        Inclination_ACA826T(1,"ACA826T倾斜传感器"),
        Inclination_HCA526T(2,"HCA526T倾斜传感器");

        private String value;
        private Integer code;

        SensorTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static SensorTypeEnum codeOf(int code) {
            for (SensorTypeEnum sensorTypeEnum : values()) {
                if (sensorTypeEnum.getCode() == code) {
                    return sensorTypeEnum;
                }
            }
            log.warn("没有找到对应的枚举", code);
            throw new RuntimeException("没有找到对应的枚举");    //这里由于是在java程序内，无页面响应。故将相关信息导入到日志中。这里抛出的异常由调用方处理
        }
    }


    // part
    // Warning中InclinationDealedInit相关
    //    public static final double INCLINATIONINIT_LIMIT = 0.2;     // 这个值作为配置，之后应该会整合到数据库中，或等以后做了软配置中心。
    public static final double INCLINATIONINIT_LIMIT = 1.74;    // 这个值是测试使用的
    //    public static final long INCLINATIONINIT_DURATION = 1000 * 60 * 60 * 24;    // 即使服务器出现问题了，一天总修好了吧。
    public static final long INCLINATIONINIT_DURATION = 1000 * 60 * 60 * 24 * 30L;    // 这里设置为30天，主要是为了便于测试。谁让硬件把原来型号的传感器拿走了。。。
    public static final int INCLINATIONINIT_COUNT_LIMIT = 60 * 24 * 10;  // 即使出现事故，一次也只提取有限的数据，避免一次提取过多的数据，造成系统内存震荡等问题，导致程序出错。
    public static final String INCLINATION_LAST_CHECK_KEY = "INCLINATION_LAST_CHECK_KEY";
    public static final String INCLINATION_LAST_CHECK_PREFIX = "INCLINATION_LAST_CHECK_PREFIX_";
    public static final long INCLINATIONINIT_DURATION_BY_TWO_CHECK = 1000 * 60 * 5L; // 两次Check数据间以5分钟，来决定是否为Warning，应该合适吧。之后可以再调整。


}
