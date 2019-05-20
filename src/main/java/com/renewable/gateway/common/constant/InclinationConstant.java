package com.renewable.gateway.common.constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class InclinationConstant {
    // 数据库中Inclination相关

    // Version相关
    public static final String VERSION_NOCLEAN = "NoClean";
    public static final String VERSION_CLEANED = "Cleaned";
    public static final String VERSION_UPLOADED = "Uploaded";

    // Version升级版 Status
    public enum InclinationStateEnum {

        Noclean(0,"原始倾斜观测数据"),
        Cleaned(1,"数据已经经过清洗"),
        Uploaded(2,"数据已经提交企业服务器");

        private String value;
        private Integer code;

        InclinationStateEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static InclinationStateEnum codeOf(int code) {
            for (InclinationStateEnum dataCleanType : values()) {
                if (dataCleanType.getCode() == code) {
                    return dataCleanType;
                }
            }
            log.warn("没有找到对应的枚举", code);
            throw new RuntimeException("没有找到对应的枚举");    //这里由于是在java程序内，无页面响应。故将相关信息导入到日志中。这里抛出的异常由调用方处理
        }
    }

}
