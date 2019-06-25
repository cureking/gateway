package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.SensorRegister;

import java.util.List;

public interface SensorRegisterMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(SensorRegister record);

	int insertSelective(SensorRegister record);

	SensorRegister selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(SensorRegister record);

	int updateByPrimaryKey(SensorRegister record);

	// custom
	int insertOrUpdate(SensorRegister sensorRegister);

	List<SensorRegister> listSensorRegister();
}