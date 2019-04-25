package com.renewable.gateway.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Description：
 * @Author: jarry
 */
@Component
@Slf4j

//通过该注解引入动态配置表。@link:http://cache.baiducontent.com/c?m=9d78d513d9d447ad4fece43f5c4c80305e0ad7352bd7a1020fde843995732b425011e5ac26520775a0d20c6416df384b9df62235775d2feddd8eca0bcabae13532cd6271671cf11c538243f4971532c157cc0dafb25aa0edb174c8f38eced85159ce56036d8784815c0215cf&p=8562c64ad4d91efd49bd9b785f&newp=c677c715d9c55ae70be296656153d8224216ed633ac3864e1290c408d23f061d4862e7bf23281705d2c17a610aaa4d5aedf7327923454df6cc8a871d81ed8960&user=baidu&fm=sc&query=%40scheduled+%B8%C4%B1%E4%CA%B1%BC%E4&qid=ee2b2c30000179e2&p1=8
//@PropertySource("classPath:root/sensor.properties")
public class DataTaskDispatch {

//    @Scheduled(cron="${incl.schedule}")//每1分钟(每个1分钟的整数倍)
//    public void closeOrderTaskV1(){
//        log.info("关闭订单定时任务启动");
//        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
////        iOrderService.closeOrder(hour);
//        log.info("关闭订单定时任务结束");
//    }
//
//
//    @Scheduled(cron="${incl.schedule}")//每1分钟(每个1分钟的整数倍)
//    public void Inclination(){
//        log.info("关闭订单定时任务启动");
//        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
////        iOrderService.closeOrder(hour);
//        log.info("关闭订单定时任务结束");
//    }


    //不过怎么样，先写一个简单获取倾斜数据的
    //@link:https://blog.csdn.net/zc_ad/article/details/83340861
    @Scheduled(fixedDelay = 100) //每100ms，运行一次
    public void sendData() {     //也许JNA的数据，也不同会调用函数返回

    }
}
