package com.renewable.gateway.taskDemo;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.sensor.InclinationConst;

import com.renewable.gateway.serial.SerialPool;
import com.renewable.gateway.serial.sensor.InclinationDeal526T;
import com.renewable.gateway.service.IInclinationService;
import com.renewable.gateway.service.ISensorDataService;
import com.renewable.gateway.util.MatlabUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sinfit.Calcul;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Description：
 * @Author: jarry
 */
@Component
@Slf4j
public class simpleTimer {

    @Autowired
    private ISensorDataService iSensorDataService;

    @Autowired
    private SerialPool serialPool;

    @Autowired
    private IInclinationService iInclinationService;


    static {
        System.out.println("test_node_init");
    }

    /**
     * 用于实现轮询获取传感器监测数据
     */
    @Scheduled(cron = "*/5 * * * * *")  //每五秒钟    //数据读取
    public void requireSerialData() {
        log.info("请求监测数据定时任务启动");

        System.out.println("simpleTimer first task start.currentTime:" + System.currentTimeMillis());
        byte[] test = {1, 2, 3, 4};
//        SerialPool.sendData("COM1",test);
//        serialPool.sendData("COM2",test);
//        SerialPool.sendData("COM3",test);


        ServerResponse<byte[]> serverResponse = InclinationDeal526T.command2Origin(00, InclinationConst.InclinationSensor1Enum.READALL);
        if (!serverResponse.isSuccess()) {
            log.error("error");
            System.out.println("simpleTimer data transfer failure");
        } else {
            byte[] originArray = serverResponse.getData();
            System.out.println(Arrays.toString(originArray));
            serialPool.sendData("COM4", originArray);
            serialPool.sendData("COM6", originArray);
//            serialPool.sendData("COM6",originArray);
        }
        System.out.println("simpleTimer second task end.currentTime:" + System.currentTimeMillis());

        log.info("请求定时任务结束");
        System.out.println("请求定时任务结束");

    }


//    @Scheduled(cron = "*/10 * * * * *")  //10秒    //数据清洗      //由于业务方案改动，故改变了原有数据清洗的架构。由于清洗时不需要再加入新的计算数据，故数据清洗现结合MYSQL的触发器执行。
//    public void cleanSensorData(){
//        log.info("数据清洗开始"+System.currentTimeMillis());
//        ServerResponse serverResponse = iSensorDataService.cleanDataTasks();
//        if (!serverResponse.isSuccess()){
//            log.error("error:cleanSensorData Timer occurpired"+serverResponse.getMsg());
//        }
//        log.info("数据清洗结束");
//    }

//    @Scheduled(cron = "*/10 * * * * *")  //10秒      //matlab
    public void testMatlabCompile(){
        System.out.println("matlab call process test start！");
        double[][] testArray1 = {{0,315},{0,225},{1.707,90},{0.1,270}};
//        Object[] result1 = iInclinationService.initAngleTotalCalMatlab(testArray1,1);
        Object[] result2 = MatlabUtil.initAngleTotalCalMatlab(testArray1,1);
        System.out.println(Arrays.toString(result2));

        MWNumericArray mwNumericArray = new MWNumericArray(result2,MWClassID.DOUBLE);
//        mwNumericArray.dispose(result2[1]);

//        System.out.println(Arrays.toString(mwNumericArray.getDoubleData()));
//        System.out.println("end");


//        Calcul calcul = new Calcul();
//        Object[] result = calcul.sinfit(4, 1.1D, 1.1D, 1.1D, 1.1D, 1.1D, 1.1D, 2.1D, 1.1D, 1.1D);
//        System.out.println(Arrays.toString(result));
        System.out.println("matlab call process test end");
    }

}
