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
}
