package com.renewable.gateway.rabbitmq.producer.template;

import com.rabbitmq.client.*;
import com.renewable.gateway.pojo.InclinationDealedTotal;
import com.renewable.gateway.rabbitmq.producer.InclinationTotal;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description：
 * @Author: jarry
 */
@Component("InclinationSender")
public class InclinationSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String INCLINATION_TOTAL_EXCHANGE = "inclination-total-data-exchange";
    private static final String INCLINATION_TOTAL_QUEUE = "inclination-total-data-queue";
    private static final String INCLINATION_TOTAL_ROUTINETYPE = "topic";
    private static final String INCLINATION_TOTAL_BINDINGKEY = "sensor.inclination.data.total";
    private static final String INCLINATION_TOTAL_ROUTINGKEY = "sensor.inclination.data.total";


    public void send(InclinationTotal inclinationTotal) throws Exception{
//        CorrelationData correlationData = new CorrelationData();  //会出现future相关的NoClassDefFoundError的异常。表示内心极度崩溃，怎么又是这种级别的异常啊。  // java.lang.NoClassDefFoundError: org/springframework/util/concurrent/SettableListenableFuture
//        correlationData.setId(inclinationTotal.getId().toString());  //消息的唯一标识ID


        rabbitTemplate.convertAndSend(INCLINATION_TOTAL_EXCHANGE,
                INCLINATION_TOTAL_ROUTINGKEY,
                inclinationTotal);
    }


    public static InclinationTotal inclinationTotalAssemble(InclinationDealedTotal inclinationDealedTotal){
        InclinationTotal inclinationTotal = new InclinationTotal();
        inclinationTotal.setId(inclinationDealedTotal.getId());
        inclinationTotal.setOriginId(inclinationDealedTotal.getOriginId());
        inclinationTotal.setAngleInitTotal(inclinationDealedTotal.getAngleInitTotal());
        inclinationTotal.setCreateTime(inclinationDealedTotal.getCreateTime());

        return inclinationTotal;
    }
}
