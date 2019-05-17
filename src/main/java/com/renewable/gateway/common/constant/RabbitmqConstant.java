package com.renewable.gateway.common.constant;

/**
 * @Description：
 * @Author: jarry
 */
public class RabbitmqConstant {

    // 由于常量表的增长，开始进行常量表的拆分。     // 话说，这个常量表应该只有RabbitMQ使用，所以之后，应该会将该常量表移动到对应MQ模块下。

    /* RabbitMQ static constant name */
    public static final String RABBITMQ_HOST = "rabbit.server.ip";
    public static final String RABBITMQ_PORT = "rabbit.server.port";
    public static final String RABBITMQ_USER_NAME = "rabbit.server.name";
    public static final String RABBITMQ_USER_PASSWORD = "rabbit.server.password";
    public static final String RABBITMQ_VIRTURAL_HOST = "rabbit.server.virtual-host";
}
