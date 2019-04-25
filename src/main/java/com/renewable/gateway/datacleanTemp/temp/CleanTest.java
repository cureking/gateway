package com.renewable.gateway.datacleanTemp.temp;

import com.renewable.gateway.common.Const;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.util.PropertiesUtil;
import com.renewable.gateway.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
/**
 * 该部分处理属于所有传感器数据处理中心池。数据接入的是对象化的数据数组
 */
public class CleanTest {

    //忘了说，并发需要解决 所有static class variable ，如executorService与cleanInterval；可以考虑放在初始化中

    //采用ScheduledExecutorService，一方面在不是用SpringSchedule等框架前提下，ScheduledExecutorService可以避免Timer异常抛出，延时等问题，
    // 另一方面，扩展性强，便于日后优化，扩展，维护等。
    private static ScheduledExecutorService executorService;

    //扰动函数系数
    private static double disturbRatio = 0.1;


    //cleanInterval Task
    private static String cleanInterval = PropertiesUtil.getProperty("sensor.interval");

    private static void init() {
        //默认启动分钟任务
        //避免手动启动，后面可以考虑框架支持
        MinTaskStart();
    }

    private static void poolInit() {
        //单独提出来，完全是为了之后便于修改池，即池配置
        //todo_finised !!!之后为了应对多个传感器，将之改为并发池，一定要处理线程结束与启动的问题。当前是直接关闭线程池来解决的，会停止一切线程池中任务。切记切记。
        //todo !!于此同时，还需要处理线程池switchCleanType()中获取属性的硬编码，要修改为针对不同传感器，获取不同传感器数据。
        //这里先采用newSingleThreadScheduledExecutor()的单线程任务池，之后可以修改为newScheduleThreadPool()多线程任务池。注意处理多线程的mutex问题
        //ScheduledExecutorService executorService =  Executors.newScheduledThreadPool(2);
//        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService = Executors.newScheduledThreadPool(Integer.parseInt(PropertiesUtil.getProperty("cleanPool.coreSize")));

    }

    static class MyRunable_Min implements Runnable {
        @Override
        public void run() {
            System.out.println("Min_run");
            //数据清洗模块代码，嗯，简称业务代码
            //可以选择不同的数据清洗方式，将数据处理方式提取出来作为不同的数据清洗子模块

            Thread currThread = Thread.currentThread();
            SwitchCleanType("MIN", currThread);
            while (currThread.isInterrupted()) {
                //但此处没有使用的必要，stop()的暴力，不安全问题在此处不会暴露，实现简单。
                currThread.stop();

                //调用业务代码中的数据提交方法

            }
        }
    }

    static class MyRunable_Hour implements Runnable {
        @Override
        public void run() {
            System.out.println("Hour_run");
            //数据清洗模块代码，嗯，简称业务代码

            Thread currThread = Thread.currentThread();
            SwitchCleanType("HOUR", Thread.currentThread());
            while (currThread.isInterrupted()) {
                //但此处没有使用的必要，stop()的暴力，不安全问题在此处不会暴露，实现简单。
                currThread.stop();
            }
        }
    }

    static class MyRunable_Day implements Runnable {
        @Override
        public void run() {
            System.out.println("Day_run");
            //数据清洗模块代码，嗯，简称业务代码

            Thread currThread = Thread.currentThread();
            SwitchCleanType("DAY", Thread.currentThread());
            while (currThread.isInterrupted()) {
                //但此处没有使用的必要，stop()的暴力，不安全问题在此处不会暴露，实现简单。
                currThread.stop();

                //调用业务代码中的数据提交方法

            }
        }
    }


    private static void MinTaskStart() {
        poolInit();
        System.out.println("分钟任务启动");
        //扰乱函数，生成扰乱值，避免任务在时间上的集中（该方法简单，日后可从队列以及并发调优的角度进行优化
        //注意double类型转int类型时，会丢失所有小数部分的精度，只保留整数部分（不做四舍五入等操作）。避免扰动过大（其实，扰动的结果只体现在初始延迟，以及之后的周期数据的产生时间）
        int disturb_delay = (int) (Const.MIN_PERIOD * (1 - Math.random() * disturbRatio));
        log.info("MinTask is starting!  扰动延迟disturb=" + disturb_delay);
//        executorService.scheduleWithFixedDelay(new MyRunable_Min(), disturb_delay, Const.MIN_PERIOD, TimeUnit.SECONDS);
        executorService.scheduleWithFixedDelay(new MyRunable_Min(), 1, 1, TimeUnit.SECONDS);
        log.info("MinTask is started!");
        System.out.println("分钟任务启动结束");
    }

    private static void HourTaskStart() {
        poolInit();
        //这里先采用newSingleThreadScheduledExecutor()的单线程任务池，之后可以修改为newScheduleThreadPool()多线程任务池。注意处理多线程的mutex问题
        System.out.println("小时任务启动");
        int disturb_delay = (int) (Const.HOUR_PERIOD * (1 - Math.random() * disturbRatio));
        log.info("HourTask is started!  扰动延迟disturb=" + disturb_delay);
//        executorService.scheduleWithFixedDelay(new MyRunable_Hour(), disturb_delay, Const.HOUR_PERIOD, TimeUnit.SECONDS);
        executorService.scheduleWithFixedDelay(new MyRunable_Hour(), 1, 1, TimeUnit.SECONDS);
        log.info("HourTask is started!");
        System.out.println("小时任务启动结束");
    }

