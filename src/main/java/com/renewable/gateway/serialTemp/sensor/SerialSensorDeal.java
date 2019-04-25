package com.renewable.gateway.serialTemp.sensor;

import com.renewable.gateway.common.sensor.InclinationConst;

/**
 * @Description：
 * @Author: jarry
 */
public abstract class SerialSensorDeal {
    private void receiveBuffer(String port, int baudrate, byte[] originBuffer) {

    }

    private boolean sendBuffer(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum) {
        return false;
    }

    private boolean sendBuffer(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum, String data) {
        return false;
    }


    //todo 以后这里可以考虑专门封装一个返回类型

    /**
     * 就是专门用于接收底层（如监听器）的数据
     *
     * @param port
     * @param baudrate
     * @param originBuffer
     * @return
     */
    public boolean receiveData(String port, int baudrate, byte[] originBuffer) {
        return false;
    }

    /**
     * 用于高层发送指令
     *
     * @param port
     * @param baudrate
     * @param address
     * @param inclinationSensor1Enum
     * @return
     */
    public boolean sendData(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum) {
        return false;
    }

    public boolean sendData(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum, String data) {
        return false;
    }
}
