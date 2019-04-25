package com.renewable.gateway.util;

import com.renewable.gateway.exception.serial.*;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * @Description： serial communication utils
 * @Origin zhong    https://www.cnblogs.com/Dreamer-1/p/5523046.html
 * @Author: jarry
 */
public class SerialPortUtil {

    private static SerialPortUtil serialPortUtil = null;

    //私有化SerialPortUtil类的构造方法，不允许其他类生成SerialPortUtil对象
//    private SerialPortUtil() { }

    static {
        //在该类被ClassLoader加载时就初始化一个SerialPortUtil对象
        if (serialPortUtil == null) {
            serialPortUtil = new SerialPortUtil();
        }
    }


    /**
     * 获取提供服务的SerialPortUtil对象
     *
     * @return serialPortUtil
     */
    public static SerialPortUtil getSerialPortUtil() {
        if (serialPortUtil == null) {
            serialPortUtil = new SerialPortUtil();
        }
        //todo 并没有解决双重检查的并发问题
        return serialPortUtil;
    }

    /**
     * 查找所有可用端口
     *
     * @return 可用端口名称列表
     */
    public static final ArrayList<String> findPort() {

        //获得当前所有可用串口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portNameList = new ArrayList<>();

        //将可用串口名添加到List并返回该List
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            portNameList.add(portName);
        }
        return portNameList;
    }

    /**
     * 打开串口
     *
     * @param portName 端口名称
     * @param baudrate 波特率
     * @return 串口对象
     * @throws SerialPortParameterFailure 设置串口参数失败
     * @throws NotASerialPort             端口指向设备不是串口类型
     * @throws NoSuchPort                 没有该端口对应的串口设备
     * @throws PortInUse                  端口已被占用
     */
    //这里先添加synchronized 处理并发，否则SensorPool中并发调用时，会产生访问错误。
    public static final synchronized SerialPort openPort(String portName, int baudrate) throws SerialPortParameterFailure, NotASerialPort, NoSuchPort, PortInUse {

        try {
            //通过端口名识别端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            //打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            CommPort commPort = portIdentifier.open(portName, 2000);
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
                return serialPort;
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

    /**
     * 关闭串口
     *
     * @param serialPort 待关闭的串口对象
     */
    public static void closePort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }

    /**
     * 往串口发送数据
     *
     * @param serialPort 串口对象
     * @param order      待发送数据
     * @throws SendDataToSerialPortFailure        向串口发送数据失败
     * @throws SerialPortOutputStreamCloseFailure 关闭串口对象的输出流出错
     */
    public static void sendToPort(SerialPort serialPort, byte[] order) throws SendDataToSerialPortFailure, SerialPortOutputStreamCloseFailure {
        OutputStream out = null;

        try {
            out = serialPort.getOutputStream();
            out.write(order);
            out.flush();
        } catch (IOException e) {
            throw new SendDataToSerialPortFailure();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                throw new SerialPortOutputStreamCloseFailure();
            }
        }
    }

    /**
     * 从串口读取数据
     *
     * @param serialPort 当前已建立连接的SerialPort对象
     * @return 读取到的数据
     * @throws ReadDataFromSerialPortFailure     从串口读取数据时出错
     * @throws SerialPortInputStreamCloseFailure 关闭串口对象输入流出错
     */
    public static byte[] readFromPort(SerialPort serialPort) {

        InputStream in = null;
        byte[] bytes = null;

        try {
            //todo 这里和javaclient的存在不同，可以相互借鉴
            in = serialPort.getInputStream();
            int bufflenth = in.available();        //获取buffer里的数据长度

            while (bufflenth != 0) {
                bytes = new byte[bufflenth];    //初始化byte数组为buffer中数据的长度
                in.read(bytes);
                bufflenth = in.available();
            }
        } catch (IOException e) {
            new ReadDataFromSerialPortFailure();
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException e) {
                new SerialPortInputStreamCloseFailure();
            }
        }
        return bytes;
    }

    /**
     * 添加监听器
     *
     * @param port     串口对象
     * @param listener 串口监听器
     * @throws TooManyListeners 监听类对象过多
     */
    public static void addListener(SerialPort port, SerialPortEventListener listener) throws TooManyListeners {

        try {
            //给串口添加监听器
            port.addEventListener(listener);
            //设置当有数据到达时唤醒监听接收线程
            port.notifyOnDataAvailable(true);
            //设置当通信中断时唤醒中断线程
            port.notifyOnBreakInterrupt(true);
        } catch (TooManyListenersException e) {
            throw new TooManyListeners();
        }
    }


    public static void main(String[] args) throws Exception {
//        SerialPortUtil tu = new SerialPortUtil();
//        tu.test_2();
//        test_1();
        test_2();
    }

    public static void test_2() {
        SerialPortUtil serialPortUtil = new SerialPortUtil();
        System.out.println(serialPortUtil.findPort());
    }

    /**
     * test_1
     */
    public static void test_1() {
        System.out.println("# serial communication start");

        SerialPortUtil serialPortUtil = new SerialPortUtil();
        System.out.println(serialPortUtil.findPort());
        SerialPort serialPort_1 = null;
        // todo 之后通过线程池的方式
        try {
            serialPort_1 = serialPortUtil.openPort("COM2", 9600);
            System.out.println(serialPort_1);

            //为了获取数据，这里线程暂停一下推进过程
            Thread.currentThread().sleep(2000);
            //接收数据
            byte[] serialArray = readFromPort(serialPort_1);
            //发送数据
            sendToPort(serialPort_1, serialArray);

            System.out.println("# data show");
            System.out.println(Arrays.toString(serialArray));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            serialPortUtil.closePort(serialPort_1);
        }
        System.out.println("# serial communication end");
    }


}
