package com.renewable.gateway.rabbitmq.producer;


import com.rabbitmq.client.*;
import com.renewable.gateway.pojo.InitializationInclination;
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
@Component("InitializationProducer")
public class InitializationProducer {

	private static final String IP_ADDRESS = PropertiesUtil.getProperty(RABBITMQ_HOST);
	private static final int PORT = Integer.parseInt(PropertiesUtil.getProperty(RABBITMQ_PORT));
	private static final String USER_NAME = PropertiesUtil.getProperty(RABBITMQ_USER_NAME);
	private static final String USER_PASSWORD = PropertiesUtil.getProperty(RABBITMQ_USER_PASSWORD);

	// 目前业务规模还很小，没必要设置太复杂的命名规则与路由规则。不过，可以先保留topic的路由策略，便于日后扩展。
	// inclinationTotal 相关配置
	private static final String INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_EXCHANGE = "initialization-inclination-exchange-terminal2centcontrol";
	private static final String INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_QUEUE = "initialization-inclination-queue-terminal2centcontrol";
	private static final String INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_ROUTINETYPE = "topic";
	private static final String INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_BINDINGKEY = "sensor.initialization.inclination.terminal2centcontrol";
	private static final String INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_ROUTINGKEY = "sensor.initialization.inclination.terminal2centcontrol";

	public static void sendInitializationInclination(List<InitializationInclination> initializationInclinationList) throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(IP_ADDRESS);
		factory.setPort(PORT);
		factory.setUsername(USER_NAME);
		factory.setPassword(USER_PASSWORD);

		Connection connection = factory.newConnection();

		Channel channel = connection.createChannel();
		channel.exchangeDeclare(INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_EXCHANGE, INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_ROUTINETYPE, true, false, null);      // String exchange, String type, boolean durable, boolean autoDelete, Map<String, Object> arguments
		channel.queueDeclare(INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_QUEUE, true, false, false, null);               // String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
		channel.queueBind(INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_QUEUE, INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_EXCHANGE, INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_BINDINGKEY);

		String initializationInclinationListStr = JsonUtil.obj2StringPretty(initializationInclinationList);
		channel.basicPublish(INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_EXCHANGE, INITIALIZATION_INCLINATION_TERMINAL2CENTCONTROL_ROUTINGKEY, MessageProperties.PERSISTENT_TEXT_PLAIN, initializationInclinationListStr.getBytes());

		channel.close();
		connection.close();
	}

}
