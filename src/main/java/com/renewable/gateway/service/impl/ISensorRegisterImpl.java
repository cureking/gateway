package com.renewable.gateway.service.impl;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.SensorRegisterMapper;
import com.renewable.gateway.pojo.SensorRegister;
import com.renewable.gateway.service.ISensorRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iSensorRegisterImpl")
public class ISensorRegisterImpl implements ISensorRegisterService {

    @Autowired
    private SensorRegisterMapper sensorRegisterMapper;

    @Override
    public ServerResponse insert(SensorRegister sensorRegister) {
        // 1.数据校验（部分情况下还有权限校验）
        if (sensorRegister == null){
            return ServerResponse.createByErrorMessage("the sensorRegister is null !");
        }

        // 2.serialSensorAssemble
        SensorRegister insertSensorRegister = sensorRegister; // 这里简化一下

        // 3.插入数据
        int countRow = sensorRegisterMapper.insertSelective(insertSensorRegister);
        if (countRow == 0){
            return ServerResponse.createByErrorMessage("the sensorRegister insert fail !");
        }

        // 4.返回成功响应
        return ServerResponse.createBySuccessMessage("the sensorRegister insert success .");
    }

    public ServerResponse insertOrUpdateBy(SensorRegister sensorRegister){
        // 数据校验
        if (sensorRegister == null){
            return ServerResponse.createByErrorMessage("sensorRegister is null !");
        }

        // 2.sensorRegisterAssemble
        SensorRegister insertOrUpdateSensorRegister = sensorRegister;

        // 3.插入/更新数据    // 貌似由于数据库版本问题不支ON DUPLICATE KEY UPDATE （之后细究）
        //        int countRow = sensorRegisterMapper.insertOrUpdate(sensorRegister);
//        if (countRow == 0){
//            return ServerResponse.createByErrorMessage("the sensorRegister insert/update fail !");
//        }

        SensorRegister existSensorRegister = sensorRegisterMapper.selectByPrimaryKey(sensorRegister.getId());
        if (existSensorRegister == null){
            int countRow = sensorRegisterMapper.insertSelective(sensorRegister);
            if (countRow == 0){
                return ServerResponse.createByErrorMessage("the sensorRegister insert fail !");
            }
        }else{
            // 说明数据原来就存在，那么就不需要更改create_time()
            sensorRegister.setCreateTime(null);
            int countRow = sensorRegisterMapper.updateByPrimaryKeySelective(sensorRegister);
            if (countRow == 0){
                return ServerResponse.createByErrorMessage("the sensorRegister update fail !");
            }
        }

        // 4.返回成功响应
        return ServerResponse.createBySuccessMessage("the sensorRegister insert/update success .");
    }

    @Override
    public ServerResponse receiveSensorRegisterFromMQ(SensorRegister sensorRegister) {
        // 1.校验数据
        if (sensorRegister == null){
            return ServerResponse.createByErrorMessage("the sensorRegister is null !");
        }

        // 2.数据组装
        SensorRegister insertSensorRegister = sensorRegister;

        // 3.保存serialSensor至数据库
        ServerResponse insertSerialSensorResponse = this.insertOrUpdateBy(insertSensorRegister);
        if (insertSerialSensorResponse.isFail()){
            return insertSerialSensorResponse;
        }

        return ServerResponse.createBySuccessMessage("the sensorRegister has inserted .");
    }
}
