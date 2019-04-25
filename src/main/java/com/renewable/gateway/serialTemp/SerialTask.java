package com.renewable.gateway.serialTemp;

import com.renewable.gateway.exception.serial.SendDataToSerialPortFailure;
import com.renewable.gateway.exception.serial.SerialPortOutputStreamCloseFailure;
import com.renewable.gateway.exception.serial.TooManyListeners;
import com.renewable.gateway.util.SerialPortUtil;
import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class SerialTask implements Callable<String>, Comparable<SerialTask> {

    private SerialPort serialPort;
    private SerialListener serialListener;
    private byte[] originData;
    private int priority;

    public SerialTask(SerialPort serialPort, SerialListener serialListener, int priority, byte[] originData) {
        this.serialPort = serialPort;
        this.serialListener = serialListener;
        this.priority = priority;
        this.originData = originData;
    }


    @Override
    public String call() throws Exception {
        //todo 工作流程尚未写完。再发送数据后，需要监听获取数据。好消息是串口的问询已经通过dispatch实现。现在就需要完成回复数据的接收问题。
        //执行发送数据，执行监听，（问题）监听数据的获取与部署，（返回，从而确保下一个程序可以很好的执行）
//        System.out.println("test_node_7:SerialTask/sendData:"+serialPort.getName());
        sendData(serialPort, originData);

        //todo_finished 3.7 回复数据的接收。在这儿接收，还是在上层接收呢？怎么接收呢？这两个会是明天的重点问题。
        //目前的打算，是将数据发往上层的数据处理core，由那里进行进行数据的处理，状态管理等（这样可以直接复用之前模块的部分代码）
        //由于之前我已经建立了监听器列表。以现在的需求，这里不需要做任何处理，直接从Listener那里获取数据，再直接发往sensorDeal

        return null;
    }

    @Override
    public int compareTo(SerialTask o) {
        if (this.priority > o.priority)//score是private的，为什么能够直接调用,这是因为在SerialTask类内部
            //大于号时返回-1，表示从大到小排列（优先级当然从大到小排列喽（这里不涉及linux的priority与rt-priority.etc哈））
            return -1;
        return 1;
    }

    private void sendData(SerialPort serialPort, byte[] originData) {

        System.out.println("# serial communication sendData start" + serialPort);

        try {
            SerialPortUtil.sendToPort(serialPort, originData);
        } catch (SendDataToSerialPortFailure sendDataToSerialPortFailure) {
            log.warn("sendDataToSerialPortFailure", sendDataToSerialPortFailure);
            sendDataToSerialPortFailure.printStackTrace();
        } catch (SerialPortOutputStreamCloseFailure serialPortOutputStreamCloseFailure) {
            log.warn("serialPortOutputStreamCloseFailure", serialPortOutputStreamCloseFailure);
            serialPortOutputStreamCloseFailure.printStackTrace();
        }

        System.out.println("# serial communication sendData end");
    }

    private void receiveData(SerialPort serialPort) {

        System.out.println("# serial communication receive start");

        try {
            SerialPortUtil.addListener(serialPort, new SerialListener(serialPort));
        } catch (TooManyListeners tooManyListeners) {
            log.warn("serialPortOutputStreamCloseFailure", tooManyListeners);
            tooManyListeners.printStackTrace();
        }

        System.out.println("# serial communication end");
    }


}
