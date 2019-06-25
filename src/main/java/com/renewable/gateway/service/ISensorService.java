package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.Sensor;

/**
 * @Description：
 * @Author: jarry
 */
public interface ISensorService {

	ServerResponse insertSensor(Sensor sensor);

	ServerResponse updateSensor(Sensor sensor);

	ServerResponse getSensorById(Integer sensorId);

	ServerResponse listSensor();

}
