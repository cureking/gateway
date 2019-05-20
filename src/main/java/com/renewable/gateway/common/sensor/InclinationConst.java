package com.renewable.gateway.common.sensor;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description： 常量类
 * @Author: jarry
 */
@Slf4j
public class InclinationConst {

    public static final String SENSORNAME = "inclination";

    //倾斜传感器1的相关参数
    public interface InclinationSensor1Response {
        int SUCCESS = 0;    //即对应命令执行成功
        int FAILED = 1;  //购对应命令执行失败

        //todo 之后视具体情况，可改为int类型等
        String ABSOLUTE_ZERO_POINT = "00";
        String RELATIVE_ZERO_POINT = "01";
    }

    //相关传感器命令字解析枚举
    public enum InclinationSensor1Enum {

        READALL(Integer.parseInt("04", 16), "同时读角度命令"),
        READALLR(Integer.parseInt("84", 16), "同时读角度命令——传感器应答回复"),

        SETZERO(Integer.parseInt("05", 16), "设置相对/绝对零点"),
        SETZEROR(Integer.parseInt("85", 16), "设置相对/绝对零点——传感器应答回复命令"),
        //
        SETSPEED(Integer.parseInt("0B", 16), "设置通讯速率"),
        SETSPEEDR(Integer.parseInt("8B", 16), "设置通讯速率——传感器应答回复命令"),

        SETMOD(Integer.parseInt("0C", 16), "设置传感器输出模式"),
        SETMODR(Integer.parseInt("8C", 16), "设置传感器输出模式——传感器应答回复命令"),
        //
        SETADDR(Integer.parseInt("0F", 16), "设置模块地址命令"),
        SETADDRR(Integer.parseInt("8F", 16), "设置模块地址命令——传感器应答回复命令"),

        GETZERO(Integer.parseInt("0D", 16), "查询相对/绝对零点命令"),
        GETZEROR(Integer.parseInt("8D", 16), "查询相对/绝对零点命令——传感器应答回复命令"),

        RESP(Integer.parseInt("97", 16), "传感器应答回复命令");


        private String value;
        private int code;
        //TODO 视情形，将参数类型改为String.etc

        InclinationSensor1Enum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static InclinationSensor1Enum codeOf(int code) {
            for (InclinationSensor1Enum inclinationSensor1Enum : values()) {
                if (inclinationSensor1Enum.getCode() == code) {
                    return inclinationSensor1Enum;
                }
            }
            log.warn("倾斜传感器-1没有找到对应的枚举", code);
            throw new RuntimeException("没有找到对应的枚举");    //这里由于是在java程序内，无页面响应。故将相关信息导入到日志中。这里抛出的异常由调用方处理
        }
    }

    //todo 其他倾斜传感器的相关参数
    // 如果不是倾斜传感器，请重新建立类


    //相关传感器命令字解析枚举
    public enum InclinationInstallModeEnum {

        ONE(1, "安装模式一：水平y与垂直x：(+x,+y)"),
        TWO(2, "安装模式二：水平y与垂直x：(+x,-y)"),
        THIRD(3, "安装模式三：水平y与垂直x：(-x,+y)"),
        FOUR(4, "安装模式四：水平y与垂直x：(-x,-y)"),

        FIVE(5, "安装模式五：水平x与垂直y：(+x,+y)"),
        SIX(6, "安装模式六：水平x与垂直y：(+x,-y)"),
        SEVEN(7, "安装模式七：水平x与垂直y：(-x,+y)"),
        EIGHT(8, "安装模式八：水平x与垂直y：(-x,-y)");


        private int code;
        private String value;
        //TODO 视情形，将参数类型改为String.etc

        InclinationInstallModeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static InclinationInstallModeEnum codeOf(int code) {
            for (InclinationInstallModeEnum inclinationSensor1Enum : values()) {
                if (inclinationSensor1Enum.getCode() == code) {
                    return inclinationSensor1Enum;
                }
            }
            log.warn("倾斜传感器-1没有找到对应的枚举", code);
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public static void main(String[] args) {
        System.out.println(InclinationSensor1Enum.READALLR.code);
    }
}
