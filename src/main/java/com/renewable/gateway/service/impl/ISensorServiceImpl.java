package com.renewable.gateway.service.impl;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.SensorMapper;
import com.renewable.gateway.pojo.Sensor;
import com.renewable.gateway.service.ISensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iSensorServiceImpl")
public class ISensorServiceImpl implements ISensorService {

	@Autowired
	private SensorMapper sensorMapper;

	@Override
	public ServerResponse insertSensor(Sensor sensor) {
		if (sensor == null){
			return ServerResponse.createByErrorMessage("the sensor is null !");
		}

		Sensor sensorInsert = sensor;
		sensorInsert.setId(null);

		int countRow = sensorMapper.insertSelective(sensorInsert);
		if (countRow == 0){
			return ServerResponse.createByErrorMessage("sensor insert fail !");
		}

		return ServerResponse.createBySuccessMessage("sensor insert success .");
	}

	@Override
	public ServerResponse updateSensor(Sensor sensor) {
		if (sensor == null){
			return ServerResponse.createByErrorMessage("the sensor is null !");
		}
		if (sensor.getId() == null){
			return ServerResponse.createByErrorMessage("the id of the sensor is null !");
		}

		Sensor sensorUpdate = sensor;

		int countRow = sensorMapper.updateByPrimaryKeySelective(sensorUpdate);
		if (countRow == 0){
			return ServerResponse.createByErrorMessage("sensor update fail !");
		}

		return ServerResponse.createBySuccessMessage("sensor update success .");
	}

	@Override
	public ServerResponse getSensorById(Integer sensorId) {
		if (sensorId == null){
			return ServerResponse.createByErrorMessage("the sensorId is null !");
		}
		Sensor sensor = null;
		sensor = sensorMapper.selectByPrimaryKey(sensorId);
		if (sensor == null){
			return ServerResponse.createByErrorMessage("there is no sensor with the id: "+sensorId);
		}
		return ServerResponse.createBySuccess(sensor);
	}

	@Override
	public ServerResponse listSensor() {
		List<Sensor> sensorList = null;
		sensorList = sensorMapper.listSensor();
		if (sensorList == null || sensorList.size() == 0){
			return ServerResponse.createByErrorMessage("there is no sensor !");
		}
		return ServerResponse.createBySuccess(sensorList);
	}
}
