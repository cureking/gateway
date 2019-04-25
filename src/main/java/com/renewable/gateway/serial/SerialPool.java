package com.renewable.gateway.serial;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.exception.serial.*;
import com.renewable.gateway.util.SensorPropertiesUtil;
import com.renewable.gateway.util.SerialPortUtil;
import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@Component("SerialPool")
public class SerialPool {


    @Autowired
    private SerialListener serialListener;


    private static ConcurrentHashMap<String, SerialPort> serialPortConcurrentHashMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, SerialListener> serialListenerConcurrentHashMap = new ConcurrentHashMap<>();
    private static ArrayList<String> portList = SerialPortUtil.findPort();
    private static int initBaudrate = Integer.valueOf(SensorPropertiesUtil.getProperty("sensor.bautrate"));


    /**
     * 装填所有的serialPort
     */
    @PostConstruct
    private void serialPortAssemble() {

        //将所有串口对象serialPort置入serialPortConcurrentHashMap
        for (Iterator iter = portList.iterator(); iter.hasNext(); ) {
            String str = (String) iter.next();

            //todo 做了配置表之后，这里可以直接通过配置表 如：PropertiesUtil.getProperty(str); 获取对应波特率
            int baudarte = initBaudrate;

            //避免不必要的资源浪费（而且少了一些报错，心里舒服多了。。。）。毕竟很多这个条件成立的可能性是很高的（也许我们日后可以做一个串口增加时监听的优化）
            if (serialPortConcurrentHashMap.get(str) != null) {
                continue;
            }

            try {
                //todo 这里可以根据portName(str)，来获取对应波特率     //sensorPropertiesUtil
                SerialPort serialPort = SerialPortUtil.openPort(str, baudarte);
                serialPortConcurrentHashMap.put(str, serialPort);
                //为对应串口对象部署串口监听者    有时候在想，如果除了SerialPort和SerialListener，还有别的对象Map，那就合并一处，一个index，多个参数
                //todo （看上一条）以后可以建立对应参数信息表，包括波特率什么的，作为日后的优化
                serialListenerAssemble(serialPort);
            } catch (SerialPortParameterFailure serialPortParameterFailure) {
                serialPortParameterFailure.printStackTrace();
            } catch (NotASerialPort notASerialPort) {
                notASerialPort.printStackTrace();
            } catch (NoSuchPort noSuchPort) {
                noSuchPort.printStackTrace();
            } catch (PortInUse portInUse) {
                portInUse.printStackTrace();
            }
        }
    }

    private void serialListenerAssemble(SerialPort serialPort) {

        //监听器这里代码一定存在冗余，当初为了方便引入serialPort对象，直接将无参constructor改为了全参constructor。这部分必然有冗余，之后优化
//        SerialListener serialListener = new SerialListener();
        System.out.println("serialListenerAssemble:" + serialPort.getName());

        try {
            SerialPortUtil.addListener(serialPort, serialListener);
        } catch (TooManyListeners tooManyListeners) {
            log.warn("serialPortOutputStreamCloseFailure", tooManyListeners);
            tooManyListeners.printStackTrace();
        }
        serialListenerConcurrentHashMap.put(serialPort.getName(), serialListener);
        System.out.println("# 对应传感器监听者部署完毕：" + serialPort.getName());
    }


    public ServerResponse sendData(String portName, byte[] originBuffer) {
        System.out.println(portName + " sendData function start:" + System.currentTimeMillis());
        try {
            SerialPortUtil.sendToPort(serialPortConcurrentHashMap.get(portName), originBuffer);
            System.out.println("sendData end");
        } catch (SendDataToSerialPortFailure sendDataToSerialPortFailure) {
            sendDataToSerialPortFailure.printStackTrace();
            return ServerResponse.createByErrorMessage("sendData fail:" + sendDataToSerialPortFailure.toString());
        } catch (SerialPortOutputStreamCloseFailure serialPortOutputStreamCloseFailure) {
            serialPortOutputStreamCloseFailure.printStackTrace();
            return ServerResponse.createByErrorMessage("sendData fail:" + serialPortOutputStreamCloseFailure.toString());
        }
        System.out.println("sendData function end:" + System.currentTimeMillis());
        return ServerResponse.createBySuccessMessage("test success");
    }
}
