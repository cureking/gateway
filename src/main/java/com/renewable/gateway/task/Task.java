package com.renewable.gateway.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Description：
 * @Author: jarry
 */
@AllArgsConstructor
@Setter
@Getter
public class Task {
    private long id;
    private String triggerName;
    private String cron;
    private State state;
    private Date nextExecute;

    public enum State {
        RUN, WAITING_NEXT, STOP
    }
}
