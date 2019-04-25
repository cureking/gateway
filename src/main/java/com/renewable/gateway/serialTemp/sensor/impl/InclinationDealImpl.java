package com.renewable.gateway.serialTemp.sensor.impl;

import com.google.common.collect.Lists;
import com.renewable.gateway.common.Const;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.dao.InclinationMapper;
import com.renewable.gateway.pojo.Inclination;
import com.renewable.gateway.serialTemp.SerialPool;
import com.renewable.gateway.serialTemp.sensor.IInclinationDeal;
import com.renewable.gateway.serialTemp.sensor.SerialSensorDeal;
import com.renewable.gateway.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


/**
 * @Description：
 * @Author: jarry
 */

/**
 * 为了追求速度，可以暂时先将串口数据处理与倾斜传感器数据对象化放在一块儿
 * 两层三个职责：首先转换数据至Object，其次保存原始数据，并将数据发往数据清洗构件
 */
@Slf4j
@Service("iInclinationDeal")
//@ContextConfiguration(locations = {"classpath:applicationContext.xml"}) //引入spring配置。
public class InclinationDealImpl extends SerialSensorDeal implements IInclinationDeal {
    //todo 这里假设每一条接受的数据都是一条完整数据。之后优化，需要进行数据分包，沾包处理

    @Autowired
    private InclinationMapper inclinationMapper;

    //由于新的thread不在spring容器中，故无法注入
//    ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
//    private InclinationMapper inclinationMapper = (InclinationMapper) ac.getBean("InclinationMapper");


//    /**
//     *
//     * @param inclination   pojo
//     * @param port  串口名称，如"COM1"
//     * @param addres    地址码
//     * @param sensor    传感器类型
//     * @param baudrate  波特率
//     * @return
//     */
//    private static OriginDataAddtion<Inclination> originDataAddtionAssemble(Inclination inclination,
//                                                                             String port,int addres,String sensor,int baudrate){
//        OriginDataAddtion<Inclination> originDataAddtion = new OriginDataAddtion<Inclination>();
//
//        originDataAddtion.setPort(port);
//        originDataAddtion.setAddres(addres);
//        originDataAddtion.setSensor(sensor);
//        originDataAddtion.setBaudrate(baudrate);
//
//        originDataAddtion.setData(inclination);
//
//        return originDataAddtion;
//    }

    private static Inclination originData2Object(byte[] originData) {
        Inclination inclination = new Inclination();
//        Inclination.setAngleX();
        //这里暂时先利用硬编码实现，但很明显这并不优雅，之后优化处理。而且这里代码的健壮性极差，完全无法应对任何意料之外的情况，如outofarrayrangeexception.etc
        double origin_X = origin2Double(originData[4], originData[5], originData[6], originData[7]);
        double origin_Y = origin2Double(originData[8], originData[9], originData[10], originData[11]);
        double origin_T = origin2Double(originData[12], originData[13], originData[14], originData[15]);
        inclination.setAngleX(origin_X);
        inclination.setAngleY(origin_Y);
        inclination.setTemperature(origin_T);
        //该数据的create_time由mybatis的mapper中now()函数控制

        return inclination;
    }

    /**
     * List<Integer>转为对应2字节Byte[]类型 （容量扩大一倍）
     *
     * @param list
     * @return
     */
    private static byte[] list2OriginData(List<Integer> list) {
        byte originData[] = new byte[list.size() * 2];
        for (int j = 0; j < list.size(); j += 2) {
            originData[j] = unsignedShortToByte2(list.get(j))[0];
            originData[j + 1] = unsignedShortToByte2(list.get(j))[1];
        }
        return originData;
    }

    /**
     * short整数转换为2字节的byte数组
     *
     * @param s short整数
     * @return byte数组
     */
    private static Byte[] unsignedShortToByte2(int s) {
        Byte[] targets = new Byte[2];
        targets[0] = (byte) (s >> 8 & 0xFF);
        targets[1] = (byte) (s & 0xFF);
        return targets;
    }

    private static String int2Byte(int data) {
        String hex = Integer.toHexString(data);
        return hex;
    }

    private static List<Integer> objece2ListData(int command, int address) {

        //数据先以Integer类型保存，之后转为Byte
        List<Integer> originArray = Lists.newArrayList();

        //这里可以解耦
        int data_0 = 68;
        int data_1 = 4;
        int data_2 = address;
        int data_3 = command;
        int data_5 = 0;

        originArray.add(data_0);
        originArray.add(data_1);
        originArray.add(data_2);
        originArray.add(data_3);

        //设置数据长度（+1是因为数据校验位还没有添加，需要计算）
        originArray.set(1, originArray.size() + 1);

        //计算校验和     校验和是不计算标示符（不计算第一位）与自身的（不计算最后一位）
        for (int j = 1; j < originArray.size() - 1; j++) {
            data_5 += originArray.get(j);
        }
        originArray.add(data_5);
        return originArray;
    }

