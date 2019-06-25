package com.renewable.gateway.controller.backend;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.Sensor;
import com.renewable.gateway.service.ISensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Description：
 * @Author: jarry
 */
@Controller
@RequestMapping("/manage/sensor/")
public class SensorManageController {

	@Autowired
	private ISensorService iSensorService;

	@RequestMapping(value = "update.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse updateSensor(@RequestBody Sensor sensor){
		return iSensorService.updateSensor(sensor);
	}

	@RequestMapping(value = "insert.do", method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse insertSensor(@RequestBody Sensor sensor){
		return iSensorService.insertSensor(sensor);
	}

}
