package com.renewable.gateway.rabbitmq.consumer;

import com.rabbitmq.client.*;
import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.SerialSensor;
import com.renewable.gateway.service.ISerialSensorService;
import com.renewable.gateway.util.JsonUtil;
import com.renewable.gateway.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_ID;
import static com.renewable.gateway.common.constant.RabbitmqConstant.RABBITMQ_HOST;
import static com.renewable.gateway.common.constant.RabbitmqConstant.RABBITMQ_USER_NAME;
import static com.renewable.gateway.common.constant.RabbitmqConstant.RABBITMQ_USER_PASSWORD;

/**
 * @Description：
 * @Author: jarry
 */
@Component("SerialSensorConsumer")
public class SerialSensorConsumer {

    @Autowired
    private ISerialSensorService iSerialSensorService;

    private static final String SERIAL_SENSOR_CENTCONTROL2TERMINAL_QUEUE = "queue-serial-sensor-centcontrol2terminal";

    @PostConstruct
    public void messageOnTerminal() throws IOException, TimeoutException, InterruptedException {
        Address[] addresses = new Address[]{
                new Address(PropertiesUtil.getProperty(RABBITMQ_HOST))
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(PropertiesUtil.getProperty(RABBITMQ_USER_NAME));
        factory.setPassword(PropertiesUtil.getProperty(RABBITMQ_USER_PASSWORD));

        Connection connection = factory.newConnection(addresses);
        final Channel channel = connection.createChannel();
        channel.basicQos(64);   // 设置客户端最多接收未ack的消息个数，避免客户端被冲垮（常用于限流）
        Consumer consumer = new DefaultConsumer(channel) {


            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                // 1.接收数据，并反序列化出对象
                SerialSensor receiveSerialSensor = JsonUtil.string2Obj(new String(body), SerialSensor.class);

                // 2.验证是否是该终端的消息的消息     // 避免ACK其他终端的消息
                if (receiveSerialSensor.getTerminalId() == Integer.parseInt(GuavaCache.getKey(TERMINAL_ID))){
                    // 业务代码
                    ServerResponse response = iSerialSensorService.receiveSerialSensorFromMQ(receiveSerialSensor);

                    if (response.isSuccess()){
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            }
        };
        channel.basicConsume(SERIAL_SENSOR_CENTCONTROL2TERMINAL_QUEUE, consumer);
        // 等回调函数执行完毕后，关闭资源
        // 想了想还是不关闭资源，保持一个监听的状态，从而确保配置的实时更新
//        TimeUnit.SECONDS.sleep(5);
//        channel.close();
//        connection.close();
    }
}
