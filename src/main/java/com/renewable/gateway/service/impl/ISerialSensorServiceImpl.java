package com.renewable.gateway.service.impl;

import com.google.common.collect.Lists;
import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.SerialSensorMapper;
import com.renewable.gateway.pojo.SerialSensor;
import com.renewable.gateway.rabbitmq.producer.SerialSensorProducer;
import com.renewable.gateway.service.ISerialSensorService;
import com.renewable.gateway.util.SerialPortUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_ID;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@Service("iSerialSensorServiceImpl")
public class ISerialSensorServiceImpl implements ISerialSensorService {

    @Autowired
    private SerialSensorMapper serialSensorMapper;

    @Autowired
    private SerialSensorProducer serialSensorProducer;


    public ServerResponse insert(SerialSensor serialSensor){
        // 1.数据校验（部分情况下还有权限校验）
        if (serialSensor == null || serialSensor.getPort() == null){
            return ServerResponse.createByErrorMessage("the serialSensor is null or the port of the serialSensor is null !");
        }

        // 2.serialSensorAssemble
        SerialSensor insertSerialSensor = serialSensor; // 这里简化一下

        // 3.插入数据
        int countRow = serialSensorMapper.insertSelective(insertSerialSensor);
        if (countRow == 0){
            return ServerResponse.createByErrorMessage("the serialSensor insert fail !");
        }

        // 4.返回成功响应
        return ServerResponse.createBySuccessMessage("the serialSensor insert success .");
    }

    public ServerResponse insertBatch(List<SerialSensor> serialSensorList){
        // 1.数据校验（部分情况下还有权限校验）
        if (serialSensorList == null || serialSensorList.size() == 0){
            return ServerResponse.createByErrorMessage("the serialSensorList is null or the size of the serialSensorList is zero !");
        }

        // 2.serialSensorAssemble
        List<SerialSensor> insertSerialSensorList = serialSensorList;// 这里简化一下
        if (insertSerialSensorList == null || insertSerialSensorList.size() == 0){
            return ServerResponse.createByErrorMessage("serialList assemble fail ! the serialList is :"+ insertSerialSensorList.toString());
        }

        // 3.插入数据
        int countRow = serialSensorMapper.insertBatch(insertSerialSensorList);
        if (countRow == 0){
            return ServerResponse.createByErrorMessage("the serialSensor insert fail !");
        }

        // 4.返回成功响应
        return ServerResponse.createBySuccessMessage("the serialSensor insert success .");
    }

    public ServerResponse update(SerialSensor serialSensor){
        // 1.数据校验（部分情况下还有权限校验）
        if (serialSensor == null || serialSensor.getPort() == null){
            return ServerResponse.createByErrorMessage("the serialSensor is null or the port of the serialSensor is null !");
        }

        // 2.serialSensorAssemble
        SerialSensor insertSerialSensor = serialSensor; // 这里简化一下

        // 3.插入数据
        int countRow = serialSensorMapper.updateByPrimaryKeySelective(insertSerialSensor);
        if (countRow == 0){
            return ServerResponse.createByErrorMessage("the serialSensor update fail !");
        }

        // 4.返回成功响应
        return ServerResponse.createBySuccessMessage("the serialSensor update success .");
    }

//    public ServerResponse updateBatch(List<SerialSensor> serialSensorList){
//        // 1.数据校验（部分情况下还有权限校验）
//        if (serialSensorList == null || serialSensorList.size() == 0){
//            return ServerResponse.createByErrorMessage("the serialSensorList is null or the size of the serialSensorList is zero !");
//        }
//
//        // 2.serialSensorAssemble
//        List<SerialSensor> insertSerialSensorList = serialSensorList;// 这里简化一下
//        if (insertSerialSensorList == null || insertSerialSensorList.size() == 0){
//            return ServerResponse.createByErrorMessage("serialList assemble fail ! the serialList is :"+ insertSerialSensorList.toString());
//        }
//
//        // 3.插入数据
//        int countRow = serialSensorMapper.insertBatch(insertSerialSensorList);
//        if (countRow == 0){
//            return ServerResponse.createByErrorMessage("the serialSensor insert fail !");
//        }
//
//        // 4.返回成功响应
//        return ServerResponse.createBySuccessMessage("the serialSensor insert success .");
//    }

