package com.renewable.gateway.rabbitmq.consumer;

import com.rabbitmq.client.*;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.Terminal;
import com.renewable.gateway.service.ITerminalService;
import com.renewable.gateway.util.JsonUtil;
import com.renewable.gateway.util.PropertiesUtil;
import com.sun.org.apache.bcel.internal.generic.FADD;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = TERMINAL_CONFIG_CENTCONTROL2TERMINAL_QUEUE, declare = "true"),
//            exchange = @Exchange(value = TERMINAL_CONFIG_CENTCONTROL2TERMINAL_EXCHANGE, declare = "true", type = TERMINAL_CONFIG_CENTCONTROL2TERMINAL_ROUTINETYPE),
//            key = TERMINAL_CONFIG_CENTCONTROL2TERMINAL_BINDINGKEY
//    ))
//    @RabbitHandler
//    public void messageOnTerminal(@Payload Terminal terminal, @Headers Map<String,Object> headers, Channel channel)throws IOException {
//
//        // 2.业务逻辑
//        ServerResponse response = iTerminalService.getTerminalFromRabbitmq(terminal);
//        System.out.println("updated terminal config: "+JsonUtil.obj2String(terminal));
//
//        // 3.确认
//        if (response.isSuccess()){
//            Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
//            channel.basicAck(deliveryTag,false);
//        }
//    }

    public static void messageOnTerminal() throws IOException, TimeoutException, InterruptedException {
        Address[] addresses =new Address[]{
                new Address(PropertiesUtil.getProperty(RABBITMQ_HOST))
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(PropertiesUtil.getProperty(RABBITMQ_USER_NAME));
        factory.setPassword(PropertiesUtil.getProperty(RABBITMQ_USER_PASSWORD));

        Connection connection = factory.newConnection(addresses);
        final Channel channel = connection.createChannel();
        channel.basicQos(64);   // 设置客户端最多接收未ack的消息个数，避免客户端被冲垮（常用于限流）
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("recv message: "+new String(body));

//                ServerResponse response = iTerminalService.getTerminalFromRabbitmq(JsonUtil.string2Obj(new String(body),Terminal.class));
//                System.out.println(JsonUtil.string2Obj(new String(body),Terminal.class).getId());
                System.out.println("updated terminal config: "+ Arrays.toString(body));

                try{
                    TimeUnit.SECONDS.sleep(1);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(TERMINAL_CONFIG_CENTCONTROL2TERMINAL_QUEUE,consumer);
        // 等回调函数执行完毕后，关闭资源
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        connection.close();
    }

    public static void main(String[] args) {

        try {
            messageOnTerminal();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
