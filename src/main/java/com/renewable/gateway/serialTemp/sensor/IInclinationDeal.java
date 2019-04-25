package com.renewable.gateway.serialTemp.sensor;

import com.renewable.gateway.common.sensor.InclinationConst;

/**
 * @Description：
 * @Author: jarry
 */
public interface IInclinationDeal extends SensorDeal<InclinationConst.InclinationSensor1Enum, String> {
    //日后再做细化扩展
    public boolean receiveData(String port, int baudrate, byte[] originBuffer);

    public boolean sendData(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum);

    public boolean sendData(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum, String data);
}
