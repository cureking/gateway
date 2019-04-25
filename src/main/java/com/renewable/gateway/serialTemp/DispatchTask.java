package com.renewable.gateway.serialTemp;

import java.util.concurrent.Callable;

import static com.renewable.gateway.serialTemp.SerialPool.serialPortExcute;

/**
 * @Description：
 * @Author: jarry
 */
public class DispatchTask implements Callable<String> {

    String port;

    DispatchTask(String port) {
        this.port = port;
    }

    @Override
    public String call() throws Exception {
        serialPortExcute(port);
        return "DispatchTask:OK";
    }
}
