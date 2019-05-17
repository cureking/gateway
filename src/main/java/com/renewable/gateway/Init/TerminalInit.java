package com.renewable.gateway.Init;

import com.renewable.gateway.common.Const;
import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.constant.CacheConstant;
import com.renewable.gateway.pojo.Terminal;
import com.renewable.gateway.rabbitmq.producer.TerminalProducer;
import com.renewable.gateway.service.ITerminalService;
import com.renewable.gateway.util.NetIndentificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.CacheConstant.*;
import static java.lang.Math.random;

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
    private void init(){
        System.out.println("TerminalInit start");

        // 1.判断数据库的terminal是否有数据
        Terminal updateterminal = new Terminal();

        ServerResponse<List<Terminal>> listResponse = this.listTerminal();
        if (listResponse.getData().size() == 0){     // 这里暂时不考虑有复数个数据的解决（即数据表由于意外情况插入多个TerminalConfig记录（话说，这个逻辑上有可能吗））
            //说明DB中没有terminalConfig
            // 2.如果没有数据，则生成一个初始化数据表记录（id为一个11位的int类型的随机数）。并将该数据插入到数据库中
            Terminal insertTerminal = this.terminalConfigGenerator();
            iTerminalService.insertTerminal(insertTerminal);

            updateterminal = insertTerminal;
        }
        if (listResponse.getData().size() != 0){
            updateterminal = listResponse.getData().get(0);
        }

        // 3.将terminal配置数据插入到GuavaCache缓存中，便于随时调用   // TerminalConfig的数据既可以从上面获取，也可以从数据库获取，这里先从数据库获取吧（这样也便于检查之前的逻辑）。
//        listResponse = this.listTerminal(); // 这里调用的是数据库数据的缓存
//        Terminal updateterminal = listResponse.getData().get(0);    // 还是那句话，这里不考虑复数情况，那种可能性在目前系统的逻辑中，貌似是没有可能的。

        this.setCacheByTerminalConfig(updateterminal);

        // 4.无论termianl配置数据的ID是否是随机数，都将配置数据放入MQ （这应该是一个public方法，便于随时进行数据更新）（centControl得到数据后，会根据数据的ID，判断是新生成数据记录（直接放入对应MQ，返回，进行更新），还是查询已存在数据（判断create_time，从而决定是否推送数据，进行更新）。
        this.uploadTerminalConfig(updateterminal);

        // 5.（别忘了，MQ那里需要写一个Consumer，来更新配置数据
        System.out.println("TerminalInit end");
    }

    // 1.
    private ServerResponse<List<Terminal>> listTerminal(){
        return iTerminalService.listTerminal();
    }

    // 2.生成随机11位ID的TerminalConfig
    private Terminal terminalConfigGenerator(){
        Terminal terminal = new Terminal();

        int randowId = (int)(Math.random()*Math.pow(10,7)*9+Math.pow(10,7));
        int projectId = 1;
        String ip = NetIndentificationUtil.getLocalIP();
        String mac = NetIndentificationUtil.getLocalMac();
        String name = "no namede";
        int state = Const.TerminalStateEnum.Running.getCode();

        terminal.setId(randowId);
        terminal.setProjectId(projectId);
        terminal.setIp(ip);
        terminal.setMac(mac);
        terminal.setName(name);
        terminal.setState(state);
        terminal.setCreateTime(new Date());
        terminal.setUpdateTime(new Date());

        return terminal;
    }

    private void setCacheByTerminalConfig(Terminal terminal){
        GuavaCache.setKey(TERMINAL_ID, terminal.getId().toString());
        System.out.println(terminal.getProjectId());
        GuavaCache.setKey(TERMINAL_PROJECT_ID, terminal.getProjectId().toString());
        GuavaCache.setKey(TERMINAL_IP, terminal.getIp());
        GuavaCache.setKey(TERMINAL_MAC, terminal.getMac());
        GuavaCache.setKey(TERMINAL_NAME, terminal.getName());
        GuavaCache.setKey(TERMINAL_STATE, terminal.getState().toString());
        GuavaCache.setKey(TERMINAL_CREATE_TIME, terminal.getCreateTime().toString());
        GuavaCache.setKey(TERMINAL_UPDATE_TIME, terminal.getUpdateTime().toString());
    }

    public void uploadTerminalConfig(Terminal terminal){
        System.out.println("upload terminalConfig start !");
        try {
            terminalProducer.sendTerminalConfig(terminal);
        } catch (IOException e) {
            log.error("terminal config upload fail ! "+e);
        } catch (TimeoutException e) {
            log.error("terminal config upload fail ! "+e);
        } catch (InterruptedException e) {
            log.error("terminal config upload fail ! "+e);
        }
    }
}
