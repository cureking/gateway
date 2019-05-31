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

    ServerResponse refresh();
}
