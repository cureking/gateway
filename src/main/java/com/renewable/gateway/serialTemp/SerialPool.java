package com.renewable.gateway.serialTemp;

import com.renewable.gateway.exception.serial.*;
import com.renewable.gateway.util.SensorPropertiesUtil;
import com.renewable.gateway.util.SerialPortUtil;
import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
//@Component
public class SerialPool {

    private static final long serialVersionUID = 1L;

    private static ThreadPoolExecutor executor;

    private static ArrayList<String> portList = SerialPortUtil.findPort();

    private static HashMap<String, Boolean> serialPortStatus = new HashMap<String, Boolean>();

    private static ConcurrentHashMap<String, SerialPort> serialPortConcurrentHashMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, SerialListener> serialListenerConcurrentHashMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, PriorityBlockingQueue<SerialTask>> serialTaskConcurrentHashMap = new ConcurrentHashMap<>();

    //todo 暂时先共用一个统一的频率，后面拆分    //感觉初始值没影响
    private static int initBaudrate = Integer.valueOf(SensorPropertiesUtil.getProperty("sensor.bautrate"));


    //静态块初始化
    static {
        System.out.println("serial init starting");
        init();
        System.out.println("SerialPool init end");
    }


    //静态初始化
    @PostConstruct
    public static void init() {
        System.out.println("serial init starting");
        poolInit();
        serialPortAssemble();

        serialPortTaskDispatch();
        System.out.println("SerialPool init end");
    }

    /**
     * 线程池初始化
     */
    private static void poolInit() {
        //这里属于原型，直接使用ThreadPoolExecutor建立。并且参数硬编码
        executor = new ThreadPoolExecutor(10, 20, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(10));
    }

    //serialPort对应队列初始化

    /**
     * 用于装填serialPort对象
     */
    private static void serialPortAssemble() {

        //将所有串口对象serialPort置入serialPortConcurrentHashMap
        for (Iterator iter = portList.iterator(); iter.hasNext(); ) {
            String str = (String) iter.next();

            //todo 做了配置表之后，这里可以直接通过配置表 如：PropertiesUtil.getProperty(str); 获取对应波特率
            int baudarte = initBaudrate;

            //避免不必要的资源浪费（而且少了一些报错，心里舒服多了。。。）。毕竟很多这个条件成立的可能性是很高的（也许我们日后可以做一个串口增加时监听的优化）
            if (serialPortConcurrentHashMap.get(str) != null) {
                continue;
            }


            try {
                //打开对应串口对象
                SerialPort serialPort = SerialPortUtil.openPort(str, baudarte);
                //将对应串口对象置入对象列表（作为串口对象池）
                serialPortConcurrentHashMap.put(str, serialPort);
                //为对应串口对象部署串口监听者    有时候在想，如果除了SerialPort和SerialListener，还有别的对象Map，那就合并一处，一个index，多个参数
                //todo （看上一条）以后可以建立对应参数信息表，包括波特率什么的，作为日后的优化
                serialListenerAssemble(serialPort);
            } catch (SerialPortParameterFailure serialPortParameterFailure) {
                serialPortParameterFailure.printStackTrace();
            } catch (NotASerialPort notASerialPort) {
                notASerialPort.printStackTrace();
            } catch (NoSuchPort noSuchPort) {
                noSuchPort.printStackTrace();
            } catch (PortInUse portInUse) {
                portInUse.printStackTrace();
            }
        }

        //初始化serialPort对应的传感器任务队列
        serialTaskQueueInit();
        //初始化serialPort对应的传感器状态队列
        serialStatusQueueInit();

        System.out.println("serialPort装填成功。serialPortMap:" + serialPortConcurrentHashMap);
    }

    private static void serialListenerAssemble(SerialPort serialPort) {

        //监听器这里代码一定存在冗余，当初为了方便引入serialPort对象，直接将无参constructor改为了全参constructor。这部分必然有冗余，之后优化
        SerialListener serialListener = new SerialListener(serialPort);

        try {
            SerialPortUtil.addListener(serialPort, serialListener);
        } catch (TooManyListeners tooManyListeners) {
            log.warn("serialPortOutputStreamCloseFailure", tooManyListeners);
            tooManyListeners.printStackTrace();
        }
        serialListenerConcurrentHashMap.put(serialPort.getName(), serialListener);
        System.out.println("# 对应传感器监听者部署完毕：" + serialPort.getName());
    }

    private static void serialTaskQueueInit() {
        for (String port : portList) {
            PriorityBlockingQueue<SerialTask> serialTasks = new PriorityBlockingQueue<>();
            serialTaskConcurrentHashMap.put(port, serialTasks);
            System.out.println("# 对应传感器任务队列初始化完成：" + port);
        }
    }

