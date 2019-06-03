package com.renewable.gateway.Init;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.rabbitmq.producer.TerminalProducer;
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
public class TerminalInit {

    @Autowired
    private ITerminalService iTerminalService;

    public ServerResponse init() {

        System.out.println("TerminalInit start");

        ServerResponse terminalInitResponse =  iTerminalService.refreshConfigFromCent();    // 这里的serialSensor初始化，会带动关联的sensorRegister与initializationInclination等初始化。   （终于理清楚这里面应有的逻辑顺序了，逻辑实体建立于物理实体上）
        if (terminalInitResponse.isFail()){
            return terminalInitResponse;
        }

        System.out.println("TerminalInit end");
        return ServerResponse.createBySuccessMessage("TerminalInit end");

    }

}
