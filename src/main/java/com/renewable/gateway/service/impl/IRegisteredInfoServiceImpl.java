package com.renewable.gateway.service.impl;

import com.renewable.gateway.common.Const;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.SensorRegisterMapper;
import com.renewable.gateway.pojo.SensorRegister;
import com.renewable.gateway.service.IRegisteredInfoService;
import com.renewable.gateway.util.OtherUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iRegisteredInfoServiceImpl")
public class IRegisteredInfoServiceImpl implements IRegisteredInfoService {

    @Autowired
    private SensorRegisterMapper sensorRegisterMapper;


    @PostConstruct
    public void text() {
        System.out.println("init");
    }


    @Override
    public ServerResponse<Integer> getSerialCount() {
        //todo 需要进行权限验证，避免纵向越权或者洪水攻击
        List<String> serialList = sensorRegisterMapper.selectSerialList();
        //这里我偷懒了。先用下面的mapper，再通过size获取大小。（其实应该分开的，降低耦合，同时降低消耗）
        return ServerResponse.createBySuccess(serialList.size());
    }

    @Override
    public ServerResponse<List<String>> getSerialList() {
        //todo 需要进行权限验证，避免纵向越权或者洪水攻击
        List<String> serialList = sensorRegisterMapper.selectSerialList();
        return ServerResponse.createBySuccess(serialList);
    }

    @Override
    public ServerResponse<Integer> getSensorCount() {
        int result = sensorRegisterMapper.countSensor();
        if (result == 0) {
            return ServerResponse.createByErrorMessage("there is no sensor running");
        }
        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<List<SensorRegister>> getSensorList() {
        //todo 需要进行权限验证，避免纵向越权或者洪水攻击
        List<SensorRegister> sensorRegisterList = sensorRegisterMapper.selectSensorList();
        return ServerResponse.createBySuccess(sensorRegisterList);
    }

    @Override
    public ServerResponse<List<SensorRegister>> getSensorListByPort(String port) {
        //todo 需要进行权限验证，避免纵向越权或者洪水攻击
        List<SensorRegister> sensorRegisterList = sensorRegisterMapper.selectSensorListByPort(port);
        return ServerResponse.createBySuccess(sensorRegisterList);
    }

    @Override
    public ServerResponse<SensorRegister> getSensor(String port, String address) {
        //TODO_FINISHED 现在需要解决这里mapper错误的问题。
        //TODO_FINISHED 只能获取COM1&01的数据
        //原因是，数据库数据的create_time字段为0000这样的初始数值，导致ibatis出错
        SensorRegister sensorRegister = sensorRegisterMapper.selectByPortAndAddress(port, address);
        if (sensorRegister == null) {
            return ServerResponse.createByErrorMessage("can't find the sensor on this position:{ " + "  port:" + port + "  address:" + address + " }");
        }
        return ServerResponse.createBySuccess(sensorRegister);
    }

    @Override
    public ServerResponse<SensorRegister> getSensor(int id) {
        SensorRegister sensorRegister = sensorRegisterMapper.selectByPrimaryKey(1);
        if (sensorRegister == null) {
            return ServerResponse.createByErrorMessage("can't find the sensor by this id:" + id);
        }
        return ServerResponse.createBySuccess(sensorRegister);
    }

    @Override
    //特定传感器设置昵称（昵称要保证唯一性）
    public ServerResponse setNickname(int id, String nickname) {

        ServerResponse validResponse = this.checkValid(nickname, Const.NICKNAME);
        if (!checkValid(nickname, Const.NICKNAME).isSuccess()) {
            return validResponse;
        }
        SensorRegister sensorRegister = sensorRegisterMapper.selectByPrimaryKey(id);
        sensorRegister.setNickName(nickname);
        int resultCount = sensorRegisterMapper.updateByPrimaryKeySelective(sensorRegister);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("昵称更新失败");
        }

        return ServerResponse.createByErrorMessage("昵称更新成功");
    }