    private static void serialStatusQueueInit() {
        for (String port : portList) {
            serialPortStatus.put(port, false);
            System.out.println("# 对应传感器状态队列初始化完成：" + port);
        }
    }

    private static boolean portListRefresh() {
        ArrayList<String> portList_new = SerialPortUtil.findPort();

        if (portList == portList_new) {
            portList = portList_new;
            return true;
        }
        return false;
    }

    /**
     * @param port
     * @param priority   优先级
     * @param originData
     */
    private static void serialTaskQueuePush(String port, int priority, byte[] originData) {
        SerialPort serialPort = serialPortConcurrentHashMap.get(port);
        SerialListener serialListener = serialListenerConcurrentHashMap.get(port);

        SerialTask serialTask = new SerialTask(serialPort, serialListener, priority, originData);
        serialTaskConcurrentHashMap.get(port).offer(serialTask);
    }

    private static void serialPortTaskDispatch() {

        for (String port : portList) {
            FutureTask<String> task = new FutureTask<>(new DispatchTask(port));
            new Thread(task).start();
        }
    }

    /**
     * 执行任务队列队首任务，并将该任务移出队列
     *
     * @param port
     */
    protected static void serialPortExcute(String port) throws InterruptedException {

        {
            //怎样才可以写出一个符合要求的执行器呢？
            //todo 一方面要保证满足条件时（对应串口任务队列有任务，对应串口没有任务正在执行）。
            //todo 另一方面，上述两个条件不满足时，要能够等待到条件满足时，去执行任务。
            //也许我可以参考一下线程池的实现原理？    或者说synchronized就可以解决部分问题？（再加上阻塞？）

            Future<String> future = null;

            //todo 这里现在通过循环的模式来实现，轮询是个好东西。但是我更希望实现AIO的那种回调模式，这样对资源的消耗会极大降低。实在不行，先通过梯度降频，降低资源消耗吧。

            while (serialTaskConcurrentHashMap.get(port).isEmpty()) {
                Thread.currentThread().sleep(5);
            }
            while (!serialTaskConcurrentHashMap.get(port).isEmpty()) {
                System.out.println("test_node_5:" + System.currentTimeMillis());
                future = executor.submit(serialTaskConcurrentHashMap.get(port).poll());
                if (future.isDone()) {
                    break;
                }
                System.out.println("test_node_6:" + System.currentTimeMillis());

                while (serialTaskConcurrentHashMap.get(port).isEmpty()) {
                    Thread.currentThread().sleep(5);
//                    System.out.println("this taskThread have no task.CurrentThread is :"+port+" thread");
                }
            }
        }
    }


    //todo 同样可以做一个抽象封装，提高代码可读性。于此同时，提高代码扩展性

    /**
     * @param port
     * @param priority   优先级，由上层确定，确保底层的通用性。（优先级越高，越先执行）
     * @param originData
     */
    public static void serialTaskGet(String port, int priority, byte[] originData) {


        if (originData == null) {
            log.warn("所要发送于" + port + "的数据为空!");
            return;
        }
        if (portList.indexOf(port) == -1) {
            log.warn("目标串口 " + port + "不存在逻辑串口列表");
            log.info("重新刷新串口列表");
            serialRefresh();

            if (portList.indexOf(port) == -1) {
                log.warn("目标串口 " + port + "物理形态不存在");
                return;
            } else {
                log.warn("目标串口 " + port + "于串口列表中更新成功");
            }
        }
        serialTaskQueuePush(port, priority, originData);
    }

    public static void serialRefresh() {
        //确定更新是有必要的
        boolean updated = portListRefresh();
        if (updated == true) {
            //与此同时更新具体对象列表
            //之后这里可以改为只更新updated的串口。那样估计需要写serialPortAssemble的有参重载方法
            serialPortAssemble();   //serialPortAssemble中包含listener,task,status的更新

            //关闭线程池，并重新初始化
            executor.shutdown();
            while (executor.isTerminated()) {
                poolInit();
            }

            //重新初始化，并执行任务调度器dispatch
            serialPortTaskDispatch();
        }
    }


    //test
    public static void main(String[] args) throws InterruptedException {
        init();
//        byte[] arr = new byte[]{1, 2, 1, 1};

//        for (; ; ) {
//            Thread.currentThread().sleep(1000);
//            System.out.println("send data");
//
//            serialTaskGet("COM2", 1, new byte[]{1, 1, 1, 1});
//            serialTaskGet("COM2", 1, new byte[]{1, 1, 1, 1});
//
//            serialTaskGet("COM3", 1, new byte[]{1, 3, 1, 1});
//            serialTaskGet("COM3", 1, new byte[]{1, 3, 1, 1});
//        }
    }
}