    private static void DayTaskStart() {
        poolInit();
        //这里先采用newSingleThreadScheduledExecutor()的单线程任务池，之后可以修改为newScheduleThreadPool()多线程任务池。注意处理多线程的mutex问题
        System.out.println("天任务启动");
        int disturb_delay = (int) (Const.DAY_PERIOD * (1 - Math.random() * disturbRatio));
        log.info("DayTask is started!  扰动延迟disturb=" + disturb_delay);
//        executorService.scheduleWithFixedDelay(new MyRunable_Day(), disturb_delay, Const.DAY_PERIOD, TimeUnit.SECONDS);
        executorService.scheduleWithFixedDelay(new MyRunable_Day(), 1, 1, TimeUnit.SECONDS);
        log.info("DayTask is started!");
        System.out.println("天任务启动结束");
    }

    //其实定时器，或者说数据清洗方式的切换，完全可以通过远程调用。但考虑到复杂的网络情况
    //最终采取数据库与本地文件双保险。数据库与本地文件通过hashcode来检查区别。
    //其实后期也可以在为本地配置文件专配一个定时器，用于检查其动态变化，再通知每个相关的任务。但这儿也需要处理任务注册。
    //todo 也许可以从Spring框架获取一定的支持？ 或者建立一个。    当然，以目前的需求，是没有什么必要的。
    private static void SwitchCleanType(String currType, Thread currThread) {
        //// TODO: 3/1/2019 由于当前只有一种传感器策略，所以这里采用固定参数。以后有需要时，该处参数需要根据传感器来源，来读取不同的传感器参数。
        cleanInterval = PropertiesUtil.getProperty("sensor.interval");
        if (!StringUtils.equals(cleanInterval, currType)) {
            //这里也可以把相关编码提取到CONST中，便于解耦
            System.out.println(currType + "定时器 切换定时器");

//            executorService.shutdown();
            //线程时间规则的切换，通过结束当前线程来完成。因为之后线程池会使用并发
            //现在多采用interrupt()方法。之后如果进行更细致的模块化处理，可以考虑interrupt()+futureTask
            currThread.interrupt();

            log.info(currType + "timer is shutdown.switching to " + cleanInterval + " timer!");
            switch (cleanInterval) {
                case "MIN":
                    MinTaskStart();
                    System.out.println("switch to MIN");
                    break;
                case "HOUR":
                    HourTaskStart();
                    System.out.println("switch to HOUR");
                    break;
                case "DAY":
                    DayTaskStart();
                    System.out.println("switch to DAY");
                    break;
                default:
                    System.out.println("尚未设置相关模块，请及时更新软件");
                    break;
            }
            log.info("switch to " + cleanInterval + " timer done!");
        }

    }

    //cleanInterval Application
    //业务代码必须考虑不同队列之间的不可交互，既可以通过共享资源mutex（Monitor），也可以通过类似池的概念，来形成多条并行队列。
    // 前者可以从AQS参考，后者可以参考kalfka等MQ中并行消息Queue.(BlockingQueue)
    //1.峰值  mutex   问题的关键：状态值的保存，状态值应该交与谁来保存，用什么方式保存

    private static void InclinationDeal(double origin_X, double origin_Y) {
        //todo 计算合倾角 先这样写，之后再改为正式的
        double dip_angle = origin_X + origin_Y;

        //如果之后需要区分型号时，这里可以再进行细致拆分，同样可以通过静态表来进行。
        // 当然不同传感器不同型号的代码执行路径，一方面可以通过上一次决定，另一方面，可以在这里通过地址码和设备注册信息（这个可以提取做一个工具类）
        //目前的规模，想法是同一类传感器数据，同样的逻辑路径，增加缺省值
        peakCompare(InclinationConst.SENSORNAME, dip_angle);
    }


    //峰值计算的公共方法部分

    /**
     * 将新数据置入peak 的Map中，确保peak中保存的是对应峰值（如果没有就添加当前数据）
     *
     * @param peakKey
     * @param peakValue
     */
    private static void peakCompare(String peakKey, double peakValue) {
        Double existedValue = Double.valueOf(RedisPoolUtil.get(Const.CLEAN_TASK_PREFIX + peakKey));
        int retval = Double.compare(peakValue, existedValue);

        //合并两个条件，利用||逻辑上的短路特性，可以提高性能。毕竟后者条件要比前者条件难触发许多许多
        if (retval > 0 || existedValue == null) {
            //todo 此处的超期时间暂时硬编码，后面无论是改写到Const中，还是设置为配置表中的配置，都是挺好的。
            RedisPoolUtil.setEx(Const.CLEAN_TASK_PREFIX + peakKey, Double.toString(peakValue), 60 * 60 * 24 * 3);
        }
    }

    //如果数据类型不统一为double，可进行转换，再不行就利用泛型
    private static double peakGet(String peakKey) {
        Double existedValue = Double.valueOf(RedisPoolUtil.get(Const.CLEAN_TASK_PREFIX + peakKey));
        //如果需要，考虑对应数据不存在，或者异常的情况，但应该不需要
        return existedValue;
    }


    //test
    public static void main(String[] args) {
        init();
    }

    //数据清洗模块单独拎出来

}
