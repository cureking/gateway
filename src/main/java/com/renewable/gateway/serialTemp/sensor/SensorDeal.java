package com.renewable.gateway.serialTemp.sensor;

/**
 * @Description：
 * @Author: jarry
 */
public interface SensorDeal<C, T> {

    public boolean receiveData(String port, int baudrate, byte[] originBuffer);

    public boolean sendData(String port, int baudrate, int address, C inclinationSensor1Enum);

    public boolean sendData(String port, int baudrate, int address, C inclinationSensor1Enum, T data);

}
