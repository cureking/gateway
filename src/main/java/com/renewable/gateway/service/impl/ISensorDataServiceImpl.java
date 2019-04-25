package com.renewable.gateway.service.impl;

import com.github.pagehelper.PageInfo;
import com.renewable.gateway.common.Const;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.dao.SensorRegisterMapper;
import com.renewable.gateway.pojo.SensorRegister;
import com.renewable.gateway.service.IInclinationService;
import com.renewable.gateway.service.ISensorDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//import com.renewable.gateway.serial.SerialPoolDemo;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iSensorDataService")
@Slf4j
public class ISensorDataServiceImpl implements ISensorDataService {

    @Autowired
    private SensorRegisterMapper sensorRegisterMapper;

//    @Autowired
//    private SerialPoolDemo serialPoolDemo;


    //经过考虑，这里的服务进行拆分，将不同传感器的数据获取方式放出去。便于管理，代码可读性以及以后的扩展
    @Autowired
    private IInclinationService iInclinationService;


    @Override
    public ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier) {

        switch (sensor_identifier) {
            case Const.SensorType.Inclination1:
                return iInclinationService.getInclinationDataList(pageNum, pageSize);

            case Const.SensorType.Inclination2:
                return null;    //还没有确定
            default:
                return ServerResponse.createByErrorMessage("can't find the sensor of this sensor_type_identifier:" + sensor_identifier);
        }

//        if (sensor_id == Const.SensorEnum.valueOf("Inclination1").getCode()){
//            //如果日后同类型传感器数据统一。可以进行范围圈定
//            return getInclinationDataList(pageNum,pageSize);
//        }else {
//            return ServerResponse.createByErrorMessage("can't find the sensor of this sensor_type_id:"+sensor_id);
//        }

    }


    @Override

    // TODO: 3/18/2019  需要根据数据的跨度，确定数据的跨度，降低数据的获取量，保证性能
    public ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_id) {
        if (sensor_id == Const.SensorEnum.valueOf("Inclination1").getCode()) {
            //如果日后同类型传感器数据统一。可以进行范围圈定
            return iInclinationService.getInclinationDataListTime(startTime, endTime);
        } else {
            return ServerResponse.createByErrorMessage("can't find the sensor of this sensor_type_id:" + sensor_id);
        }
    }

    @Override
    public void sendData(int sensor_id, String port, String address, String baudrate) {


        if (sensor_id == Const.SensorEnum.valueOf("Inclination1").getCode()) {
            //如果日后同类型传感器数据统一。可以进行范围圈定
            System.out.println("ISensorDataServiceImpl/sendData");
            iInclinationService.sendDataInclination(port, baudrate, InclinationConst.InclinationSensor1Enum.READALL);
        } else {
            log.warn("no such sensor to sendData!");
        }
    }

    @Override
    public ServerResponse cleanDataTasks() {
        List<SensorRegister> sensorRegisterList = sensorRegisterMapper.selectSensorList();
        if (sensorRegisterList == null) {
            return ServerResponse.createByErrorMessage("the sensor registry on MySql is null");
        }
        for (SensorRegister sensorRegister : sensorRegisterList) {
            System.out.println("ISensorDataServiceImpl/cleanDataTasks:sensorRegister:"+sensorRegister.toString());
            cleanDataTaskDispatch(sensorRegister);
        }
        return ServerResponse.createBySuccessMessage("数据清洗成功");
    }

    private ServerResponse cleanDataTaskDispatch(SensorRegister sensorRegister) {
        if (sensorRegister.getType() == Const.SensorEnum.Inclination1.getCode()) {
            return iInclinationService.cleanDataTask(sensorRegister);
        } else {
            return ServerResponse.createByErrorMessage("当前传感器暂未收录至开发配置表");
        }

    }

}
