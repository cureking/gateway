//package com.renewable.gateway.temp;
//
//import com.renewable.gateway.common.Const;
//import com.renewable.gateway.util.PropertiesUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Description：
// * @Author: jarry
// */
//@Slf4j
///**
// * 该部分处理属于所有传感器数据处理中心池。数据接入的是对象化的数据数组
// */
//public class CleanDataTask {
//
//    //忘了说，并发需要解决 所有static class variable ，如executorService与cleanInterval；可以考虑放在初始化中
//    //采用ScheduledExecutorService，一方面在不是用SpringSchedule等框架前提下，ScheduledExecutorService可以避免Timer异常抛出，延时等问题，
//    // 另一方面，扩展性强，便于日后优化，扩展，维护等。
//    private static ScheduledExecutorService executorService;
//
//    //扰动函数系数
//    private static double disturbRatio = 0.1;
//
//
//    //cleanInterval Task
//    private static String cleanInterval = PropertiesUtil.getProperty("sensor.interval");
//
//    private static void init() {
//        //默认启动分钟任务
//        //避免手动启动，后面可以考虑框架支持
//        MinTaskStart();
//    }
//
//    private static void poolInit() {
//        //单独提出来，完全是为了之后便于修改池，即池配置
//        //todo !!!之后为了应对多个传感器，将之改为并发池，一定要处理线程结束与启动的问题。当前是直接关闭线程池来解决的，会停止一切线程池中任务。切记切记。
//        //todo !!于此同时，还需要处理线程池switchCleanType()中获取属性的硬编码，要修改为针对不同传感器，获取不同传感器数据。
//        executorService = Executors.newSingleThreadScheduledExecutor();
//        //这里先采用newSingleThreadScheduledExecutor()的单线程任务池，之后可以修改为newScheduleThreadPool()多线程任务池。注意处理多线程的mutex问题
//        //ScheduledExecutorService executorService =  Executors.newScheduledThreadPool(2);
//    }
//
//    static class MyRunable_Min implements Runnable {
//        @Override
//        public void run() {
//            System.out.println("Min_run");
//            //数据清洗模块代码，嗯，简称业务代码
//            //可以选择不同的数据清洗方式，将数据处理方式提取出来作为不同的数据清洗子模块
//
//
//            SwitchCleanType("MIN");
//        }
//    }
//
//    static class MyRunable_Hour implements Runnable {
//        @Override
//        public void run() {
//            System.out.println("Hour_run");
//            //数据清洗模块代码，嗯，简称业务代码
//
//
//            SwitchCleanType("HOUR");
//        }
//    }
//
//    static class MyRunable_Day implements Runnable {
//        @Override
//        public void run() {
//            System.out.println("Day_run");
//            //数据清洗模块代码，嗯，简称业务代码
//
//
//            SwitchCleanType("DAY");
//        }
//    }
//
//    private static void MinTaskStart() {
//        poolInit();
//        System.out.println("分钟任务启动");
//        //扰乱函数，生成扰乱值，避免任务在时间上的集中（该方法简单，日后可从队列以及并发调优的角度进行优化
//        //注意double类型转int类型时，会丢失所有小数部分的精度，只保留整数部分（不做四舍五入等操作）。避免扰动过大（其实，扰动的结果只体现在初始延迟，以及之后的周期数据的产生时间）
//        int disturb_delay = (int) (Const.MIN_PERIOD * (1 - Math.random() * disturbRatio));
//        log.info("MinTask is starting!  扰动延迟disturb=" + disturb_delay);
//        executorService.scheduleWithFixedDelay(new MyRunable_Min(), disturb_delay, Const.MIN_PERIOD, TimeUnit.SECONDS);
//        log.info("MinTask is started!");
//        System.out.println("分钟任务启动结束");
//    }
//
//    private static void HourTaskStart() {
//        poolInit();
//        //这里先采用newSingleThreadScheduledExecutor()的单线程任务池，之后可以修改为newScheduleThreadPool()多线程任务池。注意处理多线程的mutex问题
//        System.out.println("小时任务启动");
//        int disturb_delay = (int) (Const.HOUR_PERIOD * (1 - Math.random() * disturbRatio));
//        log.info("HourTask is started!  扰动延迟disturb=" + disturb_delay);
//        executorService.scheduleWithFixedDelay(new MyRunable_Hour(), disturb_delay, Const.HOUR_PERIOD, TimeUnit.SECONDS);
//        log.info("HourTask is started!");
//        System.out.println("小时任务启动结束");
//    }
//
//    private static void DayTaskStart() {
//        poolInit();
//        //这里先采用newSingleThreadScheduledExecutor()的单线程任务池，之后可以修改为newScheduleThreadPool()多线程任务池。注意处理多线程的mutex问题
//        System.out.println("天任务启动");
//        int disturb_delay = (int) (Const.DAY_PERIOD * (1 - Math.random() * disturbRatio));
//        log.info("DayTask is started!  扰动延迟disturb=" + disturb_delay);
//        executorService.scheduleWithFixedDelay(new MyRunable_Day(), disturb_delay, Const.DAY_PERIOD, TimeUnit.SECONDS);
//        log.info("DayTask is started!");
//        System.out.println("天任务启动结束");
//    }
//
//    //其实定时器，或者说数据清洗方式的切换，完全可以通过远程调用。但考虑到复杂的网络情况
//    //最终采取数据库与本地文件双保险。数据库与本地文件通过hashcode来检查区别。
//    //其实后期也可以在为本地配置文件专配一个定时器，用于检查其动态变化，再通知每个相关的任务。但这儿也需要处理任务注册。
//    //todo 也许可以从Spring框架获取一定的支持？ 或者建立一个。    当然，以目前的需求，是没有什么必要的。
//    private static void SwitchCleanType(String currType) {
//        cleanInterval = PropertiesUtil.getProperty("sensor.interval");
//        if (!StringUtils.equals(cleanInterval, currType)) {
//            //这里也可以把相关编码提取到CONST中，便于解耦
//            System.out.println(currType + "定时器 切换定时器");
//
//            executorService.shutdown();
//            //线程时间规则的切换，通过结束当前线程来完成。因为之后线程池会使用并发
//
//
//            log.info(currType + "timer is shutdown.switching to " + cleanInterval + " timer!");
//            switch (cleanInterval) {
//                case "MIN":
//                    MinTaskStart();
//                    System.out.println("switch to MIN");
//                    break;
//                case "HOUR":
//                    HourTaskStart();
//                    System.out.println("switch to HOUR");
//                    break;
//                case "DAY":
//                    DayTaskStart();
//                    System.out.println("switch to DAY");
//                    break;
//                default:
//                    System.out.println("尚未设置相关模块，请及时更新软件");
//                    break;
//            }
//            log.info("switch to " + cleanInterval + " timer done!");
//        }
//
//    }
//
//    //cleanInterval Application
//
//    //test
//    public static void main(String[] args) {
//        init();
//    }
//
//    //数据清洗模块单独拎出来
//
//}
