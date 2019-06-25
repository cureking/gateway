package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.Sensor;

import java.util.List;

public interface SensorMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Sensor record);

	int insertSelective(Sensor record);

	Sensor selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Sensor record);

	int updateByPrimaryKey(Sensor record);

	// custom
	List<Sensor> listSensor();
}