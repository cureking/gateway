package com.renewable.gateway.Init;

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

    @Autowired
    private TerminalProducer terminalProducer;

    @PostConstruct
    private void init() {

        System.out.println("TerminalInit start");

        iTerminalService.refreshConfigFromCent();

        System.out.println("TerminalInit end");
    }

}
