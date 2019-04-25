package com.renewable.gateway.common.serialPoolTemp;

import com.renewable.gateway.exception.serial.NoSuchPort;
import com.renewable.gateway.exception.serial.NotASerialPort;
import com.renewable.gateway.exception.serial.PortInUse;
import com.renewable.gateway.exception.serial.SerialPortParameterFailure;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class SerialFactory implements PooledObjectFactory<SerialPort> {

    private final AtomicReference<PortAndBaudrate> portAndBaudrate = new AtomicReference<PortAndBaudrate>();


    @Override
    public PooledObject<SerialPort> makeObject() throws Exception {
        final PortAndBaudrate portAndBaudrate = this.portAndBaudrate.get();
//        final SerialPort serialPort = SerialPortUtil.openPort(this.portAndBaudrate.get().getPort(),this.portAndBaudrate.get().getBaudrate());


        try {
            String portName = this.portAndBaudrate.get().getPort();
            int baudrate = this.portAndBaudrate.get().getBaudrate();
            //通过端口名识别端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            //打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            CommPort commPort = portIdentifier.open(portName, baudrate);

            //判断是不是串口
            if (commPort instanceof SerialPort) {

                SerialPort serialPort = (SerialPort) commPort;

                try {
                    //设置一下串口的波特率等参数
                    serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                } catch (UnsupportedCommOperationException e) {
                    throw new SerialPortParameterFailure();
                }
                //System.out.println("Open " + portName + " sucessfully !");
                return new DefaultPooledObject<SerialPort>(serialPort);
            } else {
                //不是串口
                throw new NotASerialPort();
            }
        } catch (NoSuchPortException e1) {
            throw new NoSuchPort();
        } catch (PortInUseException e2) {
            throw new PortInUse();
        }
    }

    @Override
    public void destroyObject(PooledObject<SerialPort> serialPortPooledObject) throws Exception {

        final SerialPort serialPort = serialPortPooledObject.getObject();

        if (serialPort != null) {
            //todo 这里的判断条件后面要改为 isConnected()   （好吧。我不知道串口通信的状态量即状态函数，后面查一下）
            try {
                serialPort.close();
            } catch (Exception e) {
                log.warn("关闭端口失败：", e);
            }
        } else {
            // 相关端口已经关闭
        }
    }

    @Override
    public boolean validateObject(PooledObject<SerialPort> serialPortPooledObject) {
        final SerialPort serialPort = serialPortPooledObject.getObject();
        try {
            PortAndBaudrate portAndBaudrate = this.portAndBaudrate.get();
            String connectionPort = serialPort.getName();
            int connectionBaudrate = serialPort.getBaudRate();

            return portAndBaudrate.getPort().equals(connectionPort)
                    && portAndBaudrate.getBaudrate() == connectionBaudrate
                    && (serialPort != null);
            //todo 这里的判断条件后面要改为 isConnected()   （好吧。我不知道串口通信的状态量即状态函数，后面查一下）
            //参考
//            return hostAndPort.getHost().equals(connectionHost)
//                    && hostAndPort.getPort() == connectionPort && jedis.isConnected()
//                    && jedis.ping().equals("PONG");
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<SerialPort> serialPortPooledObject) throws Exception {

        //暂时不需要
//        final SerialPort serialPort = serialPortPooledObject.getObject();
//        if (serialPort.isReceiveTimeoutEnabled())

        //参考
//        final BinaryJedis jedis = pooledJedis.getObject();
//        if (jedis.getDB() != database) {
//            jedis.select(database);
//        }

    }

    @Override
    public void passivateObject(PooledObject<SerialPort> p) throws Exception {

    }
}
