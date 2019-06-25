package com.renewable.gateway.controller.portal;

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
@RequestMapping("/sensor/serial/")
public class SerialSensorController {

	@Autowired
	private ISerialSensorService iSerialSensorService;

	@RequestMapping(value = "list.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse listSerialSensor(){
		return iSerialSensorService.list();
	}
}
