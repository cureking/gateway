package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.SerialSensor;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
public interface ISerialSensorService {

	ServerResponse insert(SerialSensor serialSensor);

	ServerResponse insertBatch(List<SerialSensor> serialSensorList);

	ServerResponse update(SerialSensor serialSensor);

	ServerResponse updateByUser(SerialSensor serialSensor);

	ServerResponse getSerialSensorByPortAndAddress(String port, String address);

	ServerResponse list();

	ServerResponse refresh();

	ServerResponse sendSerialSensor2MQ(List<SerialSensor> serialSensorList);

	ServerResponse receiveSerialSensorFromMQ(SerialSensor serialSensor);

	// 定时任务-读取终端数据
	ServerResponse taskLoadFromSerialSensor();

	ServerResponse loadFromSerialSensorByList(List<SerialSensor> serialSensorList);

}
