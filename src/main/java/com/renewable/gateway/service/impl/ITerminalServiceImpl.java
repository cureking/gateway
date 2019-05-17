package com.renewable.gateway.service.impl;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.TerminalMapper;
import com.renewable.gateway.pojo.Terminal;
import com.renewable.gateway.service.ITerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iTerminalServiceImpl")
public class ITerminalServiceImpl implements ITerminalService {

    @Autowired
    private TerminalMapper terminalMapper;

    public ServerResponse listTerminal(){
        List<Terminal> terminalList = terminalMapper.listTerminal();
        if (terminalList == null){
            return ServerResponse.createByErrorMessage("can't find the terminal config on DB !");
        }
        return ServerResponse.createBySuccess(terminalList);
    }

    @Override
    public ServerResponse insertTerminal(Terminal terminal) {
        if (terminal == null){
            return ServerResponse.createByErrorMessage("the data that want to insert is null");
        }

        int countRow = terminalMapper.insertSelective(terminal);
        if (countRow == 0){
            return ServerResponse.createByErrorMessage("terminal insert fail !");
        }

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse getTerminalFromRabbitmq(Terminal terminal) {
        if (terminal == null){
            return ServerResponse.createByErrorMessage("the terminal is null !");
        }
        int countRow = terminalMapper.updateByMac(terminal);     // 根据mac，对数据库记录进行更新
        if (countRow == 0){
            return ServerResponse.createByErrorMessage("update terminal fail !");
        }
        return ServerResponse.createBySuccess("update terminal success !");
    }
}
