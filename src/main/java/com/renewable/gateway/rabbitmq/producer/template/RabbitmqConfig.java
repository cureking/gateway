package com.renewable.gateway.rabbitmq.producer.template;


import com.renewable.gateway.util.PropertiesUtil;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Description：
 * @Author: jarry
 */
@Configuration
@ComponentScan(basePackages = {"com.renewable.gateway.*"})
public class RabbitmqConfig {
    //    注入连接工厂，spring的配置，springboot可以配置在属性文件中
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connection = new CachingConnectionFactory();
        connection.setHost(PropertiesUtil.getProperty(RABBITMQ_HOST));
        connection.setPort(Integer.parseInt(PropertiesUtil.getProperty(RABBITMQ_PORT)));
        connection.setUsername(PropertiesUtil.getProperty(RABBITMQ_USER_NAME));
        connection.setPassword(PropertiesUtil.getProperty(RABBITMQ_USER_PASSWORD));
        connection.setVirtualHost(PropertiesUtil.getProperty(RABBITMQ_VIRTURAL_HOST));
        return connection;
    }

    //    配置RabbitAdmin来管理rabbit
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //用RabbitAdmin一定要配置这个，spring加载的是后就会加载这个类================特别重要
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

//    // 以下为AMQP配置队列绑定等，spring容器加载时候就能够注入
//    //  采用AMQP定义队列、交换器、绑定等
//    @Bean(name="direct.queue01")
//    public Queue queue001() {
//        return new Queue("direct.queue01", true, false, false);
//    }
//    @Bean(name="test.direct01")
//    public DirectExchange directExchange() {
//        return new DirectExchange("test.direct01", true, false, null);
//    }
//   @Bean
//   public Binding bind001() {
//      return BindingBuilder.bind(queue001()).to(directExchange()).with("mq.#");
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }
}
