package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.SensorRegister;

/**
 * @Description：
 * @Author: jarry
 */
public interface ISensorRegisterService {

	ServerResponse insert(SensorRegister sensorRegister);

	ServerResponse receiveSensorRegisterFromMQ(SensorRegister sensorRegister);

	ServerResponse getSensorRegisterById(Integer sensorRegisterId);

	ServerResponse listSensorRegister();

	ServerResponse update(SensorRegister sensorRegister);

	ServerResponse updateEnterprise(SensorRegister sensorRegister);
}
