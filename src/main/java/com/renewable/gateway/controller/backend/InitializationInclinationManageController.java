package com.renewable.gateway.controller.backend;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.InitializationInclination;
import com.renewable.gateway.service.IInitializationInclinationService;
import com.renewable.gateway.service.ITerminalService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Description：
 * @Author: jarry
 */
@Controller
@RequestMapping("/manage/initialization/inclination/")
public class InitializationInclinationManageController {

	@Autowired
	private IInitializationInclinationService iInitializationInclinationService;

	@RequestMapping(value = "list.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse listInitializationInclination() {
		return iInitializationInclinationService.listInitializationInclination();
	}

	@RequestMapping(value = "update.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse updateInitializationInclination(@RequestBody InitializationInclination initializationInclination) {
		return iInitializationInclinationService.updateEnterprise(initializationInclination);
	}

	@RequestMapping(value = "get_by_sensor_register_id.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse getInitializationInclinationBySensorRegisterId(@RequestParam("sensorRegisterId") Integer sensorRegisterId) {
		return iInitializationInclinationService.getInitializationInclinationBySensorRegisterId(sensorRegisterId);
	}
}
