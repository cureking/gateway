package com.renewable.gateway.serial;

import com.renewable.gateway.common.Const;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.service.IInclinationService;
import com.renewable.gateway.util.SerialPortUtil;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@Component("SerialListener")
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
        //引入spring配置。
class SerialListener implements SerialPortEventListener {


    @Autowired
    private IInclinationService iInclinationService;

    private SerialPort serialPort;


    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        serialPort = (SerialPort) serialPortEvent.getSource();
        System.out.println(serialPort.getName());

        switch (serialPortEvent.getEventType()) {
            case SerialPortEvent.BI: // 10 通讯中断
                System.out.println("通讯中断");
                break;

            case SerialPortEvent.OE: // 7 溢位（溢出）错误

            case SerialPortEvent.FE: // 9 帧错误

            case SerialPortEvent.PE: // 8 奇偶校验错误

            case SerialPortEvent.CD: // 6 载波检测

            case SerialPortEvent.CTS: // 3 清除待发送数据

            case SerialPortEvent.DSR: // 4 待发送数据准备好了

            case SerialPortEvent.RI: // 5 振铃指示

            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
                break;

            case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据

                byte[] data = null;
                try {
                    if (serialPort == null) {
                        log.error("串口对象为空，监听失败");
                    } else {
                        Thread.currentThread().sleep(50);   //其实20ms一般就可以确保数据接收完毕，但为了安全起见。之后这里可以增加一个方法，通过校验和来确定数据是否接收完毕，否则再等待5ms。（20ms意味着接受频率不可超过50hz）
                        data = SerialPortUtil.readFromPort(serialPort);


                        //todo 这里可以提取一个模块，用于做数据的沾包，分包处理
                        if (data == null) {
                            break;
                        }
                        //todo 3.7数据处理


                        //数据的处理，传感器策略选择
                        switch (data[0]) {
                            case Const.SensorType.Inclination1:
                                //将数据放入对应消息队列
                                //考虑参照RedisPool写一个相关的池
                                //2.调用目标方法，如QueuePush等，将消息置入
                                //3.正如之前研究的，这是一个稳定数据流，如果负载不足，那么即使采用了队列，也不可以。
                                //3+.故可以直接调用目标防止执行数处理。至于对应传感器中数据清洗采用何种技术，与这里没有关系
                                //业务代码需要将串口号传输过去，避免信息的丢失

                                System.out.println("已经选择策略-倾斜传感器1,data:" + Arrays.toString(data) + "    当前系统时间戳：" + System.currentTimeMillis());
                                ServerResponse response = iInclinationService.receiveData(serialPort.getName(), serialPort.getBaudRate(), data);
                                System.out.println(response);


                                break;
                            case Const.SensorType.Inclination2:
                                //将数据放入对应消息队列
                                //考虑参照RedisPool写一个相关的池
                                //2.调用目标方法，如QueuePush等，将消息置入
                                //3.正如之前研究的，这是一个稳定数据流，如果负载不足，那么即使采用了队列，也不可以。
                                //3+.故可以直接调用目标防止执行数处理。至于对应传感器中数据清洗采用何种技术，与这里没有关系
                                //业务代码需要将串口号传输过去，避免信息的丢失


                                //test
//                                System.out.println("已经选择策略-倾斜传感器2,data:" + Arrays.toString(data));
                                //todo_finished 这里数组元素在值大于等于0x80时，会转为负数，注意处理，可以编写一个Array的工具类
                                break;
                            default:
                                log.error("无此传感器标识符，请添加配置，或检查硬件设备", data);
                                //test
//                                System.out.println("无对应传感器,data:" + Arrays.toString(data));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}