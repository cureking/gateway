package com.renewable.gateway.controller.portal;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.service.ISensorService;
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
@RequestMapping("/sensor/")
public class SensorController {

	@Autowired
	private ISensorService iSensorService;

	@RequestMapping(value = "list.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse listSensor(){
		return iSensorService.listSensor();
	}

	@RequestMapping(value = "get_by_id.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse getSensorById(@RequestParam(value = "sensorId", defaultValue = "") Integer sensorId){
		return iSensorService.getSensorById(sensorId);
	}

}
