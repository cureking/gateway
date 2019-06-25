package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.InitializationInclination;

/**
 * @Description：
 * @Author: jarry
 */
public interface IInitializationInclinationService {

	ServerResponse insert(InitializationInclination initializationInclination);

	ServerResponse update(InitializationInclination initializationInclination);

	/**
	 * 此更新方法，会同步更新终端服务器与中控服务器的相关配置
	 * @param initializationInclination
	 * @return
	 */
	ServerResponse updateEnterprise(InitializationInclination initializationInclination);

	ServerResponse getInitializationInclinationBySensorRegisterId(Integer sensorRegisterId);

	ServerResponse listInitializationInclination();

	ServerResponse receiveInitializationInclinationFromMQ(InitializationInclination initializationInclination);

	ServerResponse getInitLimitBySelectiveSensorRegisterId(Integer sensorRegisterId);
}
