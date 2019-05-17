package com.renewable.gateway.common;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description： 常量类
 * @Author: jarry
 */
@Slf4j
public class Const {
    //系统的相关参数设置
    public static final String CURRENT_USER = "currentUser";
    public static final String EMALL = "email";
    public static final String USERNAME = "username";
    public static final String NICKNAME = "nickname";
    public static final String SENSOR_REGISTER_PREFIX = "sensorRegister_";

    //传感器类型参数（即协议标识符）
    public interface SensorType {
        //注意要进行进制转换，后面可以写得优雅一下
        //0x68 = 104
        int Inclination1 = 104;
        int Inclination2 = 105;
    }



    //传感器时间延迟
    //此处的参数以秒为单位
    public static final int MIN_PERIOD = 60;
    public static final int HOUR_PERIOD = 60 * 60;
    public static final int DAY_PERIOD = 60 * 60 * 24;


    //缓存 redis 的相关前缀
    public static final String CLEAN_TASK_PREFIX = "clean_";
    public static final String DEAL_SET_PREFIX = "set_";

    //缓存 redis 中传感器响应状态的保存时间（单位：秒）   传感器响应时间关联
    public static final int DEAL_SET_KEEP = 5;  //该类数据使用频度不高，数据量也不大（因为一般采用自动传输模式）不需要太过严格
    public static final int DEAL_SET_WAIT = 1;



    //终端服务器状态
    public enum TerminalStateEnum {


        Running(0, "终端服务器正常运行中"),
        Deleted(7, "终端服务器已删除");


        private String value;
        private Integer code;

        TerminalStateEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static TerminalStateEnum codeOf(int code) {
            for (TerminalStateEnum dataCleanType : values()) {
                if (dataCleanType.getCode() == code) {
                    return dataCleanType;
                }
            }
            log.warn("没有找到对应的枚举", code);
            throw new RuntimeException("没有找到对应的枚举");    //这里由于是在java程序内，无页面响应。故将相关信息导入到日志中。这里抛出的异常由调用方处理
        }
    }


    //传感器类型
    public enum SensorEnum {

        //        READALLR(Integer.parseInt("84",16), "同时读角度命令——传感器应答回复"),
        Inclination1(Integer.parseInt("68", 16), "倾斜传感器-1"),
        ;


        private String value;
        private Integer code;

        SensorEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static SensorEnum codeOf(int code) {
            for (SensorEnum inclinationSensor1Enum : values()) {
                if (inclinationSensor1Enum.getCode() == code) {
                    return inclinationSensor1Enum;
                }
            }
            log.warn("传感器没有找到对应的枚举", code);
            throw new RuntimeException("没有找到对应的枚举");    //这里由于是在java程序内，无页面响应。故将相关信息导入到日志中。这里抛出的异常由调用方处理
        }
    }

    //数据清洗类型
    public enum DataCleanType {

        //        READALLR(Integer.parseInt("84",16), "同时读角度命令——传感器应答回复"),
        Inclination1(Integer.parseInt("68", 16), "倾斜传感器-1"),
        NoAction(0, "拒绝数清洗：不进行数据清洗"),
        PeakAction(1, "峰值数据清洗：对特定时间间隔内只保存峰值"),
        TimeAreaAction(2, "时域数据清洗：对特定时间间隔内保存特定时域的所有数据");


        private String value;
        private Integer code;

        DataCleanType(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static DataCleanType codeOf(int code) {
            for (DataCleanType dataCleanType : values()) {
                if (dataCleanType.getCode() == code) {
                    return dataCleanType;
                }
            }
            log.warn("传感器没有找到对应的枚举", code);
            throw new RuntimeException("没有找到对应的枚举");    //这里由于是在java程序内，无页面响应。故将相关信息导入到日志中。这里抛出的异常由调用方处理
        }
    }


//    public static void main(String[] args) {
//        System.out.println(SensorEnum.valueOf("Inclination1").code);
//    }
}
