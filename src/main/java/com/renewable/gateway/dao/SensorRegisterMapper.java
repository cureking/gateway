package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.SensorRegister;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SensorRegisterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SensorRegister record);

    int insertSelective(SensorRegister record);

    SensorRegister selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SensorRegister record);

    int updateByPrimaryKey(SensorRegister record);

    //custom
    SensorRegister selectByPortAndAddress(@Param(value = "port") String port, @Param(value = "address") String address);

    int countSensor();

    int checkNickname(String str);

    int checkPortAndAdderss(@Param(value = "port") String port, @Param(value = "address") String address);

    SensorRegister selectByNickname(String nickname);

    List<SensorRegister> selectSensorList();

    List<SensorRegister> selectSensorListByPort(String port);

    List<String> selectSerialList();

    SensorRegister selectByPortAndBaudrate(@Param(value = "port") String port, @Param(value = "baudrate") int baudrate);
}