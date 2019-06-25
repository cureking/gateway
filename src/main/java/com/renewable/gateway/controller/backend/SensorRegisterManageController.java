package com.renewable.gateway.controller.backend;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.SensorRegister;
import com.renewable.gateway.service.ISensorRegisterService;
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
@RequestMapping("/manage/sensor/register/")
public class SensorRegisterManageController {

	@Autowired
	private ISensorRegisterService iSensorRegisterService;

	@RequestMapping(value = "update.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse updateSensorRegister(@RequestBody SensorRegister sensorRegister){
		return iSensorRegisterService.updateEnterprise(sensorRegister);
	}
}
