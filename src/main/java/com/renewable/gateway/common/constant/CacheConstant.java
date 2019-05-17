package com.renewable.gateway.common.constant;

/**
 * @Description：
 * @Author: jarry
 */
public class CacheConstant {
    // 随着常量的增加，所以进行分类，首先拆分出缓存相关的常量，如前缀等

    public static final int DAY_PERIOD = 60 * 60 * 24;

    // GuavaCache中TerminalConfig的相关
    public static final String TERMINAL_ID = "terminal_id";
    public static final String TERMINAL_PROJECT_ID = "terminal_project_id";
    public static final String TERMINAL_IP = "terminal_ip";
    public static final String TERMINAL_MAC = "terminal_mac";
    public static final String TERMINAL_NAME = "terminal_name";
    public static final String TERMINAL_MARK = "terminal_mark";
    public static final String TERMINAL_STATE = "terminal_state";
    public static final String TERMINAL_CREATE_TIME = "terminal_create_time";
    public static final String TERMINAL_UPDATE_TIME = "terminal_update_time";
}
