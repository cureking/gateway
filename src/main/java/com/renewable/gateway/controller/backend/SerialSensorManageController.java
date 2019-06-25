package com.renewable.gateway.controller.backend;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.SerialSensor;
import com.renewable.gateway.service.ISerialSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description：
 * @Author: jarry
 */
@Controller
@RequestMapping("/manage/sensor/serial/")
public class SerialSensorManageController {

	@Autowired
	private ISerialSensorService iSerialSensorService;

	@RequestMapping(value = "update_by_user.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse updateByUser(@RequestBody SerialSensor serialSensor){
		return iSerialSensorService.updateByUser(serialSensor);
	}

	/**
	 * 重新获取串口信息，并于中控室注册
	 * @return
	 */
	@RequestMapping(value = "refresh.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse refresh(){
		return iSerialSensorService.refresh();
	}
}
