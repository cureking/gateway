package com.renewable.gateway.Init;

import com.renewable.gateway.rabbitmq.producer.TerminalProducer;
import com.renewable.gateway.service.ISerialSensorService;
import com.renewable.gateway.service.ITerminalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description：
 * @Author: jarry
 */
@Component
@Slf4j
public class SerialSensorInit {

    @Autowired
    private ISerialSensorService iSerialSensorService;

    @PostConstruct
    private void init() {

        System.out.println("SerialSensorInit start");

        iSerialSensorService.refresh();     // 这里的serialSensor初始化，会带动关联的sensorRegister与initializationInclination等初始化。   （终于理清楚这里面应有的逻辑顺序了，逻辑实体建立于物理实体上）

        System.out.println("SerialSensorInit end");
    }

}
