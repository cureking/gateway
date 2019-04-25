package com.renewable.gateway.datacleanTemp.temp;

/**
 * @Description：
 * @Author: jarry
 */


import com.renewable.gateway.common.Const;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RunMain {
    static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {

        System.out.println("          x = " + System.currentTimeMillis());
        DayTaskStart();
        System.out.println("          y = " + System.currentTimeMillis());
    }

    private static void DayTaskStart() {
//        Thread.currentThread().setDaemon(true);
        System.out.println("天任务启动");
        executorService.scheduleWithFixedDelay(new MyRunable_2(), 1, Const.MIN_PERIOD, TimeUnit.SECONDS);
        System.out.println("天任务启动结束");
    }

    static class MyRunable implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("     begin = " + System.currentTimeMillis() + ", name: " + Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(1);
                System.out.println("     end = " + System.currentTimeMillis() + ", name: " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class MyRunable_2 implements Runnable {
        @Override
        public void run() {
            System.out.println("     begin = " + System.currentTimeMillis() + ", name: " + Thread.currentThread().getName());
            System.out.println("     end = " + System.currentTimeMillis() + ", name: " + Thread.currentThread().getName());
        }
    }
}
