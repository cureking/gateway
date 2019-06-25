package com.renewable.gateway.controller.portal;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.service.ISensorRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description：
 * @Author: jarry
 */
@Controller
@RequestMapping("/sensor/register/")
public class SensorRegisterController {

	@Autowired
	private ISensorRegisterService iSensorRegisterService;

	@RequestMapping(value = "list.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse listSensorRegister(){
		return iSensorRegisterService.listSensorRegister();
	}

	@RequestMapping(value = "get_by_id.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse getSensorRegisterById(@RequestParam(value = "sensorRegisterId",defaultValue = "") Integer sensorRegisterId){
		return iSensorRegisterService.getSensorRegisterById(sensorRegisterId);
	}
}