    /**
     * 但是我改完又后悔了。先不提改后有点难看，这样写就等于将判断data是否为空交由上层处理，上层必须完成函数的挑选。
     * 不过上层还要完成data的建立，所以这里的统一整合，放在之后再细致优化吧。
     *
     * @param command
     * @param address
     * @param data
     * @return
     */
    //todo_finished 这里可以对方法进行重载，毕竟有的命令是不存在数据域的。
    private static List<Integer> objece2ListData(int command, int address, Integer data) {

        //数据先以Integer类型保存，之后转为Byte
        List<Integer> originArray = Lists.newArrayList();

        //这里可以解耦
        int data_0 = 68;
        int data_1 = 4;
        int data_2 = address;
        int data_3 = command;
        int data_4 = data;
        int data_5 = 0;

        originArray.add(data_0);
        originArray.add(data_1);
        originArray.add(data_2);
        originArray.add(data_3);
        originArray.add(data_4);

        //设置数据长度（+1是因为数据校验位还没有添加，需要计算）
        originArray.set(1, originArray.size() + 1);

        //计算校验和     校验和是不计算标示符（不计算第一位）与自身的（不计算最后一位）
        for (int j = 1; j < originArray.size() - 1; j++) {
            data_5 += originArray.get(j);
        }
        originArray.add(data_5);
        return originArray;
    }

    /**
     * 完成三个（也许应该扩展为多个，再做成工具Util）十六进制转double（包含合并）
     *
     * @param data_1
     * @param data_2
     * @param data_3
     * @param data_4
     * @return
     */
    private static Double origin2Double(Byte data_1, Byte data_2, Byte data_3, Byte data_4) {
        double result = 0;
        int dataI_1 = Byte.toUnsignedInt(data_1);
        int dataI_2 = Byte.toUnsignedInt(data_2);
        int dataI_3 = Byte.toUnsignedInt(data_3);
        int dataI_4 = Byte.toUnsignedInt(data_4);

        int sig;
        //这只是针对目前的情况，也许之后会改变这种传输格式
        if (dataI_1 / 16 == 0) {
            sig = 1;
        } else if (dataI_1 / 16 == 1) {
            sig = -1;
        } else {
            sig = 1;  //为保证之后的代码正确运行，我们给它一个default_value :1;
//            System.out.println(data_1+"传输的数据符号位出现问题，出现0/1之外的情况，其高位为： "+dataI_1/16);
            log.error("传输的数据符号位出现问题，出现0/1之外的情况，其高位为： " + dataI_1 / 16);
        }

        int first = dataI_1 % 16;
        result = sig * first * 100 + dataI_2 * 1 + dataI_3 * (-0.01) + dataI_4 * (-0.0001);

//        System.out.println(Double.longBitsToDouble(data_2)*1+Double.longBitsToDouble(data_3)*(-10));
//        System.out.println(Double.longBitsToDouble(data_3));
        return result;
    }

