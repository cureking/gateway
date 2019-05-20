package com.renewable.gateway.taskDemo;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.rabbitmq.producer.InclinationProducer;
import com.renewable.gateway.serial.SerialPool;
import com.renewable.gateway.serial.sensor.InclinationDeal526T;
import com.renewable.gateway.service.IInclinationDealInitService;
import com.renewable.gateway.service.IInclinationDealTotalService;
import com.renewable.gateway.service.IInclinationService;
import com.renewable.gateway.service.ISensorDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @Description：
 * @Author: jarry
 */
@Component
@Slf4j
public class simpleTimer {

    @Autowired
    private ISensorDataService iSensorDataService;

    @Autowired
    private SerialPool serialPool;

    @Autowired
    private IInclinationService iInclinationService;

    @Autowired
    private InclinationProducer inclinationProducer;

    @Autowired
    private IInclinationDealTotalService iInclinationDealTotalService;

    @Autowired
    private IInclinationDealInitService iInclinationDealInitService;

    /**
     * 用于实现轮询获取传感器监测数据
     */
//    @Scheduled(cron = "*/5 * * * * *")  //每五秒钟    //数据读取  //暂停，以便进行Terminal的调试工作
    public void requireSerialData() {
        log.info("请求监测数据定时任务启动");

        System.out.println("simpleTimer first task start.currentTime:" + System.currentTimeMillis());
//        byte[] test = {1, 2, 3, 4};
//        SerialPool.sendData("COM1",test);
//        serialPool.sendData("COM2",test);
//        SerialPool.sendData("COM3",test);


        ServerResponse<byte[]> serverResponse = InclinationDeal526T.command2Origin(00, InclinationConst.InclinationSensor1Enum.READALL);
        if (!serverResponse.isSuccess()) {
            log.error("error");
            System.out.println("simpleTimer data transfer failure");
        } else {
            byte[] originArray = serverResponse.getData();
            System.out.println(Arrays.toString(originArray));
//            serialPool.sendData("COM4", originArray);
            //TODO 这里应该读取缓存，获得目标的地址。
            serialPool.sendData("COM6", originArray);
//            serialPool.sendData("COM6",originArray);
        }
        System.out.println("simpleTimer second task end.currentTime:" + System.currentTimeMillis());

        log.info("请求定时任务结束");
        System.out.println("请求定时任务结束");

    }


    @Scheduled(cron = "*/20 * * * * *") // 1分钟      //暂停，以便进行Terminal的调试工作
    public void testRabbitMQIntegrateService() {
        System.out.println("rabbitMq test start！");

//        iInclinationDealTotalService.uploadDataList();
        iInclinationDealInitService.uploadDataList();

        System.out.println("rabbitMq test end");
    }
}