    @Override
    public ServerResponse refresh(){

        // 1.获取串口通信列表serialSensorList
        List<String> portNameList = SerialPortUtil.findPort();
        if (portNameList == null || portNameList.size() == 0){
            return ServerResponse.createByErrorMessage("the portNameList is null or the size of the portNameList is zero !");
        }

        // 2.组装serialSensorList中的serialSensor（除了port外，其他设置为默认值即可，另外不设置ID）
        List<SerialSensor> serialSensorList = this.serialSensorListGeneratorByPort(portNameList);

        // 3-.将已经注册的传感器从列表中删除   // 从数据库校验
        List<SerialSensor> uploadSerialSensorList = this.serialSensorFilterByExistInDB(serialSensorList);
        if (uploadSerialSensorList == null || uploadSerialSensorList.size() == 0){
            return ServerResponse.createBySuccessMessage("there is no new serialSensor !");
        }

        // 3.（修正）信息发往中控室
        try {
            serialSensorProducer.sendSerialSensor(uploadSerialSensorList);
        } catch (IOException e) {
            log.error("IOException:{}",e);
            return ServerResponse.createByErrorMessage("IOException:"+e.toString());
        } catch (TimeoutException e) {
            log.error("TimeoutException:{}",e);
            return ServerResponse.createByErrorMessage("TimeoutException:"+e.toString());
        } catch (InterruptedException e) {
            log.error("InterruptedException:{}",e);
            return ServerResponse.createByErrorMessage("InterruptedException:"+e.toString());
        }

        // 3.保存serialSensorList至缓存  // 后来这个模型改了，必须将注册信息发送至中控室，由中控室分陪全局ID与其他注册信息，如sensorRegister.etc

        // 4.保存serialSensorList至数据库

        // 5.发送serialSensorList至中控室
        return ServerResponse.createBySuccessMessage("the serialSensor config has uploaded to centControl .");
    }

    private SerialSensor serialSensorGeneratorByPort(String port){
        if (port == null){
            return null;
        }

        SerialSensor resultSerialSensor = new SerialSensor();

        resultSerialSensor.setSensorRegisterId(65565);  // 这里设置一个无效的确定值，确保之后可以更新它
        resultSerialSensor.setTerminalId(Integer.parseInt(GuavaCache.getKey(TERMINAL_ID)));
        resultSerialSensor.setPort(port);
        resultSerialSensor.setAddress("01");        // 这里设置一个默认值（该默认值应当设置在常量表中）.这个量除非硬件做出让步，否则是无法实现自动注册的
        resultSerialSensor.setBaudrate(9600);       // 同上
        resultSerialSensor.setModel(false);
        resultSerialSensor.setZero(false);
        resultSerialSensor.setMark("Init");     // 初始状态，还没有完全设定值，不进行相关数据采集？
        resultSerialSensor.setCreateTime(new Date());
        resultSerialSensor.setUpdateTime(new Date());

        return resultSerialSensor;
    }

    private List<SerialSensor> serialSensorListGeneratorByPort(List<String> portList){
        List<SerialSensor> resultSerialSensorList = Lists.newArrayList();

        for (String item : portList) {
            SerialSensor resultSerialSensor = this.serialSensorGeneratorByPort(item);
            if (resultSerialSensor == null){
                continue;
            }
            resultSerialSensorList.add(resultSerialSensor);
        }

        return resultSerialSensorList;
    }

    private List<SerialSensor> serialSensorFilterByExistInDB(List<SerialSensor> serialSensorList){
        List<SerialSensor> resultSerialSensorList = Lists.newArrayList();
        for (SerialSensor serialSensor : serialSensorList) {
            ServerResponse checkValidResponse = this.checkValidByPort(serialSensor);
            if (checkValidResponse.isFail()){
                // 不存在符合条件的数据，将该数据放入新增列表中
                resultSerialSensorList.add(serialSensor);
            }
        }
        return resultSerialSensorList;
    }

    private ServerResponse checkValidByPort(SerialSensor serialSensor){

        int terminalId = serialSensor.getTerminalId();
        List<SerialSensor> resultSerialSensorList = serialSensorMapper.selectByTerminalIdAndPort(terminalId,serialSensor.getPort());

        if (resultSerialSensorList.size() == 1){
            return ServerResponse.createBySuccessMessage("the serialSensor meeting the condition exist");
        }
        if (resultSerialSensorList.size() > 1){
            // 这里日后可能出现异常，就是查询多个结果出来。也就是一个port有多个address，但是这个异常出现了，也就是提醒该写新的语句了。    // 话说硬件难道就不可以有个标准的注册规范嘛
            return ServerResponse.createBySuccessMessage("the serialSensor meeting the condition exist (more than one)!");
        }
        // 无法获得符合条件的数据
        return ServerResponse.createByErrorMessage("no serial meet the condition !");
    }
}
