package com.renewable.gateway.common.serialPoolTemp;

import gnu.io.SerialPort;

import java.io.Closeable;

/**
 * @Description：
 * @Author: jarry
 */
public interface ISerialConnection extends Closeable {
    //连接池初始化
    public void init();

    //获取连接（从连接池获取连接）
    public SerialPort getSerialPort();

    //关闭连接（将连接归还连接池）
    public void close(SerialPort serialPort);
}
