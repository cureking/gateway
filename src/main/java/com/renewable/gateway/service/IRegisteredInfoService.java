package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.SensorRegister;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */

@Deprecated
public interface IRegisteredInfoService {

    //对外开放
    //获取串口数量
    @Deprecated
    ServerResponse<Integer> getSerialCount();

    //获取串口列表
    @Deprecated
    ServerResponse<List<String>> getSerialList();

    //获取所有传感器数量
    @Deprecated
    ServerResponse<Integer> getSensorCount();

    //获取所有传感器列表       //可以使用pageHelper
    @Deprecated
    ServerResponse<List<SensorRegister>> getSensorList();

    //获取某串口的所有传感器
    @Deprecated
    ServerResponse<List<SensorRegister>> getSensorListByPort(String port);

    //获取特定串口，特定地址的传感器
    @Deprecated
    ServerResponse<SensorRegister> getSensor(String port, String address);

    //获取特定ID的传感器
    @Deprecated
    ServerResponse<SensorRegister> getSensor(int id);

    //特定传感器设置昵称（昵称要保证唯一性）
    @Deprecated
    ServerResponse setNickname(int id, String nickname);

    //通过昵称获取ID
    @Deprecated
    ServerResponse getSensorId(String nickname);


    //数据库配置表设置    //选择性更新配置表
    @Deprecated
    ServerResponse updateSensor(SensorRegister sensorRegister);

    //对内开放
    @Deprecated
    ServerResponse insertSensor(SensorRegister sensorRegister);

    @Deprecated
    ServerResponse deleteSensor(int id);

    //TODO 接下来解决 传感器配置表初始化问题，（注意物理表与逻辑表的关系）

}
