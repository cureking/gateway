package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.Terminal;

/**
 * @Description：
 * @Author: jarry
 */
public interface ITerminalService {

	ServerResponse listTerminal();

	ServerResponse insertTerminal(Terminal terminal);

	ServerResponse receiveTerminalFromRabbitmq(Terminal terminal);

	ServerResponse refreshCacheFromDB();    // 目测这个没有过高的价值，可以先暂时放着，之后进行清理

	ServerResponse refreshConfigFromCent();
}