    @Override
    public ServerResponse getSensorId(String nickname) {
        //isBlank(" ")=true
        if (StringUtils.isBlank(nickname) && nickname == "*") {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        SensorRegister sensorRegister = null;
        try {
            System.out.println("ibatis pre_start");
            System.out.println(sensorRegisterMapper.toString());
            System.out.println("ibatis start");
            sensorRegister = sensorRegisterMapper.selectByNickname(nickname);
            System.out.println("ibatis end");
        } catch (Exception e) {
            System.out.println("ibatis exception: " + e);
        }

        if (sensorRegister == null) {
            return ServerResponse.createByErrorMessage("该昵称不存在对应id，请验证昵称！");
        }
        return ServerResponse.createBySuccess(sensorRegister);
    }

    @Override
    public ServerResponse insertSensor(SensorRegister sensorRegister) {
        String port = sensorRegister.getPort();
        String address = sensorRegister.getAddress();
        System.out.println("port:" + port + "   address:" + address);
        ServerResponse validResponse = checkValidPortAndAddress(port, address);
        if (validResponse.isSuccess()) {
            int resultCount = sensorRegisterMapper.insertSelective(sensorRegister);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("传感器注册失败");
            } else {
                return ServerResponse.createByErrorMessage("传感器注册成功");
            }
        }
        return validResponse;
    }

    @Override
    public ServerResponse updateSensor(SensorRegister sensorRegister) {
        String port = sensorRegister.getPort();
        String address = sensorRegister.getAddress();
        String nickname = sensorRegister.getNickName();
        ServerResponse validResponse = checkValidPortAndAddress(port, address);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        validResponse = this.checkValid(nickname, Const.NICKNAME);
        if (!checkValid(nickname, Const.NICKNAME).isSuccess()) {
            return validResponse;
        }

        //业务代码
        //这里的assemble可以通过函数，也可以通过构造函数，更可以通过改造sensorRegister
        SensorRegister updateSensorRegister = new SensorRegister();
        updateSensorRegister.setId(sensorRegister.getId());
        updateSensorRegister.setNickName(sensorRegister.getNickName());
        updateSensorRegister.setPort(sensorRegister.getPort());
        updateSensorRegister.setAddress(sensorRegister.getAddress());
        updateSensorRegister.setType(sensorRegister.getType());
        updateSensorRegister.setModel(sensorRegister.getModel());
        updateSensorRegister.setZero(sensorRegister.getZero());
        updateSensorRegister.setBaudrate(sensorRegister.getBaudrate());
        updateSensorRegister.setRemake(sensorRegister.getRemake());

        int resultCount = sensorRegisterMapper.updateByPrimaryKeySelective(updateSensorRegister);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("数据更新成功");
        }
        return ServerResponse.createByErrorMessage("数据更新失败");
    }

    @Override
    public ServerResponse deleteSensor(int id) {

        //删除操作必须十分慎重    //包括权限验证等   //可通过业务层，以及前端，进行删除的确认。不过在此系统中，增删由系统内部实现，不对外开放
        int resultCount = sensorRegisterMapper.deleteByPrimaryKey(id);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("数据删除成功");
        }
        return ServerResponse.createByErrorMessage("数据删除失败");
    }


    //todo 将下面两个checkValid()函数合并。
    private ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //目前种类较少，采用if
            if (Const.NICKNAME.equals(type)) {
                int resultCount = sensorRegisterMapper.checkNickname(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("昵称已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    private ServerResponse checkValidPortAndAddress(String port, String address) {
        if (StringUtils.isNotBlank(port) && StringUtils.isNotBlank(address)) {
            int resutlCount = sensorRegisterMapper.checkPortAndAdderss(port, address);
            if (resutlCount > 0) {
                return ServerResponse.createByErrorMessage("在该串口该地址，已经存在传感器");
            }
            return ServerResponse.createBySuccessMessage("校验成功");
        }
        return ServerResponse.createByErrorMessage("参数错误");
    }


}