    /**
     * 处理倾斜传感器传输的数据，并对其进行命令解析，以及原始数据保存
     * 简单看了一下，这里日后完全是可以通过泛型已经迭代器模式建立为通用模块（当然，也可以先建立为倾斜传感器通用模块（串口通信的，目前主要为倾斜））
     *
     * @param port         串口所在名   如：COM1
     * @param baudrate     传感器波特率
     * @param originBuffer 传感器传输过来的数据（之后优化，也许需要进行拆包，沾包处理（可以在该模块前插入一个模块））（如果这类通信多了，就做一个过滤器吧）
     */
    private void receiveBuffer(String port, int baudrate, byte[] originBuffer) {
        //这里可以根据型号扩展，当然也可以修改静态配置表
        String sensor = InclinationConst.SENSORNAME;

        //对Byte的命令字数据进行补位处理（确保为正，便于处理）
        int command = originBuffer[3] & 0xFF;

        //接收数据数据处理规则switch
        if (command == InclinationConst.InclinationSensor1Enum.READALLR.getCode()) {
            //应答制下原始数据响应，同时也是自动输出模式下数据响应
            //1.响应处理-通过缓存实现状态的保护

            //由于并没有进行并发处理，所以是串行，阻塞的。阻塞的时间视redisd的timeout（我已经将其从2000ms改为20ms，因为之后的redis是localhost的）与网络情况决定的
            //todo 为了更加优雅地解决该问题，可以将这里进行并发处理，其实数据的先后顺序误差在1秒内是可以接受的。所以可以采用线程池解决，而不需要进行事务管理
            String model = RedisPoolUtil.get(Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETMODR.getCode());
            if (model != null || model == Integer.toString(InclinationConst.InclinationSensor1Response.FAILED)) {
                String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.READALLR.getCode();
                //其实，这里还需要根据之前请求的数据是否存在，来进行进一步的响应处理。不过暂时不需要处理得那么细致（非功能需求&兴奋需求）
                String result = RedisPoolUtil.setEx(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS), Const.DEAL_SET_KEEP);


            }

            //2.对接收到的检测参数进行处理
            Inclination inclination = originData2Object(originBuffer);
            System.out.println("AngleX:" + inclination.getAngleX());
            //那么这里需要调用方法（或者说工具，推荐使用工具，毕竟还有JNA那条线的数据需要进行相关处理），进行原始数据的保存
            //这里直接用mapper保存数据，之后视情况，封装
            //Spirng已经通过dataSource建立一个数据连接池，而且正式环境数据库和服务器位于同一台服务器，可以通过localhost访问，不存在太多延迟
            //TODO  3.5晚完成位置    在静态方法调用mapper方法（non-static），保存数据。（可以通过建立目标对象，再调用其non-static方法，当然不是用在这里，需要考虑用在哪里）

            if (inclination == null) {
                //这里也可以做一个简单的回复。这里先直接跳出，避免插入空数据
                return;
            }
            //这里不采用spring 的数据库方式，便于数据库的拆分。完全不是因为我懒得配置两个数据库
            //todo ！！！  这里出现一个重大问题，inclinationMapper没有完成Spring的注入。所以运行时会产生NullPointer的异常
            //@link https://blog.csdn.net/HELLOMRP/article/details/79736502         大致意思是被@Autowired注入的类（this）也需要是通过注入实现的才可以。
            //也许这是一个很如了解Spring IOC的机会。
            //所以我决定这周先继续调整项目结构（有点乱），然后作出顶层架构图，填充其他模块。周末call大佬看看（应用），自己也深入看看Spring IOC原理


            System.out.println("尝试向数据库保存数据：AngleX=" + inclination.getAngleX() + " AngleY=" + inclination.getAngleY() + " Temperature=" + inclination.getTemperature());
            System.out.println("inclinationMapper: " + inclinationMapper);
            int result = inclinationMapper.insert(inclination);
            if (result == 0) {
                log.warn("插入数据失败：" + inclination.toString());
                System.out.println("插入数据失败：" + inclination.toString());
            } else {
                System.out.println("插入数据成功：" + inclination.toString());
                //其实可以给调用者一个回复，这里先不做的那么细致了
            }
        } else if (command == InclinationConst.InclinationSensor1Enum.SETZEROR.getCode()) {
            //1.响应处理-通过缓存实现状态的保护
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETZEROR.getCode();
            //这是一个特例，不需要设定过期时间，因为我们需要靠这个变量来提升降低数据采集模块的不必要程序运行
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));
        } else if (command == InclinationConst.InclinationSensor1Enum.SETSPEEDR.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETSPEEDR.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));
        } else if (command == InclinationConst.InclinationSensor1Enum.SETMODR.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETMODR.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));
        } else if (command == InclinationConst.InclinationSensor1Enum.SETADDRR.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETADDRR.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));
        } else if (command == InclinationConst.InclinationSensor1Enum.GETZEROR.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.GETZEROR.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));
        } else { //2.无法解析数据
            log.error("can't exact this data package:", Arrays.toString(originBuffer));
        }


        //发送数据处理规则switch
        //通过slepp与cache来返回正确的状态

    }

    //todo_finished 依旧是Inclination1与Inclination的问题，是否通过null来判断数据，到后面数据处理细节时，再明确
    private boolean sendBuffer(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum) {
        byte[] originData;

        if (inclinationSensor1Enum == null) {
            //也许这里可以新建一种返回响应格式，如ResponseServer那样
            return false;
        }

        //1. 生成对应数据帧指令
        int command = inclinationSensor1Enum.getCode();
        if (command == InclinationConst.InclinationSensor1Enum.READALL.getCode()) {
            //todo_finished 设置缓存状态，便于确定传感器相关设置操作的状态进展
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.READALL.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));

            //生成指令数据帧
            originData = list2OriginData(objece2ListData(command, address));

            //todo 后面的一些需要加数据的，需要添加data。那么还不如写一个重载方法
        } else if (command == InclinationConst.InclinationSensor1Enum.GETZERO.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.GETZERO.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));

            originData = list2OriginData(objece2ListData(command, address));
        } else {
            log.warn("发送数据时，发生异常 ", command);
            return false;
        }

        //2.发送对应指令
        //这里肯定不会直接调用。或许我可以建立一个专门发送数据的池，或者就用之前的sensorPool。但是这种指令的频度不会很高的。
