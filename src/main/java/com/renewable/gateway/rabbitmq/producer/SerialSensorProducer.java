package com.renewable.gateway.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.renewable.gateway.pojo.SerialSensor;
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
@Component("SerialSensorProducer")
public class SerialSensorProducer {

	private static final String IP_ADDRESS = PropertiesUtil.getProperty(RABBITMQ_HOST);
	private static final int PORT = Integer.parseInt(PropertiesUtil.getProperty(RABBITMQ_PORT));
	private static final String USER_NAME = PropertiesUtil.getProperty(RABBITMQ_USER_NAME);
	private static final String USER_PASSWORD = PropertiesUtil.getProperty(RABBITMQ_USER_PASSWORD);


	private static final String SERIAL_SENSOR_TERMINAL2CENTCONTROL_EXCHANGE = "exchange-serial-sensor-terminal2centcontrol";
	private static final String SERIAL_SENSOR_TERMINAL2CENTCONTROL_QUEUE = "queue-serial-sensor-terminal2centcontrol";
	private static final String SERIAL_SENSOR_TERMINAL2CENTCONTROL_ROUTINETYPE = "topic";
	private static final String SERIAL_SENSOR_TERMINAL2CENTCONTROL_BINDINGKEY = "serial.sensor.terminal2centcontrol";
	private static final String SERIAL_SENSOR_TERMINAL2CENTCONTROL_ROUTINGKEY = "serial.sensor.terminal2centcontrol";

	public static void sendSerialSensor(List<SerialSensor> serialSensorList) throws IOException, TimeoutException, InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(IP_ADDRESS);
		factory.setPort(PORT);
		factory.setUsername(USER_NAME);
		factory.setPassword(USER_PASSWORD);

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(SERIAL_SENSOR_TERMINAL2CENTCONTROL_EXCHANGE, SERIAL_SENSOR_TERMINAL2CENTCONTROL_ROUTINETYPE, true, false, null);
		channel.queueDeclare(SERIAL_SENSOR_TERMINAL2CENTCONTROL_QUEUE, true, false, false, null);
		channel.queueBind(SERIAL_SENSOR_TERMINAL2CENTCONTROL_QUEUE, SERIAL_SENSOR_TERMINAL2CENTCONTROL_EXCHANGE, SERIAL_SENSOR_TERMINAL2CENTCONTROL_BINDINGKEY);

		String serialSensorListStr = JsonUtil.obj2StringPretty(serialSensorList);
		channel.basicPublish(SERIAL_SENSOR_TERMINAL2CENTCONTROL_EXCHANGE, SERIAL_SENSOR_TERMINAL2CENTCONTROL_ROUTINGKEY, MessageProperties.PERSISTENT_TEXT_PLAIN, serialSensorListStr.getBytes());

		channel.close();
		connection.close();
	}
}
