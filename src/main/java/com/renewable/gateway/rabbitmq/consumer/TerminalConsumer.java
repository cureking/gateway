package com.renewable.gateway.rabbitmq.consumer;

import com.rabbitmq.client.*;
import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.Terminal;
import com.renewable.gateway.service.ITerminalService;
import com.renewable.gateway.util.JsonUtil;
import com.renewable.gateway.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_IP;
import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_MAC;
import static com.renewable.gateway.common.constant.RabbitmqConstant.*;

/**
 * @Description：
 * @Author: jarry
 */
@Component
public class TerminalConsumer {

    @Autowired
    private ITerminalService iTerminalService;

    private static final String TERMINAL_CONFIG_CENTCONTROL2TERMINAL_EXCHANGE = "exchange-terminal-config-centcontrol2terminal";
    private static final String TERMINAL_CONFIG_CENTCONTROL2TERMINAL_QUEUE = "queue-terminal-config-centcontrol2terminal";
    private static final String TERMINAL_CONFIG_CENTCONTROL2TERMINAL_ROUTINETYPE = "topic";
    private static final String TERMINAL_CONFIG_CENTCONTROL2TERMINAL_BINDINGKEY = "terminal.config.centcontrol2terminal";

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
                Terminal receiveTerminalConfig = JsonUtil.string2Obj(new String(body), Terminal.class);

                // 2.验证是否是该终端的消息的消息     // 避免ACK其他终端的消息
                if (receiveTerminalConfig.getMac() == GuavaCache.getKey(TERMINAL_MAC)){
                    // 业务代码
                    ServerResponse response = iTerminalService.getTerminalFromRabbitmq(receiveTerminalConfig);
                    // 重启之后，就解决问题了。 果然是重启大法好啊。  // 然后又出问题了。。。不过目前找到问题的根源是因为双方content-type设置的问题  // 终于解决了。确实是上述问题造成的。不过在那儿之后又引出了@Payload的问题，不过也解决了。但是对原理还是不够了解，只是从应用角度解决（碰巧，这种解决方案不算太难看）

                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(TERMINAL_CONFIG_CENTCONTROL2TERMINAL_QUEUE, consumer);
        // 等回调函数执行完毕后，关闭资源
        // 想了想还是不关闭资源，保持一个监听的状态，从而确保配置的实时更新
//        TimeUnit.SECONDS.sleep(5);
//        channel.close();
//        connection.close();
    }
}
