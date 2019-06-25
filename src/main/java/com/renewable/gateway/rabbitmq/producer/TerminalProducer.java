package com.renewable.gateway.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.renewable.gateway.pojo.Terminal;
import com.renewable.gateway.util.JsonUtil;
import com.renewable.gateway.util.PropertiesUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.RabbitmqConstant.*;

/**
 * @Description：
 * @Author: jarry
 */
@Component("TerminalProducer")
public class TerminalProducer {

	private static final String IP_ADDRESS = PropertiesUtil.getProperty(RABBITMQ_HOST);
	private static final int PORT = Integer.parseInt(PropertiesUtil.getProperty(RABBITMQ_PORT));
	private static final String USER_NAME = PropertiesUtil.getProperty(RABBITMQ_USER_NAME);
	private static final String USER_PASSWORD = PropertiesUtil.getProperty(RABBITMQ_USER_PASSWORD);

	private static final String TERMINAL_CONFIG_TERMINAL2CENTCONTROL_EXCHANGE = "exchange-terminal-config-terminal2centcontrol";
	private static final String TERMINAL_CONFIG_TERMINAL2CENTCONTROL_QUEUE = "queue-terminal-config-terminal2centcontrol";
	private static final String TERMINAL_CONFIG_TERMINAL2CENTCONTROL_ROUTINETYPE = "topic";
	private static final String TERMINAL_CONFIG_TERMINAL2CENTCONTROL_BINDINGKEY = "terminal.config.terminal2centcontrol";
	private static final String TERMINAL_CONFIG_TERMINAL2CENTCONTROL_ROUTINGKEY = "terminal.config.terminal2centcontrol";

	public static void sendTerminalConfig(Terminal terminal) throws IOException, TimeoutException, InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(IP_ADDRESS);
		factory.setPort(PORT);
		factory.setUsername(USER_NAME);
		factory.setPassword(USER_PASSWORD);

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(TERMINAL_CONFIG_TERMINAL2CENTCONTROL_EXCHANGE, TERMINAL_CONFIG_TERMINAL2CENTCONTROL_ROUTINETYPE, true, false, null);
		channel.queueDeclare(TERMINAL_CONFIG_TERMINAL2CENTCONTROL_QUEUE, true, false, false, null);
		channel.queueBind(TERMINAL_CONFIG_TERMINAL2CENTCONTROL_QUEUE, TERMINAL_CONFIG_TERMINAL2CENTCONTROL_EXCHANGE, TERMINAL_CONFIG_TERMINAL2CENTCONTROL_BINDINGKEY);

		String terminalStr = JsonUtil.obj2String(terminal);
		channel.basicPublish(TERMINAL_CONFIG_TERMINAL2CENTCONTROL_EXCHANGE, TERMINAL_CONFIG_TERMINAL2CENTCONTROL_ROUTINGKEY, MessageProperties.PERSISTENT_TEXT_PLAIN, terminalStr.getBytes());

		channel.close();
		connection.close();
	}
}
