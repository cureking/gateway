package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.TerminalMapper;
import com.renewable.gateway.pojo.Terminal;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description：
 * @Author: jarry
 */
public interface ITerminalService {

    ServerResponse listTerminal();

    ServerResponse insertTerminal(Terminal terminal);

    ServerResponse getTerminalFromRabbitmq(Terminal terminal);
}
