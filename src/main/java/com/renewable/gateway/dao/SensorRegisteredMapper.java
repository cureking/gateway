package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.SensorRegistered;

public interface SensorRegisteredMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SensorRegistered record);

    int insertSelective(SensorRegistered record);

    SensorRegistered selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SensorRegistered record);

    int updateByPrimaryKey(SensorRegistered record);
}