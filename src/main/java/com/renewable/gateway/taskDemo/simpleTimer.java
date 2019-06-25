package com.renewable.gateway.taskDemo;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.rabbitmq.producer.InclinationProducer;
import com.renewable.gateway.extend.serial.SerialPool;
import com.renewable.gateway.extend.serial.sensor.InclinationDeal526T;
import com.renewable.gateway.service.*;
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
	private SerialPool serialPool;

	@Autowired
	private IInclinationService iInclinationService;

	@Autowired
	private InclinationProducer inclinationProducer;

	@Autowired
	private IInclinationDealTotalService iInclinationDealTotalService;

	@Autowired
	private IInclinationDealInitService iInclinationDealInitService;

	@Autowired
	private IWarningService iWarningService;

	@Autowired
	private ISerialSensorService iSerialSensorService;

	/**
	 * 用于实现轮询获取传感器监测数据
	 */
    @Scheduled(cron = "*/5 * * * * *")  //每五秒钟    //数据读取  //暂停，以便进行相关调试工作
	public void requireSerialData() {
		log.info("请求监测数据定时任务启动");

//		System.out.println("simpleTimer first task start.currentTime:" + System.currentTimeMillis());
//
//		ServerResponse<byte[]> serverResponse = InclinationDeal526T.command2Origin(00, InclinationConst.InclinationSensor1Enum.READALL);
//
//		if (!serverResponse.isSuccess()) {
//			log.error("error");
//			System.out.println("simpleTimer data transfer failure");
//		} else {
//			byte[] originArray = serverResponse.getData();
//			System.out.println(Arrays.toString(originArray));
//			serialPool.sendData("COM6", originArray);
//
//		}
//		System.out.println("simpleTimer second task end.currentTime:" + System.currentTimeMillis());

		iSerialSensorService.taskLoadFromSerialSensor();

		log.info("请求定时任务结束");
		System.out.println("请求定时任务结束");

	}


	    @Scheduled(cron = "*/20 * * * * *") // 20秒 	// 发送倾斜数据至MQ，至中控室     //暂停，以便进行相关调试工作
	public void RabbitMQAndInclinationIntegrateService() {
		System.out.println("RabbitMQAndInclinationIntegrateService start！");

		iInclinationDealTotalService.uploadDataList();
		iInclinationDealInitService.uploadDataList();

		System.out.println("RabbitMQAndInclinationIntegrateService end");
	}

	    @Scheduled(cron = "*/50 * * * * *") // 20秒	// 发送警报信息至MQ，至中控室
	public void WarningService() {
		System.out.println("RabbitMQAndInclinationIntegrateService start！");

		iWarningService.stateCheck();

		System.out.println("RabbitMQAndInclinationIntegrateService end");
	}

}
