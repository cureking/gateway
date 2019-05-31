package com.renewable.gateway.rabbitmq.producer;


import com.rabbitmq.client.*;
import com.renewable.gateway.rabbitmq.pojo.InclinationInit;
import com.renewable.gateway.rabbitmq.pojo.InclinationTotal;
import com.renewable.gateway.util.JsonUtil;
import com.renewable.gateway.util.PropertiesUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.RabbitmqConstant.*;

/**
 * @Description：
 * @Author: jarry
 */
@Component("InclinationProducer")
public class InclinationProducer {
    //消费者client

    // 之后进行池化操作，因为每个终端服务器都会有多个消息producer，不可能每个producer都有自己的connection。
    // 所以需要确保每个终端服务器只有有限的连接，从而确保MQServer不会应为connection过多，造成性能下降，直至GG
    // 前期由于终端服务器的producer较少，可以复用一个链接。针对该链接，每个producer都有属于自己的channel。详见周报（max-connection与max-queue是可以在Web后台中设置的）

    private static final String IP_ADDRESS = PropertiesUtil.getProperty(RABBITMQ_HOST);
    private static final int PORT = Integer.parseInt(PropertiesUtil.getProperty(RABBITMQ_PORT));
    private static final String USER_NAME = PropertiesUtil.getProperty(RABBITMQ_USER_NAME);
    private static final String USER_PASSWORD = PropertiesUtil.getProperty(RABBITMQ_USER_PASSWORD);

    // 目前业务规模还很小，没必要设置太复杂的命名规则与路由规则。不过，可以先保留topic的路由策略，便于日后扩展。
    // inclinationTotal 相关配置
    private static final String INCLINATION_TOTAL_EXCHANGE = "inclination-total-data-exchange";
    private static final String INCLINATION_TOTAL_QUEUE = "inclination-total-data-queue";
    private static final String INCLINATION_TOTAL_ROUTINETYPE = "topic";
    private static final String INCLINATION_TOTAL_ROUTINGKEY = "sensor.inclination.data.total";
    // inclinationInit 相关配置
    private static final String INCLINATION_INIT_EXCHANGE = "inclination-init-data-exchange";
    private static final String INCLINATION_INIT_QUEUE = "inclination-init-data-queue";
    private static final String INCLINATION_INIT_ROUTINETYPE = "topic";
    private static final String INCLINATION_INIT_ROUTINGKEY = "sensor.inclination.data.init";

    public static void sendInclinationTotal(List<InclinationTotal> inclinationTotalList) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername(USER_NAME);
        factory.setPassword(USER_PASSWORD);

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare(INCLINATION_TOTAL_EXCHANGE, INCLINATION_TOTAL_ROUTINETYPE, true, false, null);      // String exchange, String type, boolean durable, boolean autoDelete, Map<String, Object> arguments
        channel.queueDeclare(INCLINATION_TOTAL_QUEUE, true, false, false, null);               // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        channel.queueBind(INCLINATION_TOTAL_QUEUE, INCLINATION_TOTAL_EXCHANGE, INCLINATION_TOTAL_ROUTINGKEY);

        String inclinationTotalListStr = JsonUtil.obj2StringPretty(inclinationTotalList);
        channel.basicPublish(INCLINATION_TOTAL_EXCHANGE, INCLINATION_TOTAL_ROUTINGKEY, MessageProperties.PERSISTENT_TEXT_PLAIN, inclinationTotalListStr.getBytes());

        channel.close();
        connection.close();
    }

    public static void sendInclinationInit(List<InclinationInit> inclinationInitList) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername(USER_NAME);
        factory.setPassword(USER_PASSWORD);

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare(INCLINATION_INIT_EXCHANGE, INCLINATION_INIT_ROUTINETYPE, true, false, null);      // String exchange, String type, boolean durable, boolean autoDelete, Map<String, Object> arguments
        channel.queueDeclare(INCLINATION_INIT_QUEUE, true, false, false, null);               // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        channel.queueBind(INCLINATION_INIT_QUEUE, INCLINATION_INIT_EXCHANGE, INCLINATION_INIT_ROUTINGKEY);

        String inclinationInitListStr = JsonUtil.obj2StringPretty(inclinationInitList);
        AMQP.BasicProperties properties =
                new AMQP.BasicProperties("text/plain",
                        null,
                        null,
                        2,
                        0, null, null, null,
                        null, null, null,
                        null,     // 用户ID，这里可以设置为终端服务器ID，之后完成配置中心后，并设定缓存配置初始化后，可以从缓存中获取终端服务器ID。    // 如果没有ID，就发送IP，然后中控室通过IP分配新的ID。（不通过全局唯一ID来实现，太花时间了，目前规模也不需要）
                        null,
                        null);

        channel.basicPublish(INCLINATION_INIT_EXCHANGE, INCLINATION_INIT_ROUTINGKEY, properties, inclinationInitListStr.getBytes());
        Thread.currentThread().sleep(20);

        channel.close();
        connection.close();
    }

}