//        SensorPool.serialSendData(port,baudrate,originData);

        //正常接收监控数据的响应当然没有别的指令优先级高啦。 //不过想一想，其实优先级的设置更多也是为了以后。因为现在一不用回调，二不用太大规模
        int priority = (command == InclinationConst.InclinationSensor1Enum.READALL.getCode()) ? 0 : 1;
        System.out.println("InclinationDeal/sendBuffer");
        SerialPool.serialTaskGet(port, priority, originData);
        System.out.println("InclinationDeal/sendBuffer_2");

        //todo 这里有两个选择，其一是在发送指令后就确定操作成功，其二是在发送后，当前线程等待状态回调等方式来确保操作切实改变了远程传感器。
        //这里之后改为回调，或者futureTask什么的，再不济弄成阻塞的也是不错的。
        //最不济，也可以用循环不断问询redis，看状态有没有更新
//        try {
//            Thread.currentThread().sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //返回程序执行结果，当然这里是需要点时间的。
        return true;
    }

    private boolean sendBuffer(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum, String data) {
        byte[] originData;

        if (inclinationSensor1Enum == null) {
            //也许这里可以新建一种返回响应格式，如ResponseServer那样
            return false;
        }

        int command = inclinationSensor1Enum.getCode();
        if (command == InclinationConst.InclinationSensor1Enum.SETZERO.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETZERO.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));

            originData = list2OriginData(objece2ListData(command, address));
        } else if (command == InclinationConst.InclinationSensor1Enum.SETSPEED.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETSPEED.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));

            originData = list2OriginData(objece2ListData(command, address));
        } else if (command == InclinationConst.InclinationSensor1Enum.SETMOD.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETMOD.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));

            originData = list2OriginData(objece2ListData(command, address));
        } else if (command == InclinationConst.InclinationSensor1Enum.SETADDR.getCode()) {
            String redisKey = Const.DEAL_SET_PREFIX + InclinationConst.InclinationSensor1Enum.SETADDR.getCode();
            RedisPoolUtil.set(redisKey, Integer.toString(InclinationConst.InclinationSensor1Response.SUCCESS));

            originData = list2OriginData(objece2ListData(command, address));
        } else {
            log.warn("发送数据时，发生异常 ", command);
            return false;
        }

        //2.发送对应指令  (确定优先级先）
        int priority = (command == InclinationConst.InclinationSensor1Enum.READALL.getCode()) ? 0 : 1;
        SerialPool.serialTaskGet(port, priority, originData);
        return true;
    }


    //todo 以后这里可以考虑专门封装一个返回类型

    /**
     * 就是专门用于接收底层（如监听器）的数据
     *
     * @param port
     * @param baudrate
     * @param originBuffer
     * @return
     */
    public boolean receiveData(String port, int baudrate, byte[] originBuffer) {
        //进行参数验证    //之后优化时，可以进行细致拆分
        if (port == null || baudrate <= 0 || originBuffer == null) {
            return false;
        }
        receiveBuffer(port, baudrate, originBuffer);
        return true;
    }

    /**
     * 用于高层发送指令
     *
     * @param port
     * @param baudrate
     * @param address
     * @param inclinationSensor1Enum
     * @return
     */
    public boolean sendData(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum) {
        //可通过@Value("#{ sensor.properties['sensor.bautrate'] }")实现默认值注入。但要先Spring-config
        //@link:https://blog.csdn.net/vcfriend/article/details/79700048
        //@link:https://docs.spring.io/spring/docs/4.3.14.BUILD-SNAPSHOT/spring-framework-reference/htmlsingle/#expressions-beandef-annotation-based
        System.out.println("SerialSensorDeal/sendData");
        boolean result = sendBuffer(port, baudrate, address, inclinationSensor1Enum);
        return result;
    }

    public boolean sendData(String port, int baudrate, int address, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum, String data) {
        boolean result = sendBuffer(port, baudrate, address, inclinationSensor1Enum, data);
        return result;
    }
}

