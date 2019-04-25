package com.renewable.gateway.controller.portal;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.SensorRegister;
import com.renewable.gateway.service.IRegisteredInfoService;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
@Controller
@RequestMapping("/sensor/register/")
public class SensorRegisterController {

    @Autowired
    private IRegisteredInfoService iregisteredInfoService;

    //获取串口数量
    @RequestMapping(value = "get_serial_count.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Integer> getSerialCount() {
        return iregisteredInfoService.getSerialCount();
    }

    //获取所有串口
    @RequestMapping(value = "get_serial_list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<String>> getSerialList() {
        return iregisteredInfoService.getSerialList();
    }

    //获取传感器数量
    @RequestMapping(value = "get_sensor_count.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Integer> getSensorCount() {
        return iregisteredInfoService.getSensorCount();
    }

    //获取传感器列表
    @RequestMapping(value = "get_sensor_list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<SensorRegister>> getSensorList() {
        return iregisteredInfoService.getSensorList();
    }

    //获取特定串口上所有传感器
    @RequestMapping(value = "get_sensor_list_port.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<SensorRegister>> getSensor(String port) {
        return iregisteredInfoService.getSensorListByPort(port);
    }

    @RequestMapping(value = "get_sensor.do", method = RequestMethod.GET)
    @ResponseBody
    //获取特定传感器（通过prot和address）
    public ServerResponse<SensorRegister> getSensor(String port, String address) {
        if (port == null) {
            return ServerResponse.createByErrorMessage("串口号为空");
        }
        if (address == null) {
            return ServerResponse.createByErrorMessage("传感器地址为空");
        }
        return iregisteredInfoService.getSensor(port, address);
    }

    //获取特定传感器（通过id）
    @RequestMapping(value = "get_sensor_id.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<SensorRegister> getSensor(int id) {
        return iregisteredInfoService.getSensor(id);
    }


    //特定传感器设置昵称 （昵称要保证唯一性）
    @RequestMapping(value = "set_nickname.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setNickname(@NotNull int id, String nickname) {
        if (nickname == null) {
            return ServerResponse.createByErrorMessage("nickname is null");
        }
//        return ServerResponse.createBySuccessMessage("already set the nickname of the No."+id+" sensor");
        return iregisteredInfoService.setNickname(id, nickname);
    }

    //根据昵称，返回对应id
    @RequestMapping(value = "get_id_nickname.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getSensorId(String nickname) {
        if (nickname == null) {
            return ServerResponse.createByErrorMessage("nickname is null");
        }
//        return ServerResponse.createBySuccessMessage("already set the nickname of the No."+id+" sensor");
        return iregisteredInfoService.getSensorId(nickname);
    }


    //接下来的接口只是为了内部测试，实际上线后并不对外开放。

    //插入新的传感器（会验证昵称，组合键(数据库尚未设置，port&&address）
    @RequestMapping(value = "insert_sensor.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse insertSensor(SensorRegister sensorRegister) {
        //只是为了测试，不做深度验证
        return iregisteredInfoService.insertSensor(sensorRegister);
    }

    //更新传感器
    @RequestMapping(value = "update_sensor.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateSensor(int id, SensorRegister sensorRegister) { //SpringMVC的参数对象注入
        //只是为了测试，不做深度验证
        return iregisteredInfoService.updateSensor(sensorRegister);
    }

    //更新传感器
    @RequestMapping(value = "delete_sensor.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse deleteSensor(int id) {
        //只是为了测试，不做深度验证
        return iregisteredInfoService.deleteSensor(id);
    }


}
