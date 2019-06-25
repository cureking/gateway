package com.renewable.gateway.Init;

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
public class GlobalInit {
	// 用于严格控制顺序，目前还没有实现多维度配置的自制维护更新，必须确保一定的顺序才可以实现安装。另外安装时刻必须保持与中控室的网络连接，实现配置的注册。
	@Autowired
	private SerialSensorInit serialSensorInit;

	@Autowired
	private TerminalInit terminalInit;

	@PostConstruct
	private void init() {

		System.out.println("GlobalInit start");

		terminalInit.init();

		serialSensorInit.init();

		System.out.println("GlobalInit end");
	}
}
