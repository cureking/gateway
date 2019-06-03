package com.renewable.gateway.service.impl;

import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.InitializationInclinationMapper;
import com.renewable.gateway.pojo.InitializationInclination;
import com.renewable.gateway.service.IInitializationInclinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_ID;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iInitializationInclinationServiceImpl")
public class IInitializationInclinationServiceImpl implements IInitializationInclinationService {

    @Autowired
    private InitializationInclinationMapper initializationInclinationMapper;

    @Override
    public ServerResponse insert(InitializationInclination initializationInclination) {
        // 1.数据校验（部分情况下还有权限校验）
        if (initializationInclination == null){
            return ServerResponse.createByErrorMessage("the initializationInclination is null !");
        }

        // 2.initializationInclinationAssemble
        InitializationInclination insertSensorRegister = initializationInclination; // 这里简化一下

        // 3.插入数据
        int countRow = initializationInclinationMapper.insertSelective(insertSensorRegister);
        if (countRow == 0){
            return ServerResponse.createByErrorMessage("the initializationInclination insert fail !");
        }

        // 4.返回成功响应
        return ServerResponse.createBySuccessMessage("the initializationInclination insert success .");
    }

    @Override
    public ServerResponse getInitializationInclinationBySensorRegisterId(int sensorRegisterId) {
        int terminalId = Integer.parseInt(GuavaCache.getKey(TERMINAL_ID));

        InitializationInclination initializationInclination = initializationInclinationMapper.selectByTerminalIdAndSensorRegisterId(terminalId, sensorRegisterId);
        if (initializationInclination == null){
            return ServerResponse.createByErrorMessage("can't find the initializationInclination with the sensorRegisterId: "+sensorRegisterId+" and terminalId: "+terminalId);
        }

        return ServerResponse.createBySuccess(initializationInclination);
    }

    @Override
    public ServerResponse receiveInitializationInclinationFromMQ(InitializationInclination initializationInclination) {
        // 1.校验数据
        if (initializationInclination == null){
            return ServerResponse.createByErrorMessage("the initializationInclination is null !");
        }

        // 2.数据组装
        InitializationInclination insertInitializationInclination = initializationInclination;

        // 3.保存serialSensor至数据库
        ServerResponse insertSerialSensorResponse = this.insert(insertInitializationInclination);
        if (insertSerialSensorResponse.isFail()){
            return insertSerialSensorResponse;
        }

        return ServerResponse.createBySuccessMessage("the initializationInclination has inserted .");
    }
}
