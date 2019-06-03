package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.InitializationInclination;

/**
 * @Description：
 * @Author: jarry
 */
public interface IInitializationInclinationService {

    ServerResponse insert(InitializationInclination initializationInclination);

    ServerResponse getInitializationInclinationBySensorRegisterId(int sensorRegisterId);

    ServerResponse receiveInitializationInclinationFromMQ(InitializationInclination initializationInclination);
}
