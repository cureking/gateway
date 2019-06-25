package com.renewable.gateway.controller.portal;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.Inclination;
import com.renewable.gateway.service.IInclinationService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description：
 * @Author: jarry
 */
@Controller
//RequestMapping注解，将其下（class Controller）的请求全部统一到/sensor/这一命名空间中。
@RequestMapping("/inclination/")
public class InclinationController {
	@Autowired
	private IInclinationService iInclinationService;


	//test
	//根据传感器id，pageHelper参数获取对应监控数据  //采用pageHelper技术
	@RequestMapping(value = "data_2_db.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse data2DB() {
		Inclination inclination = new Inclination();
		inclination.setAngleX(10.0);
		inclination.setAngleY(10.0);
		inclination.setTemperature(10.0);
		return iInclinationService.inclinationData2DB(inclination);
	}

	@RequestMapping(value = "list_by_page.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse listInclinationData(@Param(value = "pageNum") Integer pageNum, @Param(value = "pageSize") Integer pageSize) {

		return iInclinationService.listInclinationData(pageNum, pageSize);
	}


	@RequestMapping(value = "list_by_time.do", method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse listInclinationDataByTime(@Param(value = "pageNum") Integer pageNum, @Param(value = "pageSize") Integer pageSize, String startTime, String endTime) {

		return iInclinationService.listInclinationDataByTime(startTime, endTime);
	}
}
